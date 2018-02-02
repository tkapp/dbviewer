package net.teekee.dbexplorer.db;

import net.teekee.dbexplorer.domain.Database;

/**
 * Connecter for MySQL. 
 */
public class MySQLConnecter implements Connecter {

	/**
	 * @see net.teekee.dbexplorer.db.Connecter#getConnection(Database)
	 */
	@Override
	public String createUrl(Database database) {
		String url = "jdbc:mysql://" + database.host + ":" + database.port + "/" + database.database + "?useUnicode=true&characterEncoding=" + database.charset;
		return url;
	}
}
