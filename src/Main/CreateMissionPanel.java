package Main;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import AssistStaff.CheckUrl;
import AssistStaff.CheckUrlAvaliable;
import AssistStaff.Config;
import AssistStaff.Mission;
import AssistStaff.WebTextField;
import EventPack.EventListener;
import EventPack.EventSource;


public class CreateMissionPanel extends JDialog implements ActionListener, EventListener{
	private JTextField Website, Status;
	private JCheckBox Txt, Pdf, Pic;
	private JButton Start, Cancel, Store;
	private boolean Checked = false;
	private String title, author, path = Config.GetV("StorePath");
	private int webtype = 0;
	private CheckUrlAvaliable x;
	private Mission m;
	private String webpage = null;
	
	
	public CreateMissionPanel(Frame frame, boolean model){
		super(frame, model);
		setIconImage(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/image/icon.png"))); 
		setTitle("新建任务");
		setResizable(false);
		setSize(400, 180);
		setLayout(null);
		int n_width=0;
	    int n_height=0;
	    n_width=Toolkit.getDefaultToolkit().getScreenSize().width;  
	    n_height=Toolkit.getDefaultToolkit().getScreenSize().height;  
	    setLocation((n_width - 400)/2, (n_height - 180)/2);
	    
	    
	    JLabel Enter = new JLabel("输入网址: ");
		Enter.setBounds(10, 10, 100, 20);
		add(Enter);
		Website = new WebTextField(20);
		Website.setBounds(80, 10, 295, 20);
		Website.getDocument().addDocumentListener(new   DocumentListener(){
			public   void   insertUpdate(DocumentEvent   e){
				CheckEnter();
			}public   void   removeUpdate(DocumentEvent   e){
				CheckEnter();
			}public   void   changedUpdate(DocumentEvent   e){
				CheckEnter();
			}
		});
		add(Website);
		
		Status = new JTextField("请输入网址");
		Status.setBounds(20, 40, 350, 30);
		Status.setEnabled(false);
		Status.setHorizontalAlignment(JTextField.CENTER);
		Status.setBackground(Color.white);
		add(Status);
	    
		Txt = new JCheckBox("保存为TXT文件");
		Txt.setBounds(10, 80, 120, 20);
		Txt.addActionListener(this);
		if(Config.GetV("DefaultTxt").equals("yes"))
			Txt.setSelected(true);
		add(Txt);
		
		Pdf = new JCheckBox("保存为PDF文件");
		Pdf.setBounds(130, 80, 120, 20);
		Pdf.addActionListener(this);
		if(Config.GetV("DefaultPdf").equals("yes"))
			Pdf.setSelected(true);
		add(Pdf);
	    
	    Pic = new JCheckBox("保存图片到文件夹");
	    Pic.setBounds(250, 80, 140, 20);
	    Pic.addActionListener(this);
	    Pic.setEnabled(false);
	    if(Config.GetV("DefaultPic").equals("yes"))
	    	Pic.setSelected(true);
	    //add(Pic);
	    
	    Store = new JButton("保存位置");
	    Store.setBounds(120,110,80,30);
	    Store.addActionListener(this);
	    add(Store);
	    
	    Start = new JButton("下载");
	    Start.setBounds(210,110,80,30);
	    Start.addActionListener(this);
	    Start.setEnabled(false);
	    add(Start);
	    
	    Cancel = new JButton("取消");
	    Cancel.setBounds(300,110,80,30);
	    Cancel.addActionListener(this);
	    add(Cancel);
	    
	    
	    setVisible(true);
	}
	
	
	public void actionPerformed(ActionEvent a) {
		if (a.getSource() == Txt){
			if (!Txt.isSelected() && !Pdf.isSelected() && !Pic.isSelected()){
				Start.setEnabled(false);
			}
			else {
				if (Txt.isSelected()){
					Config.SetV("DefaultTxt", "yes");
					Pdf.setSelected(false);
					Config.SetV("DefaultPdf", "no");
					Pic.setSelected(false);
					Config.SetV("DefaultPic", "no");
				}
				else
					Config.SetV("DefaultTxt", "no");
				if (CheckStart())
					Start.setEnabled(true);
			}
		}
		else if (a.getSource() == Pdf){
			if (!Txt.isSelected() && !Pdf.isSelected() && !Pic.isSelected()){
				Start.setEnabled(false);
			}
			else {
				if (Pdf.isSelected()){
					Config.SetV("DefaultPdf", "yes");
					Txt.setSelected(false);
					Config.SetV("DefaultTxt", "no");
					Pic.setSelected(false);
					Config.SetV("DefaultPic", "no");
				}
				else
					Config.SetV("DefaultPdf", "no");
				if (CheckStart())
					Start.setEnabled(true);
			}
		}
		else if (a.getSource() == Pic){
			if (!Txt.isSelected() && !Pdf.isSelected() && !Pic.isSelected()){
				Start.setEnabled(false);
			}
			else {
				if (Pic.isSelected()){
					Config.SetV("DefaultPic", "yes");
					Txt.setSelected(false);
					Config.SetV("DefaultTxt", "no");
					Pdf.setSelected(false);
					Config.SetV("DefaultPdf", "no");
				}
				else
					Config.SetV("DefaultPic", "no");
				if (CheckStart())
					Start.setEnabled(true);
			}
		}
		else if (a.getSource() == Store){
			JFileChooser chooser = new JFileChooser(Config.GetV("StorePath"));
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			add(chooser);
			int returnVal = chooser.showOpenDialog(getParent());
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				path = chooser.getSelectedFile().getAbsolutePath();
			}
		}
		else if (a.getSource() == Cancel){
			if (Checked)
				x.Stop();
			setVisible(false);
		}
		else if (a.getSource() == Start){
			m = new Mission();
			m.Title = title;
			m.Author = author;
			m.Path = path;
			m.Url = webpage;;
			if (Config.GetV("DefaultTxt").equals("yes")){
				m.Type = "1";
			}
			else {
				m.Type = "0";
			}
			if (Config.GetV("DefaultPdf").equals("yes")){
				m.Type = m.Type+"1";
			}
			else {
				m.Type = m.Type+"0";
			}
			if (Config.GetV("DefaultPic").equals("yes")){
				m.Type = m.Type+"1";
			}
			else {
				m.Type = m.Type+"0";
			}
			m.ID = XMLInfo.ReturnNodes() + 1;
			m.Done = false;
			m.webType = webtype;
			if (!XMLInfo.Have(m)){
				XMLInfo.Add(m);
				setVisible(false);
			}else{
				JOptionPane.showMessageDialog(this, "您输入的地址已经存在于下载列表中。","提示",JOptionPane.ERROR_MESSAGE);
				setVisible(false);
			}
		}
	}
	
	public boolean CheckReturn(){
		if (m == null)
			return false;
		else
			return true;
	}
	
	public Mission ReturnMission(){
		return m;
	}
	
	private void CheckEnter(){
		webtype = 0;
		String tmp = CheckUrl.Check(Website.getText());
		if (!tmp.equals("false")){
			if (tmp.equals("Douban")){
				String [] tmp_page = Website.getText().split("(\\?start=\\d{1,}00)");
				webpage = tmp_page[0];
				Status.setText("检测到豆瓣网址，正在检测网页...");
				x = new CheckUrlAvaliable(1, webpage);
				webtype = 1;
				x.setEventListener(this);
			}
			if (tmp.equals("Tieba")){
				String [] tmp_page = Website.getText().split("\\?pn=\\d{1,}");
				webpage = tmp_page[0];
				Status.setText("检测到百度贴吧网址，正在检测网页...");
				x = new CheckUrlAvaliable(2, webpage);
				webtype = 2;
				x.setEventListener(this);
			}
			if (tmp.equals("Tianya")){
				String [] tmp_page = Website.getText().split("\\d{1,5}\\.shtml");
				webpage = tmp_page[0] + "1.shtml";
				Status.setText("检测到天涯网址，正在检测网页...");
				x = new CheckUrlAvaliable(3, webpage);
				webtype = 3;
				x.setEventListener(this);
			}
			if (tmp.equals("Tianyaold")){
				webpage = CheckUrl.ChangeTianya(Website.getText());
				Status.setText("检测到天涯网址，正在检测网页...");
				x = new CheckUrlAvaliable(3, webpage);
				webtype = 3;
				x.setEventListener(this);
			}
			if (CheckStart()){
				Start.setEnabled(true);
			}
		}
		else{
			if (Checked)
				x.Stop();
			webtype = 0;
			Status.setText("请正确输入网址");
			Checked = false;
			webpage = null;
			Start.setEnabled(false);
		}
			
	}
	
	private boolean CheckStart(){
		if (!Txt.isSelected() && !Pdf.isSelected() && !Pic.isSelected())
			return false;
		else if (!Checked)
			return false;
		else 
			return true;
	}
	
	public void handleEvent(EventSource event) {
		int eType = event.getEventType();
		switch(eType){
			case EventSource.EVENT_END:
				if (!x.TitleOk()){
					Status.setText("页面不存在，请检查地址。");
					webtype = 0;
					break;
				}
				else if (webtype == 1){
					title = x.title;
					author = x.author;
					Status.setText("[豆瓣]"+title);
					Checked = true;
				}
				else if (webtype == 2){
					title = x.title;
					author = x.author;
					Status.setText("[百度贴吧]"+title);
					Checked = true;
				}
				else if (webtype == 3){
					title = x.title;
					author = x.author;
					Status.setText("[天涯]"+title);
					Checked = true;
				}
				if (CheckStart()){
					Start.setEnabled(true);
				}
				break;
			default:
				break;
		}
	}
	


	
}
