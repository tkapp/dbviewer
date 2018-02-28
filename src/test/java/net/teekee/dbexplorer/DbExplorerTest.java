package net.teekee.dbexplorer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.teekee.db.DbUtils;
import net.teekee.dbexplorer.constant.AttributeNames;
import net.teekee.dbexplorer.constant.ParameterNames;
import net.teekee.dbexplorer.constant.PropertyConstant;
import net.teekee.dbexplorer.db.DatabaseEngine;
import net.teekee.dbexplorer.db.MySqlConnecter;
import net.teekee.dbexplorer.domain.Column;
import net.teekee.dbexplorer.domain.Context;
import net.teekee.dbexplorer.test.RequestTestWrapper;
import net.teekee.dbexplorer.test.ResponseTestWrapper;
import spark.HaltException;

/**
 * DbExplorer test class.
 */
public class DbExplorerTest {

  /** request object for test. */
  private RequestTestWrapper request;

  /** response object for test. */
  private ResponseTestWrapper response;

  /** temporary value. */
  private String db;

  /** temporary value. */
  private String contexts;

  @Before
  public void before() {
    request = new RequestTestWrapper();
    response = new ResponseTestWrapper();

    Context context = new Context(DatabaseEngine.MySql.identifier);
    context.host = "localhost"; // TODO from properties
    context.port = "3306";
    context.database = "tpcc";
    context.user = "tpcc";
    context.password = "tpcc";
    context.charset = "UTF-8";

    MySqlConnecter connecter = new MySqlConnecter();
    Connection connection = connecter.getConnection(context);

    request.attribute(AttributeNames.CONNECTION, connection);
    request.attribute(AttributeNames.CONTEXT, context);

    db = PropertyConstant.DB;
    contexts = PropertyConstant.CONTEXTS;

    PropertyConstant.DB = "test.properties";
  }

  @After
  public void after() throws Exception {
    PropertyConstant.DB = db;
    PropertyConstant.CONTEXTS = contexts;
    
    Connection connection = (Connection) request.attribute(AttributeNames.CONNECTION);
    connection.rollback();
    connection.close();
  }

  @Test
  public void getIndex() throws Exception {

    Object handle = DbExplorer.getIndex.handle(request, response);

    assertEquals("can get json.", "[\"test\",\"sample\",\"user\"]", handle.toString());
  }

  @Test
  public void getIndex_not_exist_property_file() throws Exception {

    PropertyConstant.DB = "abc";
    Object handle = DbExplorer.getIndex.handle(request, response);

    assertEquals("can get empty list json.", "[]", handle.toString());
  }

  @Test
  public void getIndex_not_exist_property_value() throws Exception {

    PropertyConstant.CONTEXTS = "abc";
    Object handle = DbExplorer.getIndex.handle(request, response);

    assertEquals("can get empty list json.", "[]", handle.toString());
  }

  @Test
  public void getObjedcts() throws Exception {

    String json = (String) DbExplorer.getObjects.handle(request, response);
    Context actual = new Gson().fromJson(json, Context.class);

    assertTrue("can get table list.", actual.tables.size() > 0);
    assertTrue("can get empty view list.", actual.views.size() == 0);
  }

  @Test
  public void getObjects_not_exist_context() throws Exception {

    Context context = (Context) request.attribute(AttributeNames.CONTEXT);
    context.database = "abc";

    String json = (String) DbExplorer.getObjects.handle(request, response);
    Context actual = new Gson().fromJson(json, Context.class);

    assertTrue("can get empty table list.", actual.tables.size() == 0);
    assertTrue("can get empty view list.", actual.views.size() == 0);
  }

  @Test
  public void getObjects_not_exist_database() throws Exception {

    Context context = (Context) request.attribute(AttributeNames.CONTEXT);
    context.database = "abc";

    String json = (String) DbExplorer.getObjects.handle(request, response);
    Context actual = new Gson().fromJson(json, Context.class);

    assertTrue("can get empty table list.", actual.tables.size() == 0);
    assertTrue("can get empty view list.", actual.views.size() == 0);
  }

  @Test
  public void getColumns() throws Exception {

    request.params(ParameterNames.Object, "warehouse");

    String json = (String) DbExplorer.getColumns.handle(request, response);
    Column[] actual = new Gson().fromJson(json, TypeToken.getArray(Column.class).getType());

    assertTrue("can get column list", actual.length > 0);
  }

  // TODO database_is_not_exist

  @Test(expected = HaltException.class)
  public void getColumns_table_is_not_exist() throws Exception {

    request.params(ParameterNames.Object, "abcde");

    DbExplorer.getColumns.handle(request, response);

    fail();
  }

  @Test
  public void postExecute_select() throws Exception {

    Context context = (Context) request.attribute(AttributeNames.CONTEXT);
    context.database = "mysql";
    request.params(ParameterNames.Object, "abcde");
    request.params("sql", " Select * from warehouse where w_id = 1");

    String json = (String) DbExplorer.postExecute.handle(request, response);
    List actual = new Gson().fromJson(json, List.class);

    assertTrue("can get record.", actual.size() == 1);
    
    Map warehouse = (Map) actual.get(0);
    assertEquals("can get record.", "4wX8iIzfq", warehouse.get("w_name"));
  }

  @Test
  public void postExecute_update() throws Exception {

    String expect = "t_" + (int)(Math.random() * 1000);
    
    Context context = (Context) request.attribute(AttributeNames.CONTEXT);
    context.database = "mysql";
    request.params(ParameterNames.Object, "abcde");
    request.params("sql", String.format(" Update warehouse set w_name = '%s' where w_id = 1", expect));

    String json = (String) DbExplorer.postExecute.handle(request, response);
    Map object = new Gson().fromJson(json, Map.class);

    double actual = (Double) object.get("result");
    assertTrue("update several records.", actual == 1);
    
    Connection connection = (Connection) request.attribute(AttributeNames.CONNECTION);
    Map<String, Object> warehouse = DbUtils.selectOne(connection, "select * from warehouse where w_id = 1");
    
    assertEquals("warehouse name changed.", expect, warehouse.get("w_name"));
  }
}
