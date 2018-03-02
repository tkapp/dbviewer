package net.teekee.util;

import java.io.File;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Utility for property file.
 */
public class PropertyUtils {

  /**
   * get property value.
   * 
   * @param path property file path.
   * @param key property key.
   * @return property value.
   */
  public static String getProperty(final String path, final String key) {

    ResourceBundle bundle;

    try {
      bundle = ResourceBundle.getBundle(path);
    } catch (MissingResourceException e) {
      System.out.println("bundle is not found. " + path);
      try {
        File file = new File("./db.properties");
        file.createNewFile();
        return null;
      } catch (Exception ex) {
        throw new RuntimeException("can't create property file. please check permission.", e);
      }
    }

    try {
      return bundle.getString(key);
    } catch (MissingResourceException e) {
      System.out.println("cant find key -> " + key);
      return null;
    }

  }
}
