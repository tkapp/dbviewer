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
	 * execute query and convert to Map object.
	 * 
	 * @param connection DB connection.
	 * @param query SQL query.
	 * @param params bind parameter.
	 * @return entity list. if query result is empty, return empty list.
	 */
	public static List<Map<String, Object>> select(Connection connection, String query, Object... params) {

		return select(connection, query, rs -> {

			try {

				ResultSetMetaData metaData = rs.getMetaData();
				int columnCount = metaData.getColumnCount();

				return convertToMap(rs, metaData, columnCount);
				
			} catch (SQLException e) {
				throw new RuntimeException("Can't execute query. query=" + query, e);
			}

		}, params);
	}

	/**
	 * execute query and convert to Map object.
	 * 
	 * @param connection DB connection.
	 * @param query SQL query.
	 * @param params bind parameter.
	 * @return entity list. if query result is empty, return empty list.
	 */
	public static <T> List<T> select(Connection connection, String query, Function<ResultSet, T> factory, Object... params) {

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

	public static Map<String, Object> selectOne(Connection connection, String query, Object... params) throws SQLException {

		try (PreparedStatement statement = connection.prepareStatement(query)) {

			setParams(statement, params);

			try (ResultSet rs = statement.executeQuery()) {

				ResultSetMetaData metaData = rs.getMetaData();
				int columnCount = metaData.getColumnCount();

				if (rs.next()) {

					Map<String, Object> item = convertToMap(rs, metaData, columnCount);
					return item;

				} else {
					return null;
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Can't execute query. query=" + query, e);
		}
	}

	public static int count(Connection connection, String query, Object... params) throws SQLException {

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
