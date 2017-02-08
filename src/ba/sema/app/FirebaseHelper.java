package ba.sema.app;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import org.json.JSONObject;


public class FirebaseHelper 
{
	public final static String FIREBASE_FCM_URL = "https://fcm.googleapis.com/fcm/send";
	
	public static String SendNotification(String serverKey, String deviceToken, String notificationTitle, String notificationText) 
	{
		String result = "";
		
		try 
		{
			URL url = new URL(FIREBASE_FCM_URL);
			
			boolean useProxy = Boolean.parseBoolean(PropertiesHelper.GetPropertyValue("Proxy_Enabled"));
			String proxyAddress = PropertiesHelper.GetPropertyValue("Proxy_Address");
			int proxyPort = Integer.parseInt(PropertiesHelper.GetPropertyValue("Proxy_Port"));
			// System.out.println("Proxy podaci:\n" + "Enabled: " + useProxy + "\nAdresa: " + proxyAddress + "\nPort: " + proxyPort);
			
			HttpURLConnection conn;
			if (useProxy)
			{
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyAddress, proxyPort));
				conn = (HttpURLConnection)url.openConnection(proxy);
			}
			else
			{
				conn = (HttpURLConnection)url.openConnection();
			}
			
			conn.setUseCaches(false);
			conn.setDoOutput(true);

			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "key=" + serverKey);
			conn.setRequestProperty("Content-Type", "application/json");
			
			JSONObject notification = new JSONObject();
			notification.put("title", notificationTitle);
			notification.put("text", notificationText);
			notification.put("sound", "notification.mp3");
			notification.put("icon", "ic_launcher_sema");
			//
			JSONObject json = new JSONObject();
			json.put("to", deviceToken);
			json.put("notification", notification);

			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(json.toString());
			wr.flush();
			wr.close();
			
			int code = conn.getResponseCode();
			String message = conn.getResponseMessage();
			if (code == 200) 
			{
				result = "<html><font color='green'>" + code + " " + message + "</font></html>";
			}
			else
			{
				throw new Exception(message);
			}
		} 
		catch (Exception e) 
		{
			System.out.println("GREŠKA: " + e.getMessage());
			e.printStackTrace();
			
			result = "<html><font color='red'>" + e.getMessage() + "</font></html>";
		}
		
		return result;
	}
}
