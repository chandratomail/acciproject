package trivagoin;

import java.io.FileReader;
import java.util.Properties;

public class PropertiesLoader {

	static FileReader fileReader = null;

	public static String getProperty(String key) {
		String keyValue = null;
		try {
			fileReader = new FileReader("src//main///resources//config.properties");
			Properties properties = new Properties();
			properties.load(fileReader);
			keyValue = properties.getProperty(key);
			if(keyValue == null) {
				throw new Exception("***The specified key not found***");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return keyValue;
	}
}
