package net.teekee.dbexplorer.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

public class Column extends DatabaseObject {

	public static Function<ResultSet, Column> create = rs -> {
		
		try {
			Column column = new Column();
			column.name = rs.getString("name");
			
			return column;
		} catch (SQLException e) {
			throw new RuntimeException("Can't get data from resultset", e);
		}
	};

}
