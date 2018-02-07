package net.teekee.util;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;

/**
 * Test class for PropertyUtils.
 */
public class PropertyUtilsTest {

  @Test
  public void getPropertyFile_exist_file() {

    Properties p = PropertyUtils.getPropertyFile("test.properties");

    assertNotEquals("can get not empty properties.", "{}", p.toString());
  }

  @Test
  public void getPropertyFile_not_exist_file() {

    Properties p = PropertyUtils.getPropertyFile("non.properties");

    assertEquals("can get empty properties.", "{}", p.toString());
  }

  @Test
  public void getProperty_exist_property() {
    String actual = PropertyUtils.getProperty("test.properties", "exist");

    assertEquals("can get value.", "sample", actual);
  }

  @Test
  public void getProperty_not_exist_property() {
    String actual = PropertyUtils.getProperty("db.properties", "noexist");

    assertNull("can get null", actual);
  }

  @Test
  public void getProperty_not_exist_property_value() {
    String actual = PropertyUtils.getProperty("test.properties", "empty");

    assertEquals("can get empty string.", "", actual);
  }

}
