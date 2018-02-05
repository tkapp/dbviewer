package net.teekee.dbexplorer.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

/**
 *  Table meta data.
 */
public class Table extends DatabaseObject {	
	
	public static Function<ResultSet, Table> create = rs -> {
		
		try {
			Table table = new Table();
			table.name = rs.getString("name");
			table.comment = rs.getString("comment");
			
			return table;
		} catch (SQLException e) {
			throw new RuntimeException("Can't get data from resultset", e);
		}
	};
	
	/** table comment. */
	public String comment;
	
	/** column list. */
	public List<Table> columns;
	
}
