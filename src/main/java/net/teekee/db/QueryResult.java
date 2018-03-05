package net.teekee.db;

import java.util.List;
import java.util.Map;

public class QueryResult {

  public final List<String> header;
  
  public final List<Map<String, Object>> body;
  
  public QueryResult(List<String> header, List<Map<String, Object>> body) {
    this.header = header;
    this.body = body;
  }
}
