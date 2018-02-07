package net.teekee.db;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Utilities for DB access.
 */
public class DBUtils {

  /**
   * Constructor.
   */
  private DBUtils() {
  }

  /**
   * execute query and convert to Map object.
   *
   * @param connection DB connection.
   * @param query SQL query.
   * @param params bind parameter.
   * @return entity list. if query result is empty, return empty list.
   */
  public static List<Map<String, Object>> select(final Connection connection, final String query, final Object... params) {

    return select(connection, rs -> {

      try {

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        return convertToMap(rs, metaData, columnCount);

      } catch (SQLException e) {
        throw new RuntimeException("Can't execute query. query=" + query, e);
      }

    }, query, params);
  }

  /**
   * execute query and convert to object.
   * 
   * @param connection DB connection.
   * @param factory factory.
   * @param query SQL query.
   * @param params bind parameter.
   * @return entity list. if query result is empty, return empty list.
   */
  public static <T> List<T> select(Connection connection, Function<ResultSet, T> factory, String query, Object... params) {

    try (PreparedStatement statement = connection.prepareStatement(query)) {

      setParams(statement, params);

      try (ResultSet rs = statement.executeQuery()) {

        List<T> result = new ArrayList<>();

        while (rs.next()) {

          result.add(factory.apply(rs));
        }

        return result;
      }
    } catch (SQLException e) {
      throw new RuntimeException("Can't execute query. query=" + query, e);
    }
  }

  /**
   * execute query and convert to Map object.
   *
   * @param connection DB connection.
   * @param query SQL query.
   * @param params bind parameter.
   * @return entity as Map.
   */
  public static Map<String, Object> selectOne(Connection connection, String query, Object... params) throws SQLException {

    return selectOne(connection, rs -> {

      try {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        Map<String, Object> item = convertToMap(rs, metaData, columnCount);
        return item;
      } catch (SQLException e) {
        throw new RuntimeException("Can't convert map.", e);
      }

    }, query, params);
  }

  /**
   * execute query and convert to Map object.
   *
   * @param connection DB connection.
   * @param factory object factory.
   * @param query SQL query.
   * @param params bind parameter.
   * @return entity.
   */
  public static <T> T selectOne(Connection connection, Function<ResultSet, T> factory, String query, Object... params) throws SQLException {

    try (PreparedStatement statement = connection.prepareStatement(query)) {

      setParams(statement, params);

      try (ResultSet rs = statement.executeQuery()) {

        if (rs.next()) {

          T item = factory.apply(rs);
          return item;

        } else {
          return null;
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Can't execute query. query=" + query, e);
    }
  }

  /**
   * execute query for count.
   * 
   * @param connection database connection.
   * @param query SQL query.
   * @param params bind parameters.
   * @return record count.
   */
  public static int count(Connection connection, String query, Object... params) {

    try (PreparedStatement statement = connection.prepareStatement(query)) {

      setParams(statement, params);

      try (ResultSet rs = statement.executeQuery()) {
        rs.next();
        return rs.getInt(1);
      }
    } catch (SQLException e) {
      throw new RuntimeException("Can't execute query. query=" + query, e);
    }
  }

  /**
   * Executes SQL that is DDL or DML.
   * 
   * @param connection connection.
   * @param sql sql.
   * @return record count that is executed.
   * @throws SQLException
   */
  public static int execute(Connection connection, String sql) throws SQLException {
    //
    return execute(connection, sql, new Object[0]);
  }

  public static int execute(Connection connection, String sql, Object... params) throws SQLException {
    //
    try (PreparedStatement statement = connection.prepareStatement(sql)) {

      setParams(statement, params);

      return statement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Can't execute sql. sql=" + sql, e);
    }
  }

  private static void setParams(PreparedStatement statement, Object... params) throws SQLException {
    //
    for (int i = 0; i < params.length; i++) {
      if (params[i] instanceof Integer || params[i].getClass() == int.class) {
        statement.setInt(i + 1, (int) params[i]);
      } else if (params[i] instanceof BigInteger) {
        statement.setInt(i + 1, ((BigInteger) params[i]).intValue());
      } else { // TODO
        statement.setString(i + 1, (String) params[i]);
      }
    }
  }

  private static Map<String, Object> convertToMap(ResultSet rs, ResultSetMetaData metaData, int columnCount) throws SQLException {
    Map<String, Object> item = new HashMap<>();

    for (int i = 0; i < columnCount; i++) {
      item.put(metaData.getColumnName(i + 1), rs.getObject(i + 1));
    }
    return item;
  }
}
