package Main;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class HelpDoc implements ActionListener{
	private JButton Donate, Close, Connect;
	private JDialog helpdoc;
	public HelpDoc(JFrame root){
		helpdoc = new JDialog(root,true);
		helpdoc.setSize(400,500);
		helpdoc.setTitle("帮助文档");
		helpdoc.setResizable(false);
		helpdoc.setIconImage(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/image/icon.png"))); 

		int width=0;
		int height=0;
		width=Toolkit.getDefaultToolkit().getScreenSize().width;
		height=Toolkit.getDefaultToolkit().getScreenSize().height;
		helpdoc.setLocation((width - 400) / 2, (height - 500) / 2);

		JPanel help_doc = new JPanel();
		helpdoc.setLayout(null);

		JTextArea text = new JTextArea();
		text.setLineWrap(true);
		text.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(text);
		help_doc.setLayout(new BorderLayout());
		help_doc.setBounds(10, 15, 380, 400);
		help_doc.add(scrollPane,BorderLayout.CENTER);
		helpdoc.add(help_doc);

		InputStreamReader isr=new InputStreamReader(HelpDoc.class.getClassLoader().getResourceAsStream("help.txt"));
		BufferedReader in = new BufferedReader(isr);
		try{
			String tempx = in.readLine();
			while (!tempx.equals(null)){
				String c_line = System.getProperty("line.separator");
				text.append(tempx + c_line);
				tempx = in.readLine();
			}
			//System.out.println(temp);
		}
		catch(Exception e){

		}

		Donate = new JButton("捐 助");
		Donate.setBounds(10,430,110,30);
		Donate.addActionListener(this);
		helpdoc.add(Donate);
		Connect = new JButton("联系作者");
		Connect.setBounds(140,430,110,30);
		Connect.addActionListener(this);
		helpdoc.add(Connect);
		Close = new JButton("关闭帮助页面");
		Close.setBounds(270,430,110,30);
		Close.addActionListener(this);
		helpdoc.add(Close);
		text.setCaretPosition(0);
		helpdoc.setVisible(true);
	}

	public void actionPerformed(ActionEvent a) {
		if (a.getSource() == Close ){
			helpdoc.setVisible(false);
			helpdoc.dispose();
		}
		else if (a.getSource() == Donate){
			java.net.URI uri = null;
			try {
				uri = new java.net.URI("http://me.alipay.com/so898");
				java.awt.Desktop.getDesktop().browse(uri);
			} catch (URISyntaxException e) {}
			catch (IOException e) {}
		}
		else if (a.getSource() == Connect){
			java.net.URI uri = null;
			try {
				uri = new java.net.URI("http://about.me/so898");
				java.awt.Desktop.getDesktop().browse(uri);
			} catch (URISyntaxException e) {}
			catch (IOException e) {}
		}
	}
}