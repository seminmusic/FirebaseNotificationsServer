package ba.sema.app;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.concurrent.Future;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.json.JSONObject;


public class FirebaseHelper 
{
	public final static String FIREBASE_FCM_URL = "https://fcm.googleapis.com/fcm/send";
	
	public final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
	
	
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
//			notification.put("title", notificationTitle);
//			notification.put("text", notificationText);
//			notification.put("sound", "notification.mp3");
//			notification.put("icon", "ic_launcher_sema");
			notification.put("title", notificationTitle);
			notification.put("body", notificationText);
			notification.put("sound", "default");
			notification.put("color", "#9134C1");
			//
			JSONObject json = new JSONObject();
			json.put("to", deviceToken);
			json.put("notification", notification);
			
			System.out.println("Start: " + dateFormat.format(new java.util.Date()));
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(json.toString());
			wr.flush();
			wr.close();
			
			int code = conn.getResponseCode();
			String message = conn.getResponseMessage();
			System.out.println("End:   " + dateFormat.format(new java.util.Date()));
			
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
	
	public static String SendNotificationAsync(String serverKey, String deviceToken, String notificationTitle, String notificationText)
	{
		String result = "";
		
		CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault();
		try
		{
			boolean useProxy = Boolean.parseBoolean(PropertiesHelper.GetPropertyValue("Proxy_Enabled"));
			String proxyAddress = PropertiesHelper.GetPropertyValue("Proxy_Address");
			int proxyPort = Integer.parseInt(PropertiesHelper.GetPropertyValue("Proxy_Port"));
			
			httpClient.start();
			
			HttpPost request = new HttpPost(FIREBASE_FCM_URL);
			if (useProxy)
			{
				HttpHost proxy = new HttpHost(proxyAddress, proxyPort);
	            RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
	            request.setConfig(config);
			}
			
			request.setHeader("Authorization", "key=" + serverKey);
			request.setHeader("Content-Type", "application/json");
			
			JSONObject notification = new JSONObject();
			notification.put("title", notificationTitle);
			notification.put("text", notificationText);
			notification.put("sound", "notification.mp3");
			notification.put("icon", "ic_launcher_sema");
			//
			JSONObject json = new JSONObject();
			json.put("to", deviceToken);
			json.put("notification", notification);
			
			request.setEntity(new StringEntity(json.toString()));
			
			System.out.println("Async Start: " + dateFormat.format(new java.util.Date()));
			Future<HttpResponse> future = httpClient.execute(request, null);
            HttpResponse response = future.get();
            System.out.println("Async End:   " + dateFormat.format(new java.util.Date()));
            
            int responseCode = response.getStatusLine().getStatusCode();
            String responseMessage = response.getStatusLine().getReasonPhrase();
            System.out.println("Response: " + response.getStatusLine());
            
            if (responseCode == 200)
            {
            	result = "<html><font color='green'>Async - " + responseCode + " " + responseMessage + "</font></html>";
            }
            else
			{
				throw new Exception(responseMessage);
			}
		}
		catch (Exception e)
		{
			result = "<html><font color='red'>Async - " + e.getMessage() + "</font></html>";
		}
		finally
		{
			try
			{
				httpClient.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return result;
	}
}
