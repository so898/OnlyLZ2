package Main;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import AssistStaff.Config;


public class ConfigPanel extends JDialog implements ActionListener{
	private JButton C_Path1,C_Path2,ok,d_set;
	private JTextField S_Path1,S_Path2;
	private JCheckBox in_save_page,in_overwrite,sperator;
	private JComboBox check;
	public ConfigPanel(JFrame root){
		super(root,true);
		String [] jiange = {"-","*","=","#","@","~"};
		setIconImage(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/image/icon.png")));
		setSize(400,300);
		setTitle("设置");
		
		setLayout(null);
		setResizable(false);
		int width=0;
		int height=0;
		width=Toolkit.getDefaultToolkit().getScreenSize().width;	
		height=Toolkit.getDefaultToolkit().getScreenSize().height;	
		setLocation((width - 400) / 2, (height - 270)	 /	 2);
		JLabel one = new JLabel("默认保存位置：");
		one.setBounds(30, 10, 200, 20);
		add(one);
		S_Path1 = new JTextField(20);
		S_Path1.setBounds(30, 40, 230, 30);
		S_Path1.setText(Config.GetV("StorePath"));
		S_Path1.setEditable(false);
		add(S_Path1);
		C_Path1 = new JButton("选择目录");
		C_Path1.setBounds(270,40,100,30);
		C_Path1.addActionListener(this);
		add(C_Path1);
		JLabel two = new JLabel("缓存网页保存位置：");
		two.setBounds(30, 80, 200, 20);
		add(two);
		S_Path2 = new JTextField(20);
		S_Path2.setBounds(30, 110, 230, 30);
		S_Path2.setText(Config.GetV("TempPath"));
		S_Path2.setEditable(false);
		add(S_Path2);
		C_Path2 = new JButton("选择目录");
		C_Path2.setBounds(270,110,100,30);
		C_Path2.addActionListener(this);
		
		sperator = new JCheckBox("启用楼层间隔符");
		sperator.setBounds(50,150,140,30);
		sperator.addActionListener(this);
		add(sperator);
		
		JLabel louceng = new JLabel("间隔符：");
		louceng.setBounds(220,150,70,30);
		add(louceng);
		check = new JComboBox(jiange);
		check.setBounds(270,150,70,30);
		check.addActionListener(this);
		add(check);
		
		if (Config.GetV("BetweenLine").equals("yes")){
			sperator.setSelected(true);
			for(int i =0; i<jiange.length;i++)
				if (Config.GetV("LineMark").equals(jiange[i])){
					check.setSelectedIndex(i);
				}
		}
		else{
			check.setEnabled(false);
		}
		
		add(C_Path2);
		in_save_page = new JCheckBox("保存缓存页面");
		in_save_page.setBounds(50,190,120,30);
		in_save_page.addActionListener(this);
		add(in_save_page);
		in_overwrite = new JCheckBox("覆盖文本");
		in_overwrite.setBounds(220,190,120,30);
		in_overwrite.addActionListener(this);
		add(in_overwrite);
		if (!Config.GetV("DownloadTemp").equals("yes")){
			C_Path2.setEnabled(false);
			in_save_page.setSelected(false);
		}
		else{
			in_save_page.setSelected(true);
		}
		if (Config.GetV("RewriteTxt").equals("yes")){
			in_overwrite.setSelected(true);
		}
		
		ok = new JButton("确定");
		ok.setBounds(55, 230, 120, 30);
		ok.addActionListener(this);
		add(ok);
		d_set = new JButton("还原初始设定");
		d_set.setBounds(215, 230, 120, 30);
		d_set.addActionListener(this);
		add(d_set);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent a) {
		if (a.getSource() == C_Path1){
			JFileChooser chooser = new JFileChooser(Config.GetV("StorePath"));
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			add(chooser);
			int returnVal = chooser.showOpenDialog(getParent());
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				String path = chooser.getSelectedFile().getAbsolutePath();
				S_Path1.setText(path);
				Config.SetV("StorePath",path);
			}
		}
		else if (a.getSource() == C_Path2){
			JFileChooser chooser = new JFileChooser(Config.GetV("TempPath"));
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			add(chooser);
			int returnVal = chooser.showOpenDialog(getParent());
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				String path = chooser.getSelectedFile().getAbsolutePath();
				S_Path2.setText(path);
				Config.SetV("TempPath",path);
			}
		}
		else if (a.getSource() == in_save_page){
			if (in_save_page.isSelected()){
				C_Path2.setEnabled(true);
				Config.SetV("DownloadTemp","yes");
			}
			else{
				C_Path2.setEnabled(false);
				Config.SetV("DownloadTemp","no");
			}
		}
		else if (a.getSource() == in_overwrite){
			if (in_overwrite.isSelected()){
				Config.SetV("RewriteTxt","yes");
			}
			else{
				Config.SetV("RewriteTxt","no");
			}
		}
		else if (a.getSource() == sperator){
			if (Config.GetV("BetweenLine").equals("yes")){
				sperator.setSelected(false);
				Config.SetV("BetweenLine","no");
				check.setEnabled(false);
			}
			else{
				sperator.setSelected(true);
				Config.SetV("BetweenLine","yes");
				check.setEnabled(true);
				String part_line_mark = (String) check.getSelectedItem();
				Config.SetV("LineMark",part_line_mark);
			}
		}
		else if (a.getSource() == check){
			String part_line_mark = (String) check.getSelectedItem();
			Config.SetV("LineMark",part_line_mark);
		}
		else if (a.getSource() == d_set){
			Config.CheckConfig();
		}
		else if (a.getSource() == ok){
			dispose();
		}
	}
}
