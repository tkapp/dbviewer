package net.teekee.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import net.teekee.dbexplorer.DBExplorer;

/**
 * Utility for property file.
 */
public class PropertyUtils {

	/**
	 * get Properties object.
	 * 
	 * @param path property file path.
	 * @return Properties object.
	 */
	public static Properties getPropertyFile(String path) {

		File parent;
		try {
			URL resource = DBExplorer.class.getClassLoader().getResource(".");
			parent = new File(new URI(resource.toString()));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

		Properties p = new Properties();

		File file = new File(parent.getAbsolutePath() + "/" + path);
		if (file.exists()) {
			try (FileReader in = new FileReader(file)) {

				p.load(in);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		return p;
	}

	/**
	 * get property value.
	 * 
	 * @param path property file path.
	 * @param key property key.
	 * @return property value.
	 */
	public static String getProperty(String path, String key) {
		Properties p = getPropertyFile(path);
		return (String) p.get(key);
	}
}
