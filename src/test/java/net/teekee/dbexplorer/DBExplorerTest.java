package net.teekee.dbexplorer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.teekee.dbexplorer.constant.AttributeNames;
import net.teekee.dbexplorer.constant.ParameterNames;
import net.teekee.dbexplorer.constant.PropertyConstant;
import net.teekee.dbexplorer.db.DatabaseEngine;
import net.teekee.dbexplorer.db.MySQLConnecter;
import net.teekee.dbexplorer.domain.Column;
import net.teekee.dbexplorer.domain.Context;
import net.teekee.dbexplorer.test.RequestTestWrapper;
import net.teekee.dbexplorer.test.ResponseTestWrapper;
import spark.HaltException;

/**
 * DBExplorer test class.
 */
public class DBExplorerTest {

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
    context.host = "localhost";
    context.port = "3306";
    context.database = "mysql";
    context.user = "utest";
    context.password = "unittest";
    context.charset = "UTF-8";

    MySQLConnecter connecter = new MySQLConnecter();
    Connection connection = connecter.getConnection(context);

    request.attribute(AttributeNames.CONNECTION, connection);
    request.attribute(AttributeNames.CONTEXT, context);

    db = PropertyConstant.DB;
    contexts = PropertyConstant.CONTEXTS;

    PropertyConstant.DB = "test.properties";
  }

  @After
  public void after() {
    PropertyConstant.DB = db;
    PropertyConstant.CONTEXTS = contexts;
  }

  @Test
  public void getIndex() throws Exception {

    Object handle = DBExplorer.getIndex.handle(request, response);

    assertEquals("can get json.", "[\"test\",\"sample\",\"user\"]", handle.toString());
  }

  @Test
  public void getIndex_not_exist_property_file() throws Exception {

    PropertyConstant.DB = "abc";
    Object handle = DBExplorer.getIndex.handle(request, response);

    assertEquals("can get empty list json.", "[]", handle.toString());
  }

  @Test
  public void getIndex_not_exist_property_value() throws Exception {

    PropertyConstant.CONTEXTS = "abc";
    Object handle = DBExplorer.getIndex.handle(request, response);

    assertEquals("can get empty list json.", "[]", handle.toString());
  }

  @Test
  public void getObjects() throws Exception {

    Context context = (Context) request.attribute(AttributeNames.CONTEXT);
    context.database = "mysql";

    String json = (String) DBExplorer.getObjects.handle(request, response);
    Context actual = new Gson().fromJson(json, Context.class);

    assertTrue("can get table list.", actual.tables.size() > 0);
    assertTrue("can get empty view list.", actual.views.size() == 0);
  }

  @Test
  public void getObjects_not_exist_context() throws Exception {

    Context context = (Context) request.attribute(AttributeNames.CONTEXT);
    context.database = "abc";

    String json = (String) DBExplorer.getObjects.handle(request, response);
    Context actual = new Gson().fromJson(json, Context.class);

    assertTrue("can get empty table list.", actual.tables.size() == 0);
    assertTrue("can get empty view list.", actual.views.size() == 0);
  }

  @Test
  public void getObjects_not_exist_database() throws Exception {

    Context context = (Context) request.attribute(AttributeNames.CONTEXT);
    context.database = "abc";

    String json = (String) DBExplorer.getObjects.handle(request, response);
    Context actual = new Gson().fromJson(json, Context.class);

    assertTrue("can get empty table list.", actual.tables.size() == 0);
    assertTrue("can get empty view list.", actual.views.size() == 0);
  }

  @Test
  public void getColumns() throws Exception {

    Context context = (Context) request.attribute(AttributeNames.CONTEXT);
    context.database = "mysql";
    request.params(ParameterNames.TABLE, "user");

    String json = (String) DBExplorer.getColumns.handle(request, response);
    Column[] actual = new Gson().fromJson(json, TypeToken.getArray(Column.class).getType());

    assertTrue("can get column list", actual.length> 0);
  }

  // TODO database_is_not_exist
  
  @Test(expected = HaltException.class)
  public void getColumns_table_is_not_exist() throws Exception {

    Context context = (Context) request.attribute(AttributeNames.CONTEXT);
    context.database = "mysql";
    request.params(ParameterNames.TABLE, "abcde");

    DBExplorer.getColumns.handle(request, response);

    fail();
  }
}
