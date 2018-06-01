package org.binas.ws.it;

import org.binas.ws.BadInit_Exception;
import org.binas.ws.EmailExists_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.UserNotExists_Exception;
import org.binas.ws.UserView;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class GetCreditIT extends BaseIT{
	
	@Test
	public void sucess() throws EmailExists_Exception, InvalidEmail_Exception, UserNotExists_Exception {
		UserView user = binasClient.activateUser("utilizador@dominio");
		Assert.assertEquals(binasClient.getCredit(user.getEmail()), 10);		
	}
	
	
	@Test(expected = UserNotExists_Exception.class)
	public void nullTest() throws UserNotExists_Exception {
		binasClient.getCredit(null);
		
	}
	
	/*
	@Test(expected = InvalidEmail_Exception.class)
	public void invalidUser() throws EmailExists_Exception, InvalidEmail_Exception, UserNotExists_Exception {
		UserView user = binasClient.activateUser("utilizador@dominio");
		Assert.assertEquals(binasClient.getCredit("aaa@bbb"), 10);	
	}*/
	
	
	@Test(expected = UserNotExists_Exception.class)
	public void emptyTest() throws UserNotExists_Exception {
		binasClient.getCredit("      ");
	}
	
	@After
	public void tearDown() {
		binasClient.testClear();
	}
}

