package net.teekee.dbexplorer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.teekee.dbexplorer.constant.AttributeNames;
import net.teekee.dbexplorer.constant.PropertyConstant;
import net.teekee.dbexplorer.db.DatabaseEngine;
import net.teekee.dbexplorer.db.MySQLConnecter;
import net.teekee.dbexplorer.domain.Context;
import net.teekee.dbexplorer.domain.Table;
import net.teekee.dbexplorer.test.RequestTestWrapper;
import net.teekee.dbexplorer.test.ResponseTestWrapper;

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
		List<Table> actual =  new Gson().fromJson(json, TypeToken.getParameterized(List.class, Table.class).getType());
		
		assertTrue("can get json data.", actual.size() > 0);
	}
	
	@Test
	public void getObjects_not_exist_context() throws Exception {
		
		Context context = (Context) request.attribute(AttributeNames.CONTEXT);
		context.database = "abc";
		
		String json = (String) DBExplorer.getObjects.handle(request, response);
		List<Table> actual =  new Gson().fromJson(json, TypeToken.getParameterized(List.class, Table.class).getType());
		
		assertTrue("can get json data.", actual.size() == 0);
	}
	
	@Test
	public void getObjects_not_exist_database() throws Exception {
		
		Context context = (Context) request.attribute(AttributeNames.CONTEXT);
		context.database = "abc";
		
		String json = (String) DBExplorer.getObjects.handle(request, response);
		List<Table> actual =  new Gson().fromJson(json, TypeToken.getParameterized(List.class, Table.class).getType());
		
		assertTrue("can get json data.", actual.size() == 0);
	}
}
