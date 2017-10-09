package ba.sema.app;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.HttpResponseStatus;
import org.asynchttpclient.Response;
import org.asynchttpclient.proxy.ProxyServer;
import org.json.JSONObject;


public class FirebaseHelper 
{
	public final static String FIREBASE_FCM_URL = "https://fcm.googleapis.com/fcm/send";
	
	public final static boolean PROXY_ENABLED = Boolean.parseBoolean(PropertiesHelper.GetPropertyValue("Proxy_Enabled"));
	public final static String PROXY_ADDRESS = PropertiesHelper.GetPropertyValue("Proxy_Address");
	public final static int PROXY_PORT = Integer.parseInt(PropertiesHelper.GetPropertyValue("Proxy_Port"));
	
	public final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
	
	
	public static String SendNotification(String serverKey, String deviceToken, String notificationTitle, String notificationText) 
	{
		String result = "";
		
		try 
		{
			URL url = new URL(FIREBASE_FCM_URL);
			
			HttpURLConnection conn;
			if (PROXY_ENABLED)
			{
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_ADDRESS, PROXY_PORT));
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
			// notification.put("title", notificationTitle);
			// notification.put("text", notificationText);
			// notification.put("sound", "notification.mp3");
			// notification.put("icon", "ic_launcher_sema");
			notification.put("title", notificationTitle);
			notification.put("body", notificationText);
			notification.put("sound", "default");
			notification.put("color", "#9134C1");
			//
			JSONObject json = new JSONObject();
			json.put("to", deviceToken);
			json.put("notification", notification);
			
			System.out.println("Start: " + dateFormat.format(new java.util.Date()));
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
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
	
	public static void SendNotificationAsync(String serverKey, String deviceToken, String notificationTitle, String notificationText) 
	{
		JSONObject notification = new JSONObject();
		// notification.put("title", notificationTitle);
		// notification.put("text", notificationText);
		// notification.put("sound", "notification.mp3");
		// notification.put("icon", "ic_launcher_sema");
		notification.put("title", notificationTitle);
		notification.put("body", notificationText);
		notification.put("sound", "default");
		notification.put("color", "#9134C1");
		//
		JSONObject json = new JSONObject();
		json.put("to", deviceToken);
		json.put("notification", notification);
		
		AsyncHttpClient asyncHttpClient;
		if (PROXY_ENABLED)
		{
			AsyncHttpClientConfig config = new DefaultAsyncHttpClientConfig.Builder().setProxyServer(new ProxyServer.Builder(PROXY_ADDRESS, PROXY_PORT)).build();
			asyncHttpClient = new DefaultAsyncHttpClient(config);
		}
		else 
		{
			asyncHttpClient = new DefaultAsyncHttpClient();
		}
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", "key=" + serverKey);
		headers.put("Content-Type", "application/json");
		
		try
		{
			asyncHttpClient
				.preparePost(FIREBASE_FCM_URL)
				.setCharset(java.nio.charset.StandardCharsets.UTF_8)
				.setSingleHeaders(headers)
				.setBody(json.toString())
				.execute(new AsyncCompletionHandler<Response>() 
				{
					   @Override
					   public State onStatusReceived(HttpResponseStatus status) throws Exception
					   {
						   System.out.println(dateFormat.format(new java.util.Date()) + " HTTP status received: " + status);
						   asyncHttpClient.close();
						   return super.onStatusReceived(status);
					   }
					   
					   @Override
					   public Response onCompleted(Response response) throws Exception
					   {
						   System.out.println(dateFormat.format(new java.util.Date()) + " HTTP response processing is finished:");
						   System.out.println(response.toString());
					       return response;
					   }
					    
					   @Override
					   public void onThrowable(Throwable t)
					   {
						   System.out.println(dateFormat.format(new java.util.Date()) + " Exception occurs during the processing of the response");
					       t.printStackTrace();
					   }
				});
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void SimulateSlowHttpResponse(String url, int delayMilliseconds)
	{
		System.out.println(dateFormat.format(new java.util.Date()) + " Method call started");
		
		AsyncHttpClient asyncHttpClient;
		if (PROXY_ENABLED)
		{
			AsyncHttpClientConfig config = new DefaultAsyncHttpClientConfig.Builder().setProxyServer(new ProxyServer.Builder(PROXY_ADDRESS, PROXY_PORT)).build();
			asyncHttpClient = new DefaultAsyncHttpClient(config);
		}
		else 
		{
			asyncHttpClient = new DefaultAsyncHttpClient();
		}
		
		String simulatorUrl = "http://slowwly.robertomurray.co.uk/delay/" + delayMilliseconds + "/url/" + url;
		try
		{
			asyncHttpClient.prepareGet(simulatorUrl).execute(new AsyncCompletionHandler<Response>()
			{
				   @Override
				   public State onStatusReceived(HttpResponseStatus status) throws Exception
				   {
					   System.out.println(dateFormat.format(new java.util.Date()) + " HTTP status line has been received: " + status);
					   asyncHttpClient.close();
					   return super.onStatusReceived(status);
				   }
				   
				   @Override
				   public Response onCompleted(Response response) throws Exception
				   {
				       System.out.println(dateFormat.format(new java.util.Date()) + " HTTP response processing is finished");
				       return response;
				   }
				    
				   @Override
				   public void onThrowable(Throwable t)
				   {
				       System.out.println(dateFormat.format(new java.util.Date()) + " Unexpected exception occurs during the processing of the response");
				       t.printStackTrace();
				   }
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		System.out.println(dateFormat.format(new java.util.Date()) + " Method call finished");
	}
}
