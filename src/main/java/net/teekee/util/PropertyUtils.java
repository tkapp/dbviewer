package net.teekee.util;

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
    try {
      ResourceBundle bundle = ResourceBundle.getBundle(path);
      return bundle.getString(key);
    } catch (MissingResourceException e) {
      return null;
    }
  }
}
