package Main;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import AssistStaff.CheckProxy;
import AssistStaff.Config;
import AssistStaff.Encrypt;
import EventPack.EventListener;
import EventPack.EventSource;

public class SetProxy extends JDialog implements ActionListener, EventListener{
	private JCheckBox proxy_auth;
	private JLabel jhost, jport, juser, jpassword;
	private JTextField proxy_host, proxy_u;
	private JPasswordField proxy_p;
	private JButton ok, cancel;
	private JFormattedTextField proxy_port;
	private CheckDialog dialog;
	
	public SetProxy(ConfigPanel configPanel){
		super(configPanel);
		setLayout(null);
		setSize(320,240);
		setTitle("代理设置");
		setResizable(false);
		int width=0;
		int height=0;
		width=Toolkit.getDefaultToolkit().getScreenSize().width;	
		height=Toolkit.getDefaultToolkit().getScreenSize().height;	
		setLocation((width - 320)/2, (height - 240)/2);
		
		JPanel proxy_one = new JPanel();
		proxy_one.setBounds(10,10,290,60);
		proxy_one.setLayout(null);
		proxy_one.setBackground(Color.white);
		proxy_one.setBorder(BorderFactory.createLineBorder(Color.black));
		add(proxy_one);
		
		jhost = new JLabel("代理地址：");
		jhost.setBounds(10,5,100,20);
		proxy_one.add(jhost);
		
		proxy_host = new JTextField(20);
		proxy_host.setBounds(70,5,200,20);
		proxy_one.add(proxy_host);
		proxy_host.setText(Config.GetV("ProxyHost"));
		
		jport = new JLabel("端口：");
		jport.setBounds(20,35,100,20);
		proxy_one.add(jport);
		
		NumberFormat e=NumberFormat.getIntegerInstance();
		e.setGroupingUsed(false);
		proxy_port=new JFormattedTextField(e);
		proxy_port.setBounds(70,35,200,20);
		proxy_one.add(proxy_port);
		proxy_port.setText(Config.GetV("ProxyPort"));
		
		proxy_auth = new JCheckBox("网络代理验证");
		proxy_auth.setBounds(10,70,120,30);
		proxy_auth.addActionListener(this);
		add(proxy_auth);
		
		JPanel proxy_two = new JPanel();
		proxy_two.setBounds(10,100,290,60);
		proxy_two.setLayout(null);
		proxy_two.setBackground(Color.white);
		proxy_two.setBorder(BorderFactory.createLineBorder(Color.black));
		add(proxy_two);
		
		juser = new JLabel("用户名：");
		juser.setBounds(15,5,100,20);
		proxy_two.add(juser);
		
		proxy_u = new JTextField(20);
		proxy_u.setBounds(70,5,200,20);
		proxy_two.add(proxy_u);
		proxy_u.setText(Config.GetV("Proxy_username"));
		
		jpassword = new JLabel("密码：");
		jpassword.setBounds(20,35,100,20);
		proxy_two.add(jpassword);
		
		proxy_p = new JPasswordField(20);
		proxy_p.setBounds(70,35,200,20);
		proxy_two.add(proxy_p);
		proxy_p.setText(Config.GetV("Proxy_password"));
		
		if (Config.GetV("Proxy_Auth").equals("yes")){
			proxy_auth.setSelected(true);
		}
		else{
			juser.setEnabled(false);
			proxy_u.setEnabled(false);
			jpassword.setEnabled(false);
			proxy_p.setEnabled(false);
		}
		
		if (Config.GetV("Proxy").equals("no")){
			proxy_auth.setEnabled(false);
			jhost.setEnabled(false);
			proxy_host.setEnabled(false);
			jport.setEnabled(false);
			proxy_port.setEnabled(false);
			juser.setEnabled(false);
			proxy_u.setEnabled(false);
			jpassword.setEnabled(false);
			proxy_p.setEnabled(false);
		}
		
		ok = new JButton("确定");
		ok.setBounds(10,170,100,30);
		ok.addActionListener(this);
		add(ok);
		
		cancel = new JButton("取消");
		cancel.setBounds(180,170,100,30);
		cancel.addActionListener(this);
		add(cancel);
		
		
		setVisible(true);
	}

	public void actionPerformed(ActionEvent a) {
		if (a.getSource() == proxy_auth){
			if (Config.GetV("Proxy_Auth").equals("yes")){
				proxy_auth.setSelected(false);
				Config.SetV("Proxy_Auth","no");
				juser.setEnabled(false);
				proxy_u.setEnabled(false);
				jpassword.setEnabled(false);
				proxy_p.setEnabled(false);
			}
			else{
				Config.SetV("Proxy_Auth","yes");
				juser.setEnabled(true);
				proxy_u.setEnabled(true);
				jpassword.setEnabled(true);
				proxy_p.setEnabled(true);
			}
		}
		else if (a.getSource() == cancel){
			this.setVisible(false);
		}
		else if (a.getSource() == ok){
			dialog = new CheckDialog(this, "检查代理", "正在检查代理设置......");
			CheckProxy xb =new CheckProxy();
			xb.setEventListener(this);
			xb.Start();
		}
	}
	
	private void SaveAll(){
		Config.SetV("ProxyHost", proxy_host.getText());
		Config.SetV("ProxyPort", proxy_port.getText());
		Config.SetV("ProxyUsername", proxy_u.getText());
		Config.SetV("ProxyPassword", Encrypt.Encode(proxy_p.getPassword()));
	}

	public void handleEvent(EventSource event) {
		int eType = event.getEventType();
		switch(eType){
			case EventSource.EVENT_PROXY_SUCCESS:
				dialog.Disappear();
				setVisible(false);
				SaveAll();
				Config.SetV("ProxyAccess", "yes");
				break;
			case EventSource.EVENT_PROXY_FAIL:
				dialog.Disappear();
				JOptionPane.showMessageDialog(this, "代理测试失败，请检查您的设置。","代理错误",JOptionPane.ERROR_MESSAGE);
				break;
			default:
				break;
		}
		
	}
}
