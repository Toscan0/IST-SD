package pt.tecnico.sd;

import org.junit.*;

/**
 * Unit test for ConfigHelper.
 */
public class AppTest {

    @Test
    public void Test1()
    {
        assert( true );
    }

    @Test
    public void Test2()
    {
    	ConfigHelper helper =new ConfigHelper();
    	//a min value always needs to be configured
    	assert( ! helper.getConfigValue("min").equals(""));
    }

    @Test
    public void Test3()
    {
    	// This line will prevent the code from being deployed
    	//assert(false) ;
    }


}
