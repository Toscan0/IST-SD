package pt.tecnico.sd;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigHelper {
	private Properties _properties;

	public ConfigHelper() {
		try {
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");	
	        _properties = new Properties();
	        _properties.load(inputStream);
	    } catch (IOException e) {
	        System.out.printf("Failed to load configuration: %s%n", e);
	    }
	}

	public void printSomeProperties() {
	    // load configuration properties
        System.out.printf("Loaded %d properties%n", _properties.size());

        String minValue = this.getConfigValue("min");
        System.out.printf("min: %s%n", minValue);

        String maxValue = this.getConfigValue("max");
        System.out.printf("max: %s%n", maxValue);
	}

	public String getConfigValue(String configName) {
        String value = _properties.getProperty(configName);
		return value;
	}
}
