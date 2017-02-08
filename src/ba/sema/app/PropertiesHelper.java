package ba.sema.app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertiesHelper 
{
	public static String GetPropertyValue(String propertyName) 
	{
		String propertyValue = null;
		InputStream inputStream = null;
		
		try 
		{
			Properties properties = new Properties();
			String propertiesFileName = "konfiguracija.properties";
 
			inputStream = PropertiesHelper.class.getClassLoader().getResourceAsStream(propertiesFileName);
			if (inputStream != null) 
			{
				properties.load(inputStream);
			} 
			else 
			{
				throw new FileNotFoundException("Property file '" + propertiesFileName + "' not found in the classpath.");
			}
			
			propertyValue = properties.getProperty(propertyName);
			if (propertyValue == null)
			{
				throw new NoSuchFieldException("Property with name '" + propertyName + "' does not exist.");
			}
		} 
		catch (Exception e) 
		{
			System.out.println("Exception: " + e);
		} 
		finally 
		{
			try 
			{
				inputStream.close();
			} 
			catch (IOException e) 
			{
				// e.printStackTrace();
			}
		}
		
		return propertyValue;
	}
}
