package Main;

import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class CheckDialog extends JDialog{
	private JLabel showtext;
	public CheckDialog(JDialog root,String title,String text){
		super(root);
		setSize(320,60);
		setTitle(title);
		setResizable(false);
		setLayout(null);
		int width=0;
		int height=0;
		width=Toolkit.getDefaultToolkit().getScreenSize().width;	
		height=Toolkit.getDefaultToolkit().getScreenSize().height;	
		setLocation((width - 320)/2, (height - 60)/2);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		showtext = new JLabel(text);
		showtext.setBounds(10,5,200,20);
		add(showtext);
		setVisible(true);
	}
	
	public void Disappear(){
		setVisible(false);
	}
	
	public void ResetTitle(String title){
		setTitle(title);
	}
	
	public void ResetText(String text){
		showtext.setText(text);
	}
	
}
