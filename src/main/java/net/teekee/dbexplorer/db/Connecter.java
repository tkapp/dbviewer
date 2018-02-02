package net.teekee.dbexplorer.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.teekee.dbexplorer.domain.Database;

/**
 * Database connector.
 */
public interface Connecter {

	/**
	 * connect to database.
	 * 
	 * @param database database connection settings.
	 * @return Database connection.
	 */
	default public Connection getConnection(Database database) {
		//
		try {
			//
			Class.forName(DatabaseKinds.MySql.driverName);

			String user = database.name;
			String password = database.password;

			String url = createUrl(database);

			Connection connection = DriverManager.getConnection(url, user, password);
			connection.setAutoCommit(false);

			return connection;

		} catch (ClassNotFoundException | SQLException e) {
			//
			throw new RuntimeException("SystemException", e);
		}
	}

	/**
	 * create url for database connect.
	 * 
	 * @param database database database connection settings.
	 * @return url for database connect.
	 */
	String createUrl(Database database);
}
