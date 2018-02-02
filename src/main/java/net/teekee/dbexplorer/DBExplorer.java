package net.teekee.dbexplorer;

import static spark.Spark.*;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;

import net.teekee.db.DBUtils;
import net.teekee.dbexplorer.constant.ContentType;
import net.teekee.dbexplorer.domain.Database;
import net.teekee.util.PropertyUtils;
import spark.Filter;
import spark.Route;

/**
 * DBExplorer Start up class.
 */
public class DBExplorer {

	/**
	 * DBExplorer Main method.
	 */
	public static void main(String[] args) {
		port(8080);
		get("/", (request, response) -> "index\n");
		get("/foo", (request, response) -> "foooo!");
		get("/:keyword", (request, response) -> request.params("keyword") + "\n");
		get("/:keyword/", (request, response) -> "/" + request.params("keyword") + "\n");
		get("/:keyword/upper", (request, response) -> "/" + request.params("keyword").toUpperCase() + "\n");
		get("/bar", (request, response) -> "bar!");
	}

	/**
	 * DBExplorer Main method.
	 */
	public static void main2(String[] args) {

		port(8080);

		before("/:database/*", connectionFilter);

		get("/", getIndex);
		get("/:database/objects", getObjects);

		get("/:database/:table/columns/", (request, response) -> {
			return "";
		});

		get("/:database/:table/column", (request, response) -> {
			return "";
		});

		after((request, response) -> {
			Connection connection = (Connection) request.attribute(ParameterNames.CONNECTION);
			connection.commit();
		});

		afterAfter((request, response) -> {
			Connection connection = (Connection) request.attribute(ParameterNames.CONNECTION);
			connection.close();
		});

		exception(Exception.class, (exception, request, response) -> {
			
			try {
				exception.printStackTrace();
				Connection connection = (Connection) request.attribute(ParameterNames.CONNECTION);
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

	protected static Filter connectionFilter = (request, response) -> {
		
		String databaseName = request.params(ParameterNames.DATABASE);
		Database database = new Database(databaseName);

		if (database.host == null) {
			halt(404);
		}
		
		Connection connection = database.getConnection();
		request.attribute(ParameterNames.CONNECTION, connection);
		request.attribute(ParameterNames.DATABASE, database);
		
	};

	/**
	 * GET /.
	 * 
	 * show database list as json.
	 */
	protected static Route getIndex = (request, response) -> {

		String value = PropertyUtils.getProperty("db.properties", "databases");
		String[] databases = (StringUtils.isEmpty(value)) ? new String[0] : value.split(",");

		response.type(ContentType.APPLICATION_JSON.name);
		return new Gson().toJson(databases);
	};

	/**
	 * GET /database:/schema
	 */
	protected static Route getObjects = (request, response) -> {

		Connection connection = (Connection) request.attribute(ParameterNames.CONNECTION);
		Database database = (Database) request.attribute(ParameterNames.DATABASE_OBJECT);

		DBUtils.select(connection, "select * from information_schema.TABLES where ", database.database);

		return "";
	};

}
