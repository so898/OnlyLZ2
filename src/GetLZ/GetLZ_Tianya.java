package GetLZ;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import AssistStaff.Config;
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


public class GetLZ_Tianya extends GetLZ{
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
		private com.itextpdf.text.Font fontChinese;
		private boolean stop_mark =false;
		private String page = null;
		private boolean downpdf;
		private String tmp_path = Config.GetV("TempPath");
		private boolean isavepage, ioverwrite, betweenline;
		private String cu_path = null;
		private String line_mark = null;
		private MissionPanel MP;
		private Mission mt;
		private String soft_name ="楼主跟我走2";
		private FileWriter writer = null;
		
		public GetLZ_Tianya(Mission m, MissionPanel x){
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
			
			start();
		}

		public void run(){
			StartDownload();
		}
		
		public void StartDownload(){
			try{
				long tt=System.currentTimeMillis();
				addItem(page,"1.shtml");
				downLoadFirst();
				MP.changeProcess(5);
				String [] tmp_head = page.split("\\d{1,5}\\.shtml");
				for(int i=2;i<(pages * 1)+1;i += 1)
				{
					addItem(tmp_head[0] + String.valueOf(i) + ".shtml",String.valueOf(i)+".shtml");
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
					GetLzTianya(url, filename);
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
					GetLzTianya(url, filename);
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
				XMLInfo.Done(mt);
				MP.changeProcess(100);
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
			httpUrl = (HttpURLConnection) url.openConnection();
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

		public void GetLzTianya(String destUrl, String filename) throws IOException{
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
				httpUrl = (HttpURLConnection) url.openConnection();
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
				else if (temp.indexOf("<div class=\"atl-info\">") != -1 && mark != 1){
					temp = in.readLine();
					String [] a = temp.split(">");
					String [] aa = temp.split("\"");
					String [] b = a[2].split("<");
					author = b[0];
					link = aa[1];
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
					while(temp.indexOf("<div class=\"bbs-content clearfix\">") == -1){
						temp = in.readLine();
					}
					while (temp.indexOf("</div>") == -1){
						temp = temp + in.readLine();
					}
					String [] story1 = temp.split("<div class=\"bbs-content clearfix\">");
					String [] story2 = story1[1].split("</div>");
					String [] story = story2[0].split("<br>");
					for (int i =0; i<story.length; i++){
						if (story[i].indexOf("<center>") !=-1 && story[i].indexOf("</center>") !=-1)
							continue;
						else if (story[i].indexOf("<img src=\"") !=-1){
							String [] img = story[i].split("\"");
							Write("(" + img[1] + ")");
							Write(c_line);
						}
						else if (story[i].indexOf("type=\"application/x-shockwave-flash\"") != -1){
							String [] tmp1 = story[i].split("src=\"");
							String [] flash = tmp1[1].split("\"");
							Write("[Flash 地址：" + flash[0] + "]");
						}
						else{
							Write(story[i]);
							Write(c_line);
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
					if (temp.indexOf("<div class=\"host-ico\">楼主</div>") != -1){
						while(temp.indexOf("<div class=\"bbs-content\">") == -1){
							temp = in.readLine();
						}
						while(temp.indexOf("</div>") == -1){
							temp = temp + in.readLine();
						}
						String [] story1 = temp.split("<div class=\"bbs-content\">");
						String [] story2 = story1[1].split("</div>");
						String [] story = story2[0].split("<br>");
						for (int i =0; i<story.length; i++){
							if (story[i].indexOf("<center>") !=-1 && story[i].indexOf("</center>") !=-1)
								continue;
							else if (story[i].indexOf("<img src=\"") !=-1){
								String [] img = story[i].split("\"");
								Write("(" + img[1] + ")");
								Write(c_line);
							}
							else if (story[i].indexOf("type=\"application/x-shockwave-flash\"") != -1){
								String [] tmp1 = story[i].split("src=\"");
								String [] flash = tmp1[1].split("\"");
								Write("[Flash 地址：" + flash[0] + "]");
							}
							else{
								Write(story[i]);
								Write(c_line);
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
				
				if (pages == 1 && temp.indexOf("<div class=\"atl-pages\">") != -1){
					String tmp1 = in.readLine();
					String tmp2 = in.readLine();
					while(tmp2.indexOf("class=\"js-keyboard-next\"") == -1){
						tmp1 = tmp2;
						tmp2 = in.readLine();
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
}
