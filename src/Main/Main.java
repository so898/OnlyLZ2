package Main;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import AssistStaff.Config;
import AssistStaff.Mission;
import EventPack.EventListener;
import EventPack.EventSource;


public class Main extends JFrame implements ActionListener, EventListener{
	private int Width=450, Height=600;
	private JMenuItem about, exit, setting, help, stop_all, start_all, create, clear_list;
	private JCheckBoxMenuItem sys_tray, overwrite;
	private JButton Start, Stop, Create;
	private GridLayout MidLayout = new GridLayout(6,1);
	private JPanel MidPanel;
	private StatusBar statusbar;
	private SystemTray tray;
	private TrayIcon trayIcon;
	private boolean istray = false, cantray = false;
	private ArrayList<MissionPanel> mlist = new ArrayList<MissionPanel>();
	
	public Main(){
		setIconImage(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/image/icon.png")));
		setSize(Width,Height);
		setLayout(null);
		setResizable(false);
		setTitle("楼主跟我走2");
		int	 width=0;
		int	 height=0;
		width=Toolkit.getDefaultToolkit().getScreenSize().width;	
		height=Toolkit.getDefaultToolkit().getScreenSize().height;	
		setLocation((width - Width)/2, (height - Height)/2);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JMenuBar menuBar = new JMenuBar();
		JMenu startMenu = new JMenu("文件");
		JMenu optionMenu = new JMenu("选项");
		JMenu helpMenu = new JMenu("帮助");
		menuBar.add(startMenu);
		menuBar.add(optionMenu);
		menuBar.add(helpMenu);
		create = new JMenuItem("新建任务");
		create.addActionListener(this);
		startMenu.add(create);
		startMenu.addSeparator();
		/*start_all = new JMenuItem("开始全部下载");
		start_all.setEnabled(false);
		start_all.addActionListener(this);
		startMenu.add(start_all);
		stop_all = new JMenuItem("停止全部下载");
		stop_all.setEnabled(false);
		stop_all.addActionListener(this);
		startMenu.add(stop_all);*/
		clear_list = new JMenuItem("清空列表");
		clear_list.addActionListener(this);
		startMenu.add(clear_list);
		startMenu.addSeparator();
		exit = new JMenuItem("退出");
		exit.addActionListener(this);
		startMenu.add(exit);
		setting = new JMenuItem("设置");
		setting.addActionListener(this);
		optionMenu.add(setting);
		optionMenu.addSeparator();
		sys_tray = new JCheckBoxMenuItem("启用系统托盘");
		sys_tray.addActionListener(this);
		if(Config.GetV("SystemTray").equals("yes")){
			sys_tray.setState(true);
		}
		optionMenu.add(sys_tray);
		overwrite = new JCheckBoxMenuItem("覆盖文本");
		overwrite.addActionListener(this);
		if(Config.GetV("RewriteTxt").equals("yes"))
			overwrite.setState(true);
		optionMenu.add(overwrite);
		help = new JMenuItem("使用说明");
		help.addActionListener(this);
		helpMenu.add(help);
		about = new JMenuItem("关于");
		about.addActionListener(this);
		helpMenu.add(about);
		setJMenuBar(menuBar);
		
		JPanel TopPanel = new JPanel();
		TopPanel.setBounds(0, 0, Width, 50);
		TopPanel.setLayout(null);
		Create = new JButton("新建任务");
		Create.setBounds(10, 5, 100, 40);
		Create.addActionListener(this);
		TopPanel.add(Create);
		Start = new JButton("全部开始");
		Start.setBounds(130, 5, 100, 40);
		Start.addActionListener(this);
		TopPanel.add(Start);
		Stop = new JButton("全部结束");
		Stop.setBounds(250, 5, 100, 40);
		Stop.addActionListener(this);
		TopPanel.add(Stop);
		add(TopPanel);
		
		MidPanel = new JPanel();
		MidPanel.setLayout(MidLayout);
		MidPanel.setSize(435, (MidLayout.getRows()+2)*70);
		MidPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		JScrollPane scrollPane = new JScrollPane(MidPanel);
		scrollPane.setBounds(5, 50, 435, 470);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane);
		
		statusbar = new StatusBar("请新建任务开始下载。",0);
	    statusbar.setBounds(0, Height-75, Width, 25);
	    add(statusbar);
		
		
		MakeList(0);
		if (SystemTray.isSupported() && Config.GetV("SystemTray").equals("yes")) {
			this.tray();
			cantray = true;
		}
		
		setVisible(true);
	}
	
	private void MakeList(int i){
		ArrayList<Mission> ml = null;
		if (i==1){
			MidPanel.removeAll();
			MidLayout.setRows(6);
			ArrayList<MissionPanel> tmp_list = mlist;
			//Collections.reverse(tmp_list);
			int mission = 0;
			for(MissionPanel m:tmp_list){
				mission += 1;
				if (mission >= 6)
					MidLayout.setRows(MidLayout.getRows()+1);
				MidPanel.setSize(435, (MidLayout.getRows()+1)*70);
				MidPanel.add(m);
			}
			statusbar.flush("任务建立成功");
		}
		else{
			if (XMLInfo.ReturnNodes() == 0)
				return;
			ml = XMLInfo.ReturnList();
			Collections.reverse(ml);
			int mission = 0;
			for(Mission m: ml) {
				MissionPanel Testx = new MissionPanel(MidPanel, m);
				mlist.add(Testx);
				Testx.setEventListener(this);
				Testx.setBackground(Color.white);
				mission += 1;
				if (mission >= 6)
					MidLayout.setRows(MidLayout.getRows()+1);
				MidPanel.setSize(435, (MidLayout.getRows()+1)*70);
				MidPanel.add(Testx);
			}
			MidLayout.setVgap(2);
			MidLayout.layoutContainer(MidPanel);
			statusbar.flush("历史任务列表载入成功");
		}
	}
	
	private void ShowAbout(){
		Icon ico = new ImageIcon(getClass().getResource("/image/icon.png"));
		JOptionPane.showMessageDialog(rootPane, 
					"楼主跟我走2 \nBuild 130623 \n" +
					"so898 荣誉出品\n" +
					"作者微博: weibo.com/so898", "关于", JOptionPane.INFORMATION_MESSAGE, ico);
	}
	
	public void actionPerformed(ActionEvent a) {
		if (a.getSource() == Create || a.getSource() == create){
			CreateMissionPanel tmp_C = new CreateMissionPanel(this, true);
			if (tmp_C.CheckReturn()){
				MissionPanel Testx = new MissionPanel(MidPanel, tmp_C.ReturnMission());
				Testx.setEventListener(this);
				Testx.setBackground(Color.white);
				Collections.reverse(mlist);
				mlist.add(Testx);
				Collections.reverse(mlist);
				MakeList(1);
			}
		}
		else if (a.getSource() == about){
			ShowAbout();
		}
		else if (a.getSource() == help){
			new HelpDoc(this);
		}
		else if (a.getSource() == setting){
			new ConfigPanel(this);
		}else if (a.getSource() == clear_list){
			MidPanel.removeAll();
			MidPanel.updateUI();
			XMLInfo.RemoveAll();
			statusbar.flush("历史任务列表已清空");
		}else if (a.getSource() == sys_tray){
			if (sys_tray.getState()){
				Config.SetV("SystemTray","yes");
				cantray = true;
				try {
					tray.add(trayIcon);
				} catch (AWTException e1) {
					e1.printStackTrace();
				}
			}
			else{
				Config.SetV("SystemTray","no");
				cantray = false;
				SystemTray.getSystemTray().remove(trayIcon);
			}
		}else if (a.getSource() == exit){
			System.exit(0);
		}else if (a.getSource() == overwrite){
			Config.SetV("RewriteTxt","no");
		}
	}
	
	public static void main(String [] argv){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception	 e)	 {
			e.printStackTrace();
		}
		new Main();
	}

	public void handleEvent(EventSource event) {
		int eType = event.getEventType();
		switch(eType){
			case EventSource.EVENT_DELETE_MESSION:
				if (MidLayout.getRows() > 6)
					MidLayout.setRows(MidLayout.getRows()-1);
				MidPanel.setSize(435, (MidLayout.getRows()+1)*70);
				statusbar.flush("任务已从列表删除");
			default:
				break;
		}
		
	}
	
	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			System.exit(0);
		}
		else if (e.getID() == WindowEvent.WINDOW_ICONIFIED){
			if (cantray){
				setVisible(false);
				istray = true;
			}
		}
		else{
			super.processWindowEvent(e);
		}
	}

	private void tray() {
		tray = SystemTray.getSystemTray();
		ImageIcon icon = new ImageIcon(getClass().getResource("/image/tray.png"));
		PopupMenu pop = new PopupMenu();
		MenuItem show = new MenuItem("显示窗口");
		MenuItem about = new MenuItem("关于");
		MenuItem exit = new MenuItem("退出");
		trayIcon = new TrayIcon(icon.getImage(), "楼主跟我走2", pop);
		try{
			tray.add(trayIcon);
		}catch (AWTException ex) {
			ex.printStackTrace();
		}
		trayIcon.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && istray) {
					setExtendedState(Frame.NORMAL); 
					setVisible(true);
					istray = false;
				}
				else if (e.getClickCount() == 2 && !istray){
					setVisible(false);
					istray = true;
				}
			}
		});
		show.addActionListener(new ActionListener() {
	
			public void actionPerformed(ActionEvent e) {
				setExtendedState(Frame.NORMAL); 
				setVisible(true);
				istray = false;
			}
		});
		exit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		about.addActionListener(new ActionListener() { 

			public void actionPerformed(ActionEvent e) {
				ShowAbout();
			}
		});
		pop.add(show);
		pop.add(about);
		pop.add(exit);
	}
}
