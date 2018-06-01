package org.binas.station.ws.it;

import static org.junit.Assert.assertEquals;

import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.BalanceView;
import org.binas.station.ws.UserNotFound_Exception;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GetBalanceIT extends BaseIT{
	private final static int X = 5;
	private final static int Y = 5;
	private final static int CAPACITY = 20;
	private final static int RETURN_PRIZE = 0;
	private static final int USER_POINTS = 10;

	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() {
	}

	@AfterClass
	public static void oneTimeTearDown() {
	}
	
	// initialization and clean-up for each test
	@Before
	public void setUp() throws BadInit_Exception {
		client.testClear();
		client.testInit(X, Y, CAPACITY, RETURN_PRIZE);
	}

	@After
	public void tearDown() {
	}
	
	//getBalance(String userEmail)
	@Test(expected = UserNotFound_Exception.class)
	public void getBalanceValidEmailButNullClient() throws BadInit_Exception, UserNotFound_Exception{
		BalanceView balanceView;
		balanceView = client.getBalance("sd.test@tecnico.ulisboa");
    }
	
	@Test(expected = UserNotFound_Exception.class)
	public void getBalanceInvalidEmailButNullClient() throws BadInit_Exception, UserNotFound_Exception{
		BalanceView balanceView;
		balanceView = client.getBalance("@tecnico.ulisboa");
    }
}
