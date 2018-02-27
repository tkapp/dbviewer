package net.teekee.dbexplorer.constant;

/**
 * Content type.
 */
public enum ContentType {

  /** application/json */
  APPLICATION_JSON("application/json"),

  ;

  /**
   * Constractor.
   * 
   * @param name content type.
   */
  private ContentType(String name) {
    this.name = name;
  }

  /** content type. */
  public String name;
}
