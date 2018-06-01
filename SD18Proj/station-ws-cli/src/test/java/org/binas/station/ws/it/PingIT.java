package org.binas.station.ws.it;

import org.junit.Test;
import org.junit.Assert;

/**
 * Class that tests Ping operation
 */
public class PingIT extends BaseIT {

	// tests
	// assertEquals(expected, actual);

	// public String ping(String x)

	@Test
	public void pingEmptyTest() {
		Assert.assertNotNull(client.testPing("test"));
	}
	
	@Test
	public void nullTest() {
		Assert.assertNotNull(client.testPing(null));
	}
	
	@Test
	public void emptyStringTest() {
		Assert.assertNotNull(client.testPing(""));
	}
}
