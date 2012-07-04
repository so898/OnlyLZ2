package AssistStaff;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;

import EventPack.EventListener;
import EventPack.Proxy_Fail;
import EventPack.Proxy_Success;

public class CheckProxy extends Thread{

	private EventListener listener;
	private BufferedReader in;
	private static String Website = "http://www.douban.com/";
	private static String proxyHost = Config.GetV("ProxyHost");
	private static String proxyUser = Config.GetV("ProxyUsername");
	private static String proxyPass = Encrypt.Decode(Config.GetV("ProxyPassword"));
	private int proxyPort;
	
	public void Start(){
		
		
		
		if (Config.GetV("ProxyProt") != "")
			proxyPort = Integer.parseInt(Config.GetV("ProxyProt"));
		else
			proxyPort =0;
		
		if (proxyHost.equals("") || proxyPort ==0){
			this.listener.handleEvent(new Proxy_Fail());
			return;
		}
		
		try {
			BufferedInputStream bis = null;
			HttpURLConnection httpUrl = null;
			URL url = null;
			url = new URL(Website);
			InetSocketAddress isa = new InetSocketAddress(proxyHost, proxyPort);
			Proxy proxy = new Proxy(Proxy.Type.HTTP, isa);
			if (Config.GetV("Proxy_Auth").equals("yes"))
				Authenticator.setDefault(new MyAuthenticator(proxyUser, proxyPass));
			httpUrl = (HttpURLConnection) url.openConnection(proxy);
			httpUrl.connect();
			bis = new BufferedInputStream(httpUrl.getInputStream());
			InputStreamReader isr=new InputStreamReader(bis,"UTF-8");
			in = new BufferedReader(isr);
			String temp = in.readLine();
			while(!temp.equals("</html>")||temp != null){
				if (temp.indexOf("<title>") != -1){
					String [] a = temp.split("<title>");
					String [] b = a[1].split("<title>");
					String title = b[0];
					if (title == "¶¹°ê"){
						this.listener.handleEvent(new Proxy_Success());
						return;
					}
				}
				temp = in.readLine();
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			this.listener.handleEvent(new Proxy_Fail());
			return;
		}
		this.listener.handleEvent(new Proxy_Fail());
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
