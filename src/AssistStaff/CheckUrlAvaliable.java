package AssistStaff;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;

import EventPack.Check_End;
import EventPack.EventListener;


public class CheckUrlAvaliable extends Thread{
	private int webtype;
	private String Website;
	public String title, author = null;
	private EventListener listener;
	private BufferedReader in;
	private static String proxyHost = Config.GetV("ProxyHost");
	private static String proxyUser = Config.GetV("ProxyUsername");
	private static String proxyPass = Encrypt.Decode(Config.GetV("ProxyPassword"));
	private int proxyPort;
	
	public CheckUrlAvaliable(int i, String j){
		webtype = i;
		Website = j;
		if (Config.GetV("ProxyProt") != "")
			proxyPort = Integer.parseInt(Config.GetV("ProxyProt"));
		else
			proxyPort =0;
		start();
	}

	public void run(){
		switch (webtype){
			case 1:
				CheckDouban();
				break;
			case 2:
				CheckTieba();
				break;
			case 3:
				CheckTianya();
			default:
				return;
		}
			
	}
	
	private void CheckDouban(){
		try {
			BufferedInputStream bis = null;
			HttpURLConnection httpUrl = null;
			URL url = null;
			url = new URL(Website);
			if (Config.GetV("Proxy").equals("yes")){
				InetSocketAddress isa = new InetSocketAddress(proxyHost, proxyPort);
				Proxy proxy = new Proxy(Proxy.Type.HTTP, isa);
				if (Config.GetV("Proxy_Auth").equals("yes"))
					Authenticator.setDefault(new MyAuthenticator(proxyUser, proxyPass));
				httpUrl = (HttpURLConnection) url.openConnection(proxy);
			}
			else
				httpUrl = (HttpURLConnection) url.openConnection();
			httpUrl.connect();
			bis = new BufferedInputStream(httpUrl.getInputStream());
			InputStreamReader isr=new InputStreamReader(bis,"UTF-8");
			in = new BufferedReader(isr);
			String temp = in.readLine();
			while(!temp.equals("</html>")){
				if (temp.indexOf("<h1>") != -1){
                    temp = in.readLine();
                    System.out.print(temp);
					String [] a = temp.split("    ");
					title = a[1];
				}
				else if (temp.indexOf("<span class=\"from\">") != -1 && author == null){
					String [] a = temp.split(">");
					String [] b = a[2].split("<");
					author = b[0];
					break;
				}
				temp = in.readLine();
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.listener.handleEvent(new Check_End());
	}
	
	private void CheckTieba(){
		try {
			BufferedInputStream bis = null;
			HttpURLConnection httpUrl = null;
			URL url = null;
			url = new URL(Website);
			if (Config.GetV("Proxy").equals("yes")){
				InetSocketAddress isa = new InetSocketAddress(proxyHost, proxyPort);
				Proxy proxy = new Proxy(Proxy.Type.HTTP, isa);
				if (Config.GetV("Proxy_Auth").equals("yes"))
					Authenticator.setDefault(new MyAuthenticator(proxyUser, proxyPass));
				httpUrl = (HttpURLConnection) url.openConnection(proxy);
			}
			else
				httpUrl = (HttpURLConnection) url.openConnection();
			httpUrl.connect();
			bis = new BufferedInputStream(httpUrl.getInputStream());
			InputStreamReader isr=new InputStreamReader(bis);
			in = new BufferedReader(isr);
			String temp = in.readLine();
			while(temp.indexOf("</html>") == -1 ){
				if (temp.indexOf("title:\"") != -1){
					String [] a = temp.split("\"");
					title = a[1];
					break;
				}
				if (temp.indexOf("author:\"")	 !=	 - 1&& author == null){
					String [] a =temp.split("\"");
					author = a[1];
				}
				temp = in.readLine();
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.listener.handleEvent(new Check_End());
	}
	
	private void CheckTianya(){
		try {
			BufferedInputStream bis = null;
			HttpURLConnection httpUrl = null;
			URL url = null;
			url = new URL(Website);
			if (Config.GetV("Proxy").equals("yes")){
				InetSocketAddress isa = new InetSocketAddress(proxyHost, proxyPort);
				Proxy proxy = new Proxy(Proxy.Type.HTTP, isa);
				if (Config.GetV("Proxy_Auth").equals("yes"))
					Authenticator.setDefault(new MyAuthenticator(proxyUser, proxyPass));
				httpUrl = (HttpURLConnection) url.openConnection(proxy);
			}
			else
				httpUrl = (HttpURLConnection) url.openConnection();
			httpUrl.connect();
			bis = new BufferedInputStream(httpUrl.getInputStream());
			InputStreamReader isr=new InputStreamReader(bis,"UTF-8");
			in = new BufferedReader(isr);
			String temp = in.readLine();
			while(temp.indexOf("</html>") == -1){
				if (temp.indexOf("<h1 class=\"atl-title\">") != -1){
					String [] a = temp.split(">");
					String [] b = a[1].split("<");
					title = b[0];
				}
				if (temp.indexOf("<div class=\"atl-info\">") != -1 && author == null){
					temp = in.readLine();
					String [] a = temp.split(">");
					String [] b = a[2].split("<");
					author = b[0];
					break;
				}
				temp = in.readLine();
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.listener.handleEvent(new Check_End());
	}
	
	public boolean TitleOk(){
		if (title == null)
			return false;
		else
			return true;
	}
	
	public void Stop(){
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setEventListener(EventListener listener){  
        this.listener = listener;  
    }
	
	static class MyAuthenticator extends Authenticator {
	    private String user = "";
	    private String password = "";
	    public MyAuthenticator(String user, String password) {
	    	this.user = user;
	    	this.password = password;
	    }
	    protected PasswordAuthentication getPasswordAuthentication() {
	    	return new PasswordAuthentication(user, password.toCharArray());
	    }
	}
}
