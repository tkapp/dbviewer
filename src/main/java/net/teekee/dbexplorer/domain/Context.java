package net.teekee.dbexplorer.domain;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.teekee.dbexplorer.db.Connecter;
import net.teekee.dbexplorer.db.DatabaseEngine;
import net.teekee.dbexplorer.db.MySQLConnecter;
import net.teekee.util.PropertyUtils;

/**
 * Database Connection settings.
 */
public class Context {

  /** DB connector. */
  private Connecter connecter;

  /** setting name. */
  public String name;

  /** DB port. */
  public String port;

  /** host name or host address. */
  public String host;

  /** user name for database login. */
  public String user;

  /** password for database login. */
  public String password;

  /** database name. */
  public String database;

  /** character set. */
  public String charset;

  public List<Table> tables;

  public List<View> views;

  /**
   * Constructor.
   * 
   * @param name setting name.
   */
  public Context(String name) {

    this.name = name;
    this.host = PropertyUtils.getProperty("db.properties", name + ".host");
    this.port = PropertyUtils.getProperty("db.properties", name + ".port");
    this.user = PropertyUtils.getProperty("db.properties", name + ".user");
    this.password = PropertyUtils.getProperty("db.properties", name + ".password");
    this.database = PropertyUtils.getProperty("db.properties", name + ".database");

    setDatabase(DatabaseEngine.MySql.identifier);
  }

  /**
   * set kind of database.
   * 
   * @param identifier DB identifier.
   */
  private void setDatabase(String identifier) {
    if (StringUtils.equals(identifier, DatabaseEngine.MySql.identifier)) {
      connecter = new MySQLConnecter();
    } else {
      throw new RuntimeException(identifier + " is not define.");
    }
  }

  /**
   * get database connection.
   * 
   * @return database connection.
   */
  public Connection getConnection() {
    return connecter.getConnection(this);
  }
}
