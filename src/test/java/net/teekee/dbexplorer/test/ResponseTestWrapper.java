package net.teekee.dbexplorer.test;

import spark.Response;

/**
 * Response wrapper for tests.
 */
public class ResponseTestWrapper extends Response {

  private String type;

  @Override
  public void type(String type) {
    this.type = type;
  }

  @Override
  public String type() {
    return type;
  }
}
