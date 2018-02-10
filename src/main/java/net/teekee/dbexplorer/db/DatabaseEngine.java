package net.teekee.dbexplorer.db;

/**
 * Database engine kind.
 */
public enum DatabaseEngine {

  /** MySQL. */
  MySql("MySQL", "com.mysql.jdbc.Driver"),

  /** MariaDB. */
  MariaDB("MariaDB", ""),

  /** PostgreSQL. */
  PostgreSQL("PostgreSQL", ""),

  /** Oracle. */
  Oracle("Oracle", ""),

  /** SQLServer. */
  SQLServer("SQLServer", ""),

  ;

  /**
   * Constructor.
   * 
   * @param identifier database engine identifier.
   * @param driverName jdbc driver class name.
   */
  DatabaseEngine(String identifier, String driverName) {
    this.identifier = identifier;
    this.driverName = driverName;
  }

  /** database kind identifier. */
  public final String identifier;

  /** jdbc driver class name. */
  public final String driverName;
}
