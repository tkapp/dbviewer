package net.teekee.dbexplorer.domain;

import java.util.List;

/**
 *  Table meta data.
 */
public class Table extends DatabaseObject {	
	
	public String comment;
	
	public List<Table> columns;
}
