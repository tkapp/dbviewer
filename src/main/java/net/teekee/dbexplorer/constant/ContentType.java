package net.teekee.dbexplorer.constant;

public enum ContentType {

	APPLICATION_JSON("application/json"),

	;
	
	private ContentType(String name) {
		this.name = name;
	}
	
	public String name;
}
