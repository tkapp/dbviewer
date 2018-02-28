package net.teekee.dbexplorer;

import static spark.Spark.after;
import static spark.Spark.afterAfter;
import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.port;
import static spark.Spark.post;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.client.api.Request;
import com.google.gson.Gson;
import net.teekee.db.DbUtils;
import net.teekee.dbexplorer.constant.AttributeNames;
import net.teekee.dbexplorer.constant.ContentType;
import net.teekee.dbexplorer.constant.ParameterNames;
import net.teekee.dbexplorer.constant.PropertyConstant;
import net.teekee.dbexplorer.domain.Column;
import net.teekee.dbexplorer.domain.Context;
import net.teekee.dbexplorer.domain.DatabaseObject;
import net.teekee.dbexplorer.domain.Table;
import net.teekee.util.PropertyUtils;
import spark.Route;

/**
 * DbExplorer Start up class.
 */
public final class DbExplorer {

  /**
   * DBExplorer Main method.
   */
  public static void main(String[] args) {

    port(8080);

    before("/:context/*", (request, response) -> {
      String contextName = request.params(ParameterNames.CONTEXT);
      Context context = new Context(contextName);

      if (context.host == null) {
        halt(404);
      }

      Connection connection = context.getConnection();
      request.attribute(AttributeNames.CONNECTION, connection);
      request.attribute(AttributeNames.CONTEXT, context);
    });

    get("/", getIndex);
    get("/:context/objects", getObjects);
    get("/:context/:object/columns", getColumns);
    post("/:context/execute", postExecute);

    after((request, response) -> {
      Connection connection = (Connection) request.attribute(AttributeNames.CONNECTION);
      connection.commit();
    });

    afterAfter((request, response) -> {
      Connection connection = (Connection) request.attribute(AttributeNames.CONNECTION);
      connection.close();
    });

    exception(Exception.class, (exception, request, response) -> {

      try {
        exception.printStackTrace();
        Connection connection = (Connection) request.attribute(AttributeNames.CONNECTION);
        connection.rollback();
        connection.close(); // TODO
        halt(500);
      } catch (SQLException e) {
        // TODO
      } finally {
        halt(500);
      }
    });
  }

  /**
   * GET "/".
   * display database list as json.
   */
  protected static Route getIndex = (request, response) -> {

    String value = PropertyUtils.getProperty(PropertyConstant.DB, PropertyConstant.CONTEXTS);
    String[] contexts = (StringUtils.isEmpty(value)) ? new String[0] : value.split(",");

    response.type(ContentType.APPLICATION_JSON.name);
    return new Gson().toJson(contexts);
  };

  /**
   * GET "/:context/objects".
   * display database objects in context as json.
   */
  protected static Route getObjects = (request, response) -> {

    Connection connection = (Connection) request.attribute(AttributeNames.CONNECTION);
    Context context = (Context) request.attribute(AttributeNames.CONTEXT);

    List<Table> tables = DbUtils.select(connection, Table.create, "SELECT table_name name, table_comment comment FROM information_schema.tables "
        + "WHERE table_type = 'BASE TABLE' and UPPER(table_schema) = ? order by name", context.database.toUpperCase());

    List<Table> views = DbUtils.select(connection, Table.create,
        "SELECT table_name name, table_comment comment FROM information_schema.tables WHERE table_type = 'VIEW' and UPPER(table_schema) = ? order by name",
        context.database.toUpperCase());

    Map<String, List<? extends DatabaseObject>> result = new HashMap<>();
    result.put("tables", tables);
    result.put("views", views);

    response.type(ContentType.APPLICATION_JSON.name);
    return new Gson().toJson(result);
  };

  /**
   * GET /:context/:object/columns.
   */
  protected static Route getColumns = (request, response) -> {

    Connection connection = (Connection) request.attribute(AttributeNames.CONNECTION);
    Context context = (Context) request.attribute(AttributeNames.CONTEXT);
    String objectName = request.params(ParameterNames.Object);

    Table table = DbUtils.selectOne(connection, Table.create,
        "SELECT table_name name, NULL comment FROM information_schema.tables " + "WHERE UPPER(table_schema) = ? AND UPPER(table_name) = ?",
        context.database.toUpperCase(), objectName.toUpperCase());
    if (table == null) {
      halt(404, "no such table or view.");
    }

    List<Column> columns = DbUtils.select(connection, Column.create,
        "SELECT column_name name, column_comment comment FROM information_schema.columns WHERE UPPER(table_schema) = ? AND UPPER(table_name) = ? ORDER BY ordinal_position",
        context.database.toUpperCase(), objectName.toUpperCase());

    response.type(ContentType.APPLICATION_JSON.name);
    return new Gson().toJson(columns);
  };

  protected final static Route postExecute = (request, response) -> {

    String sql = request.params("sql");
    if (StringUtils.isEmpty(sql)) {
      halt(500, "sql is required.");
    }

    String first = sql.trim().split(" ")[0];
    Connection connection = (Connection) request.attribute(AttributeNames.CONNECTION);

    if (StringUtils.equals(first.toUpperCase(), "SELECT")) {

      if (StringUtils.indexOf(sql, ";") >= 0) {
        halt(500, "dbe can execute only one sql. please remove \";\"."); // TODO
      }

      List<Map<String, Object>> result = DbUtils.select(connection, sql);

      response.type(ContentType.APPLICATION_JSON.name);
      return new Gson().toJson(result);

    } else {

      if (StringUtils.indexOf(sql, ";") >= 0) {
        halt(500, "dbe can execute only one sql. please remove \";\"."); // TODO
      }

      int result = DbUtils.execute(connection, sql);

      response.type(ContentType.APPLICATION_JSON.name);
      return String.format("{ result: %d}", result);
    }

  };
}
