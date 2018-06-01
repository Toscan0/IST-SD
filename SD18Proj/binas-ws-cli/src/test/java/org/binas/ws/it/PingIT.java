package org.binas.ws.it;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test suite
 */
public class PingIT extends BaseIT {

    // tests
    // assertEquals(expected, actual);

    // public String ping(String x)

    @Test
    public void pingEmptyTest() {
		assertNotNull(binasClient.testPing("test"));
    }
    
    @Test
	public void nullTest() {
		Assert.assertNotNull(binasClient.testPing(null));
	}
	
	@Test
	public void emptyStringTest() {
		Assert.assertNotNull(binasClient.testPing(""));
	}
	
	@After
	public void tearDown() {
		binasClient.testClear();
	}
}
