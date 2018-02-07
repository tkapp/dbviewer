package net.teekee.dbexplorer.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.teekee.dbexplorer.domain.Context;

/**
 * Database connector.
 */
public interface Connecter {

  /**
   * connect to database.
   * 
   * @param context database connection settings.
   * @return Database connection.
   */
  default public Connection getConnection(Context context) {
    //
    try {
      //
      Class.forName(DatabaseEngine.MySql.driverName);

      String user = context.user;
      String password = context.password;

      String url = createUrl(context);

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
   * @param context database connection settings.
   * @return url for database connect.
   */
  String createUrl(Context context);
}
