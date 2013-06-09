package GetLZ;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.util.Vector;

import AssistStaff.Config;
import AssistStaff.Encrypt;
import AssistStaff.Mission;
import Main.MissionPanel;
import Main.XMLInfo;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;


public class GetLZ_Douban extends GetLZ{
		public final static boolean DEBUG = true;//调试用
		private static final int BUFFER_SIZE = 8096;//缓冲区大小
		private Vector<String> vDownLoad = new Vector<String>();//URL列表
		private Vector<String> vFileList = new Vector<String>();//下载后的保存文件名列表
		private String title = null;
		private String author = null;
		private String link = null;
		private int mark = 0;
		private int title_mark = 0;
		private int pages = 1;
		private int bug =0;
		private Document doc = null;
		private Font fontChinese;
		private boolean stop_mark =false;
		private String page = null;
		private boolean downpdf;
		private String tmp_path = Config.GetV("TempPath");
		private boolean isavepage, ioverwrite, betweenline, proxy, proxy_auth;
		private String cu_path = null;
		private String line_mark = null;
		private MissionPanel MP;
		private Mission mt;
		private String soft_name ="楼主跟我走2";
		private FileWriter writer = null;
		private static String proxyHost = Config.GetV("ProxyHost");
		private static String proxyUser = Config.GetV("ProxyUsername");
		private static String proxyPass = Encrypt.Decode(Config.GetV("ProxyPassword"));
		private int proxyPort;
		
		public GetLZ_Douban(Mission m, MissionPanel x){
			title = m.Title;
			page = m.Url;
			cu_path = m.Path;
			MP = x;
			mt = m;
			if (Integer.parseInt(m.Type) % 100 / 10 == 1)
				downpdf =true;
			else
				downpdf = false;
			
			if (Config.GetV("DownloadTemp").equals("yes"))
				isavepage = true;
			else
				isavepage = false;
			
			if (Config.GetV("RewriteTxt").equals("yes"))
				ioverwrite = true;
			else
				ioverwrite = false;
			
			if (Config.GetV("BetweenLine").equals("yes"))
				betweenline = true;
			else
				betweenline = false;
			
			for (int i =0; i<10 ; i++)
				line_mark = line_mark + Config.GetV("LineMark");
			
			if (Config.GetV("ProxyProt") != "")
				proxyPort = Integer.parseInt(Config.GetV("ProxyProt"));
			else
				proxyPort =0;
			
			if (Config.GetV("Proxy").equals("yes"))
				proxy = true;
			else
				proxy = false;
			
			if (Config.GetV("ProxyAuth").equals("yes"))
				proxy_auth = true;
			else
				proxy_auth = false;
			
			start();
		}

		public void run(){
			StartDownload();
		}
		
		public void StartDownload(){
			try{
				long tt=System.currentTimeMillis();
				addItem(page,"0.html");
				downLoadFirst();
				MP.changeProcess(5);
				for(int i=100;i<(pages * 100);i += 100)
				{
					addItem(page + "?start="+String.valueOf(i),String.valueOf(i)+".html");
				}
				downLoadByList();
				long tt1=System.currentTimeMillis();
				tt1=tt1-tt;
			}
			catch (Exception err) {
				//System.out.println(err.getMessage());
			}
		}

		public void downLoadFirst() {
			String url = null;
			String filename = null;

			if (downpdf){
				doc = new Document(PageSize.A4);
				try {
					BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
					fontChinese = new Font(bf , 13, Font.NORMAL);
				} catch (DocumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			//按列表顺序保存资源
			for (int i = 0; i < vDownLoad.size(); i++) {
				url = (String) vDownLoad.get(i);
				filename = (String) vFileList.get(i);
				doc = new Document(PageSize.A4);
				try {
					GetLzDouban(url, filename);
				}
				catch (IOException err) {
					if (DEBUG) {
						bug = 1;
						title = null;
						author = null;
						link = null;
						mark = 0;
						title_mark = 0;
						pages = 1;
					}
				}
			}
			if (DEBUG) {
				vDownLoad = new Vector<String>();
				vFileList = new Vector<String>();
			}
		}

		public void downLoadByList() {
			String url = null;
			String filename = null;
			//processbar.setValue(1);
			//按列表顺序保存资源
			for (int i = 0; i < vDownLoad.size(); i++) {
				url = (String) vDownLoad.get(i);
				filename = (String) vFileList.get(i);
				try {
					GetLzDouban(url, filename);
					MP.changeProcess( ((85 * (i+1)) / vDownLoad.size()) + 10);
				}
				catch (IOException err) {
					if (DEBUG) {
						title = null;
						author = null;
						link = null;
						mark = 0;
						title_mark = 0;
						pages = 1;
						bug =1;
					}
				}
			}

			if (DEBUG) {
				if (bug == 0 && !stop_mark){
					title = null;
					author = null;
					link = null;
					mark = 0;
					title_mark = 0;
					pages = 1;
					bug =0;
				}
				else{
					bug = 0;
				}
				if (downpdf)
					doc.close();
				page = null;
				mt.Done = true;
				MP.changeProcess(100);
				XMLInfo.Done(mt);
				vDownLoad = new Vector<String>();
				vFileList = new Vector<String>();
			}
		}

		private void addItem(String url, String filename) {
			vDownLoad.add(url);
			vFileList.add(filename);
		}

		public void saveToFile(String destUrl, String fileName) throws IOException {
			FileOutputStream fos = null;
			BufferedInputStream bis = null;
			HttpURLConnection httpUrl = null;
			URL url = null;
			byte[] buf = new byte[BUFFER_SIZE];
			int size = 0;
			//建立链接
			url = new URL(destUrl);
			if (proxy){
				InetSocketAddress isa = new InetSocketAddress(proxyHost, proxyPort);
				Proxy proxy = new Proxy(Proxy.Type.HTTP, isa);
				if (proxy_auth)
					Authenticator.setDefault(new MyAuthenticator(proxyUser, proxyPass));
				httpUrl = (HttpURLConnection) url.openConnection(proxy);
			}
			else{
				httpUrl = (HttpURLConnection) url.openConnection();
			}
			//连接指定的资源
			httpUrl.connect();
			//获取网络输入流
			bis = new BufferedInputStream(httpUrl.getInputStream());
			//建立文件
			fos = new FileOutputStream(tmp_path + File.separatorChar + fileName);
			//System.out.println(tmp_path);

			//保存文件
			while ( (size = bis.read(buf)) != -1)
				fos.write(buf, 0, size);
				fos.close();
				bis.close();
				httpUrl.disconnect();
		}
		
		private void Write(String words){
			if (downpdf){
				String c_line = System.getProperty("line.separator");
				try {
					if (words.equals(c_line)){
						Paragraph cline = new Paragraph(" ");
						doc.add(cline);
					}else{
						Paragraph pf = new Paragraph(words,fontChinese);
						doc.add(pf);
					}
				} catch (DocumentException e) {
					e.printStackTrace();
				}
				
			}
			else{
				try {
					writer.write(words);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void GetLzDouban(String destUrl, String filename) throws IOException{
			InputStreamReader isr= null;
			if (isavepage){
				saveToFile(destUrl, filename);
				FileInputStream Openfile=new FileInputStream(tmp_path + File.separatorChar + filename);
				isr=new InputStreamReader(Openfile,"UTF-8");
			}
			else{
				BufferedInputStream bis = null;
				HttpURLConnection httpUrl = null;
				URL url = null;
				//建立链接
				url = new URL(destUrl);
				if (proxy){
					InetSocketAddress isa = new InetSocketAddress(proxyHost, proxyPort);
					Proxy proxy = new Proxy(Proxy.Type.HTTP, isa);
					if (proxy_auth)
						Authenticator.setDefault(new MyAuthenticator(proxyUser, proxyPass));
					httpUrl = (HttpURLConnection) url.openConnection(proxy);
				}
				else{
					httpUrl = (HttpURLConnection) url.openConnection();
				}
				
				//连接指定的资源
				httpUrl.connect();
				//获取网络输入流
				bis = new BufferedInputStream(httpUrl.getInputStream());
				isr=new InputStreamReader(bis,"UTF-8");
			}
			
			BufferedReader in = new BufferedReader(isr);
			String temp = in.readLine();
			String c_line = System.getProperty("line.separator");
			if (title_mark == 1 && !downpdf){
				String filePath = cu_path + File.separatorChar + title + ".txt";
				filePath = filePath.toString();
				File myFilePath = new File(filePath);
				writer = new FileWriter(myFilePath, true);
			}
			while(!temp.equals("</html>")){
				if (temp.indexOf("<title>")	 !=	 -1 && title_mark == 0){
					if (!downpdf){
						String filePath = cu_path + File.separatorChar +title + ".txt";
						filePath = filePath.toString();
						File myFilePath = new File(filePath);
						if (myFilePath.exists() && ioverwrite){
							myFilePath.delete();
						}
						writer = new FileWriter(myFilePath, true);
						writer.write(title + c_line);
					}
					title_mark = 1;
				}
				else if (temp.indexOf("<span class=\"from\">") != -1 && mark != 1){
					String [] a = temp.split(">");
					String [] ab = a[1].split("<a href=");
					String [] b = a[2].split("<");
					author = b[0];
					link = ab[1];
					if (downpdf){
						String filePath = cu_path + File.separatorChar +title + ".pdf";
						filePath = filePath.toString();
						try {
							PdfWriter.getInstance(doc, new FileOutputStream(filePath));
							Paragraph pf = new Paragraph(title,fontChinese);
							Paragraph cline = new Paragraph(" ");
							doc.addTitle(title);
							doc.addAuthor(author);
							doc.addCreator(soft_name);
							doc.open();
							doc.add(pf);
							doc.add(cline);
							pf = new Paragraph("原帖地址： " + page,fontChinese);
							doc.add(pf);
							doc.add(cline);
							pf = new Paragraph("作者：" + author,fontChinese);
							doc.add(pf);
							doc.add(cline);
							doc.add(cline);
						} catch (DocumentException e) {System.out.println(e);}
					}
					else{
						writer.write(c_line + "原帖地址： " + page + c_line);
						writer.write(c_line + "作者： " + author + c_line + c_line);
					}
					while(temp.indexOf("<div class=\"topic-content\">") != -1){
						temp = in.readLine();
					}
					while (temp.indexOf("</p>") == -1){
						temp = in.readLine();
						if (temp.indexOf("</p>") == -1){
							String [] y = temp.split("<br/>");
							if (y.length > 1){
								String [] u = null;
								String [] w = null;
								if (y[1].indexOf("<a href=") != -1){
									u = y[1].split("\"");
									w = y[1].split("<a href=.*</a>");
									if (w.length == 0){
										Write("[" + u[1] + "]");
									}	
									else{
										for (int i=0;i<w.length;i++){
											Write(w[i]);
											if (i==0){
												Write("[" + u[1] + "]");
											}
										}
									}
									Write(c_line);
								}
								else{
									Write(y[1]);
									Write(c_line);
								}
							}
							else if (!temp.equals("")){
								Write(c_line);
							}
						}
						else{
							String [] y = temp.split(">");
							String [] w = y[1].split("<");
							if (w.length > 1){
								Write(w[0]);
								Write(c_line);
							}
						}
					}
					mark =1;
					if (betweenline){
						Write(c_line);
						Write(line_mark);
						Write(c_line);
					}
					else{
						Write(c_line);
					}
				}
				else if (mark == 1 ){
					if (temp.indexOf(author) != -1 && temp.indexOf(link) != -1 && temp.indexOf("src=") == -1 && temp.indexOf("<span class=\"pubdate\">") == -1){
						while(temp.indexOf("<p>") ==-1){
							if (temp.indexOf("<div class=\"reply-quote\">") !=-1){
								String x_temp = temp;
								while (temp.indexOf("</div>") == -1){
									temp = in.readLine();
									x_temp = x_temp + temp;
								}
								String [] a = x_temp.split(">");
								String [] b = a[4].split("<");
								String [] c = a[9].split("<");
								Write("引用 " + c[0] + " :");
								Write(c_line);
								Write("[[" + b[0] + "]]");
								Write(c_line);
							}
							temp = in.readLine();
						}
						
						String [] z = temp.split("<p>");
						if (z.length > 1){
							if (z[1].indexOf("</p>") != -1){
								String [] w = z[1].split("<");
								Write(w[0]);
								Write(c_line);
								Write(c_line);
								temp=in.readLine();
								continue;
							}
							else{
								if (temp.indexOf("<br/>") != -1){
									String [] u = temp.split("<br/>");
									String [] p = u[0].split(">");
									Write(p[1]);
									Write(c_line);
									for (int i =1; i< u.length-1; i++){
										String [] w = null;
										String [] u1 = null;
										if (u[1].indexOf("<a href=") != -1){
											u1 = u[1].split("\"");
											w = u[1].split("<a href=.*</a>");
											if (w.length == 0)
												Write("[" + u1[1] + "]");
											else{
												for (int j=0;j<w.length;j++){
													Write(w[j]);
													if (i==0){
														Write("[" + u1[1] + "]");
													}
												}
											}
											Write(c_line);
										}
										else{
											Write(u[1]);
											Write(c_line);
										}
									}
									String [] q = u[u.length-1].split("<");
									Write(q[0]);
									Write(c_line);
									
								}
								else{
									Write(z[1]);
									Write(c_line);
								}
							}
							
						}
						else{
							Write(c_line);
						}
						while (temp.indexOf("</p>") == -1){
							temp = in.readLine();
							if (temp.indexOf("</p>") == -1){
								String [] y = temp.split(">");
								if (y.length > 1){
									Write(y[1]);
									Write(c_line);
								}
								else{
									Write(c_line);
								}
							}
							else{
								String [] y = temp.split(">");
								String [] w = y[1].split("<");
								if (w.length > 1){
									Write(w[0]);
									Write(c_line);
								}
							}
							
						}
						if (betweenline){
							Write(c_line);
							Write(line_mark);
							Write(c_line);
						}
						else{
							Write(c_line);
						}
					}
				}
				
				if (pages == 1 && temp.indexOf("<span class=\"thispage\">") != -1){
					String tmp1 = in.readLine();
					String tmp2 = in.readLine();
					String tmp3 = in.readLine();
					while (tmp3.indexOf("<span class=\"next\">") == -1){
						tmp1 = tmp2;
						tmp2 = tmp3;
						tmp3 = in.readLine();
					}
					String [] tmp_a = tmp1.split(">");
					String [] tmp_b = tmp_a[1].split("<");
					
					pages = Integer.parseInt(tmp_b[0]);
				}
				temp = in.readLine();
			}
			if (!downpdf)
				writer.close();
			in.close();
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
