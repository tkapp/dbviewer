package net.teekee.dbexplorer.test;

import java.util.HashMap;
import java.util.Map;

import spark.Request;

/**
 * Request wrapper for tests.
 */
public class RequestTestWrapper extends Request {

  private Map<String, Object> attributes = new HashMap<>();

  private Map<String, String> params = new HashMap<>();

  @Override
  public void attribute(String key, Object value) {
    attributes.put(key, value);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T attribute(String key) {
    return (T) attributes.get(key);
  }

  public void params(String key, String value) {
    params.put(key, value);
  }
  
  public String params(String key) {
    return params.get(key);
  }

}
