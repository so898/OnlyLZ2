package Main;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import AssistStaff.Mission;


public class DetailPanel extends JFrame implements ActionListener{
	private JTextField title, file, url, done;
	private JButton close;
	
	public DetailPanel(Mission m){
		setIconImage(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/image/icon.png")));
		setSize(320,400);
		setTitle("详细信息");
		setLayout(null);
		setResizable(false);
		int width=0;
		int height=0;
		width=Toolkit.getDefaultToolkit().getScreenSize().width;	
		height=Toolkit.getDefaultToolkit().getScreenSize().height;	
		setLocation((width - 400) / 2, (height - 270)	 /	 2);
		JLabel one = new JLabel("文章标题：");
		one.setBounds(20, 10, 200, 20);
		add(one);
		title = new JTextField(100);
		title.setBounds(20, 40, 280, 20);
		title.setText(m.Title);
		title.setEditable(false);
		add(title);
		JLabel two = new JLabel("作者：");
		two.setBounds(20, 70, 200, 20);
		add(two);
		file = new JTextField(100);
		file.setBounds(20, 100, 280, 20);
		file.setText(m.Author);
		file.setEditable(false);
		add(file);
		JLabel three = new JLabel("保存位置：");
		three.setBounds(20, 130, 200, 20);
		add(three);
		file = new JTextField(100);
		file.setBounds(20, 160, 280, 20);
		file.setText(m.Path);
		file.setEditable(false);
		add(file);
		JLabel four = new JLabel("原始网页：");
		four.setBounds(20, 190, 200, 20);
		add(four);
		url = new JTextField(100);
		url.setBounds(20, 220, 280, 20);
		url.setText(m.Url);
		url.setEditable(false);
		add(url);
		JLabel five = new JLabel("是否下载完成：");
		five.setBounds(20, 250, 200, 20);
		add(five);
		done = new JTextField(100);
		done.setBounds(20, 280, 280, 20);
		if (m.Done)
			done.setText("是");
		else
			done.setText("否");
		done.setEditable(false);
		add(done);
		
		close = new JButton("关闭");
		close.setBounds(100, 330, 120, 30);
		close.addActionListener(this);
		add(close);
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent a) {
		if (a.getSource()==close){
			setVisible(false);
		}
	}

}
