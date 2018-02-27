package net.teekee.dbexplorer;

import static spark.Spark.after;
import static spark.Spark.afterAfter;
import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.port;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
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
    get("/:context/:table/columns", getColumns);
    get("/:context//columns", getColumns);

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

    List<Table> tables = DbUtils.select(connection, Table.create,
        "SELECT table_name name, table_comment comment FROM information_schema.tables "
            + "WHERE table_type = 'BASE TABLE' and UPPER(table_schema) = ? order by name",
        context.database.toUpperCase());

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
   * GET /:context/:table/columns.
   */
  protected static Route getColumns = (request, response) -> {

    Connection connection = (Connection) request.attribute(AttributeNames.CONNECTION);
    Context context = (Context) request.attribute(AttributeNames.CONTEXT);
    String tableName = request.params(ParameterNames.TABLE);

    Table table = DbUtils.selectOne(connection, Table.create,
        "SELECT table_name name, NULL comment FROM information_schema.tables "
            + "WHERE table_type = 'BASE TABLE' AND UPPER(table_schema) = ? AND  UPPER(table_name) = ?",
        context.database.toUpperCase(), tableName.toUpperCase());
    if (table == null) {
      halt(404, "no such table.");
    }

    List<Column> columns = DbUtils.select(connection, Column.create,
        "SELECT column_name name, column_comment FROM information_schema.columns WHERE UPPER(table_schema) = ? AND UPPER(table_name) = ? ORDER BY name",
        context.database.toUpperCase(), tableName.toUpperCase());

    response.type(ContentType.APPLICATION_JSON.name);
    return new Gson().toJson(columns);
  };
}
