package net.teekee.dbexplorer.db;

import net.teekee.dbexplorer.domain.Context;

/**
 * Connecter for MySQL. 
 */
public class MySQLConnecter implements Connecter {

	/**
	 * @see net.teekee.dbexplorer.db.Connecter#getConnection(Context)
	 */
	@Override
	public String createUrl(Context context) {
		String url = "jdbc:mysql://" + context.host + ":" + context.port + "/" + context.database + "?useUnicode=true&characterEncoding=" + context.charset;
		return url;
	}
}
