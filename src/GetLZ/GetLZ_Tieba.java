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

import org.apache.commons.lang3.StringEscapeUtils;

import AssistStaff.Config;
import AssistStaff.Encrypt;
import AssistStaff.Mission;
import Main.MissionPanel;
import Main.XMLInfo;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

public class GetLZ_Tieba extends GetLZ{
	public final static boolean DEBUG = true;//调试用
	private static final int BUFFER_SIZE = 8096;//缓冲区大小
	private Vector<String> vDownLoad = new Vector<String>();//URL列表
	private Vector<String> vFileList = new Vector<String>();//下载后的保存文件名列表
	private String title = null;
	private String author = null;
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
	
	public GetLZ_Tieba(Mission m, MissionPanel x){
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
			for(int i=2;i<(pages)+1;i += 1)
			{
				addItem(page + "?pn="+String.valueOf(i),String.valueOf(i)+".html");
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

		//按列表顺序保存资源
		for (int i = 0; i < vDownLoad.size(); i++) {
			url = (String) vDownLoad.get(i);
			filename = (String) vFileList.get(i);
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
			
			try {
				GetLzTieba(url, filename);
			}
			catch (IOException err) {
				if (DEBUG) {
					bug = 1;
					title = null;
					author = null;
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
		//按列表顺序保存资源
		for (int i = 0; i < vDownLoad.size(); i++) {
			url = (String) vDownLoad.get(i);
			filename = (String) vFileList.get(i);
			try {
				GetLzTieba(url, filename);
				MP.changeProcess( ((85 * (i+1)) / vDownLoad.size()) + 10);
			}
			catch (IOException err) {
				if (DEBUG) {
					title = null;
					author = null;
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

	public void GetLzTieba(String destUrl, String filename) throws IOException{
		InputStreamReader isr= null;
		if (isavepage){
			saveToFile(destUrl, filename);
			FileInputStream Openfile=new FileInputStream(tmp_path + File.separatorChar + filename);
			isr=new InputStreamReader(Openfile);
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
			isr=new InputStreamReader(bis);
		}
		BufferedReader in = new BufferedReader(isr);
		String temp = in.readLine();
		FileWriter writer = null;
		String c_line = System.getProperty("line.separator");
		if (title_mark == 1 && !downpdf){
			String filePath = cu_path + File.separatorChar + title + ".txt";
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			writer = new FileWriter(myFilePath, true);
		}
		while(temp.indexOf("</html>") == -1){
			if (temp.indexOf("author:\"")	 !=	 -1 && mark == 0){
				String [] a =temp.split("\"");
				author = a[1];
				mark = 1;
				title_mark = 1;
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
					String filePath = cu_path + File.separatorChar +title + ".txt";
					filePath = filePath.toString();
					File myFilePath = new File(filePath);
					//System.out.println(myFilePath.exists());
					if (myFilePath.exists() && ioverwrite){
						myFilePath.delete();
					}
					writer = new FileWriter(myFilePath, true);
					writer.write(title + c_line);
					writer.write(c_line + "原帖地址： " + page + c_line);
					writer.write(c_line + "作者：" + author + c_line + c_line);
				}
			}
			else if (temp.indexOf("<li class=\"l_pager pager_theme_2\"><span class=\"tP\">") != -1 && pages ==1){
				
				String [] a = temp.split("\"");
				String [] b = a[a.length-2].split("=");
				pages = Integer.parseInt(b[1]);
			}
			else if (mark != 0){
				if (temp.indexOf(author) != -1 && temp.indexOf("<div class=\"p_post\"") != -1){
					while (temp.indexOf("<cc>") == -1){
						temp = in.readLine();
						if (temp.indexOf("<legend>") != -1){
							String [] a = temp.split("&nbsp;");
							if (downpdf){
								try {
									Paragraph pf = new Paragraph("引用 " + a[1] + " :",fontChinese);
									doc.add(pf);
								} catch (DocumentException e) {
									System.out.println("yinyong:"+e);
									}
							}
							else{
								writer.write("引用 " + a[1] + " :" +c_line);
							}
							temp = in.readLine();
							String [] b = temp.split("</?p(.*\")?>");
							if (downpdf){
								try {
									Paragraph pf = new Paragraph("[[" + b[1] + "]]",fontChinese);
									doc.add(pf);
								} catch (DocumentException e) {System.out.println("[]"+e);}
							}
							else{
								writer.write("[[" + StringEscapeUtils.unescapeHtml4(b[1]) + "]]"+c_line);
							}
						}
						
					}
					String [] a = temp.split("<cc><.*d_post_content\">");
					String [] b = a[1].split("</div></cc><br/>");
					String [] c = b[0].split("<br>");
					for (int i = 0 ;i < c.length; i++){
						String uncode = StringEscapeUtils.unescapeHtml4(c[i]);
						if (uncode.indexOf("<img ") != -1){
							String [] x = uncode.split("src=\"");
							String [] xx = x[1].split("\"");
							String [] y = uncode.split("<img .*\"( )?>");
							for (int j=0;j<y.length;j++){
								if (downpdf){
									try {
										Paragraph	pf = new Paragraph(y[j],fontChinese);
										doc.add(pf);
									} catch (DocumentException e) {System.out.println("y: "+e);}
								}
								else{
									writer.write(y[j]);
								}
								if (j==0){
									if (downpdf){
										try {
											Image img = Image.getInstance(xx[0]);
											img.setAlignment(Image.LEFT);
											if (img.getPlainHeight() > 500 || img.getPlainWidth() > 500){
												img.scaleToFit(500f, 500f);
											}
											doc.add(img);
										} catch (DocumentException e) {System.out.println("img:" + e);}
									}
									else{
										writer.write("(" + xx[0] + ")");
									}
									
								}
							}
							if (y.length == 0){
								if (downpdf){
									try {
										Image img = Image.getInstance(xx[0]);
										img.setAlignment(Image.LEFT);
										if (img.getPlainHeight() > 500 || img.getPlainWidth() > 500){
											img.scaleToFit(500f, 500f);
										}
										doc.add(img);
									} catch (DocumentException e) {System.out.println("img2:"+e);}
								}
								else{
									writer.write("(" + xx[0] + ")");
								}
							}
							if (downpdf){
								try {
									Paragraph cline = new Paragraph(" ");
									doc.add(cline);
								} catch (DocumentException e) {}
							}
							else{
								writer.write(c_line);
							}
						}
						else if (uncode.indexOf("<a href=") != -1){
							String [] u = uncode.split("\"");
							String [] w = uncode.split("<a href=.*</a>");
							for (int j=0;j<w.length;j++){
								if (downpdf){
									try {
										Paragraph pf = new Paragraph(w[j],fontChinese);
										doc.add(pf);
									} catch (DocumentException e) {}
								}
								else{
									writer.write(w[j]);
								}
								if (j==0){
									if (downpdf){
										try {
											Paragraph pf = new Paragraph("[" + u[1] + "]",fontChinese);
											doc.add(pf);
										} catch (DocumentException e) {}
									}
									else{
										writer.write("[" + u[1] + "]");
									}
									
								}
							}
							if (downpdf){
								try {
												Paragraph cline = new Paragraph(" ");
									doc.add(cline);
								} catch (DocumentException e) {}
							}
							else{
								writer.write(c_line);
							}
						}
						else if (uncode.indexOf("pluginspage=\"http://www.macromedia.com/go/getflashplayer\"") != -1){
							String [] x = uncode.split("src=\"");
							String [] xx = x[1].split("\"");
							if (downpdf){
								try {
									Paragraph pf = new Paragraph("[Flash 地址：" + xx[0] + "]",fontChinese);
									doc.add(pf);
								} catch (DocumentException e) {}
							}
							else{
								writer.write("[Flash 地址：" + xx[0] + "]");
							}
						}
						else{
							if (!uncode.equals("")){
								if (downpdf){
									try {
										Paragraph pf = new Paragraph(uncode,fontChinese);
										doc.add(pf);
									} catch (DocumentException e) {}
								}
								else{
									writer.write(uncode + c_line);
								}
							}
						}
					}
					if (betweenline){
						if (downpdf){
							try {
								Paragraph cline = new Paragraph("");
								doc.add(cline);
								Paragraph pf = new Paragraph(line_mark,fontChinese);
								doc.add(pf);
								doc.add(cline);
							} catch (DocumentException e) {}
						}
						else{
							writer.write(c_line + line_mark + c_line + c_line);
						}
					}
					else{
						if (downpdf){
							try {
								Paragraph cline = new Paragraph(" ");
								doc.add(cline);
							} catch (DocumentException e) {}
						}
						else{
							writer.write(c_line);
						}
					}
					
				}
			}
			temp = in.readLine();
		}
		if (!downpdf){
			writer.close();
		}
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

