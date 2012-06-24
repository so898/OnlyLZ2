package AssistStaff;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import EventPack.Check_End;
import EventPack.EventListener;


public class CheckUrlAvaliable extends Thread{
	private int webtype;
	private String Website;
	public String title, author = null;
	private EventListener listener;
	private BufferedReader in;
	
	public CheckUrlAvaliable(int i, String j){
		webtype = i;
		Website = j;
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
			httpUrl = (HttpURLConnection) url.openConnection();
			httpUrl.connect();
			bis = new BufferedInputStream(httpUrl.getInputStream());
			InputStreamReader isr=new InputStreamReader(bis,"UTF-8");
			in = new BufferedReader(isr);
			String temp = in.readLine();
			while(!temp.equals("</html>")){
				if (temp.indexOf("<h1>") != -1){
					String [] a = temp.split("<h1>");
					String [] b = a[1].split("</h1>");
					title = b[0];
				}
				else if (temp.indexOf("<span class=\"pl20\">") != -1 && author == null){
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
			httpUrl = (HttpURLConnection) url.openConnection();
			httpUrl.connect();
			bis = new BufferedInputStream(httpUrl.getInputStream());
			InputStreamReader isr=new InputStreamReader(bis);
			in = new BufferedReader(isr);
			String temp = in.readLine();
			while(temp.indexOf("</html>") == -1 ){
				if (temp.indexOf("<h1 class=") != -1){
					String [] a = temp.split(">");
					String [] b = a[1].split("<");
					title = b[0];
					break;
				}
				if (temp.indexOf("author")	 !=	 - 1&& author == null){
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
}
