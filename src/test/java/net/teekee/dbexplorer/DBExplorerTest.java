package net.teekee.dbexplorer;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import net.teekee.dbexplorer.test.RequestTestWrapper;
import net.teekee.dbexplorer.test.ResponseTestWrapper;

/**
 * DBExplorer test class.
 */
public class DBExplorerTest {

	private RequestTestWrapper request;
	
	private ResponseTestWrapper response;
	
	@Before
	public void before() {
		request = new RequestTestWrapper();
		response = new ResponseTestWrapper();		
	}
	
	@Test
	public void getIndex() throws Exception {
		
		Object handle = DBExplorer.getIndex.handle(request, response);
		
		assertEquals("can get json.", "[\"test\",\"sample\",\"user\"]", handle.toString());
	}
}
