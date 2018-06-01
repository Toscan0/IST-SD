package org.binas.ws.it;

import org.binas.ws.EmailExists_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.UserNotExists_Exception;
import org.binas.ws.UserView;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class ActivateUserIT extends BaseIT{
	
	@Test
	public void sucess() throws EmailExists_Exception, InvalidEmail_Exception {
		
			UserView userView = binasClient.activateUser("utilizador@dominio");
			
			Assert.assertEquals("utilizador@dominio", userView.getEmail());
			Assert.assertEquals(false, userView.isHasBina());
			Assert.assertEquals((Integer) 10, userView.getCredit());
		
	}
	
	
	@Test(expected = InvalidEmail_Exception.class)
	public void nullTest() throws EmailExists_Exception, InvalidEmail_Exception {
		binasClient.activateUser(null);
	}
	
	
	@Test(expected = InvalidEmail_Exception.class)
	public void invalidMail1() throws EmailExists_Exception, InvalidEmail_Exception {
		Assert.assertNotNull(binasClient.activateUser("utilizadordominio"));
	}
	
	@Test(expected = InvalidEmail_Exception.class)
	public void invalidMail2() throws EmailExists_Exception, InvalidEmail_Exception {
		Assert.assertNotNull(binasClient.activateUser("uti@lizador@domin@io"));
	}
	
	
	@Test(expected = InvalidEmail_Exception.class)
	public void emptyTest() throws EmailExists_Exception, InvalidEmail_Exception {
		Assert.assertNotNull(binasClient.activateUser("      "));	
	}
	
	@After
	public void tearDown() {
		binasClient.testClear();
	}
}
