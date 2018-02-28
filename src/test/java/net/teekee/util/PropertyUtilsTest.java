package net.teekee.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 * Test class for PropertyUtils.
 */
public class PropertyUtilsTest {

  @Test
  public void getProperty_exist_property() {
    String actual = PropertyUtils.getProperty("test", "exist");

    assertEquals("can get value.", "sample", actual);
  }

  @Test
  public void getProperty_not_exist_property_file() {
    String actual = PropertyUtils.getProperty("testabc", "");

    assertNull("can get null", actual);
  }

  @Test
  public void getProperty_not_exist_property_key() {
    String actual = PropertyUtils.getProperty("test", "noexist");

    assertNull("can get null", actual);
  }

  @Test
  public void getProperty_not_exist_property_value() {
    String actual = PropertyUtils.getProperty("test", "empty");

    assertEquals("can get empty string.", "", actual);
  }

}
