package Main;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;

import AssistStaff.Mission;
import EventPack.Delete_Mission;
import EventPack.EventListener;
import GetLZ.GetLZ;
import GetLZ.GetLZ_Douban;
import GetLZ.GetLZ_Tianya;
import GetLZ.GetLZ_Tieba;


public class MissionPanel extends JPanel implements MouseListener{
	private JLabel processnum;
	private JProgressBar processbar;
	private boolean processing = true;
	private JPopupMenu pop;
	private JMenuItem restart, stop, detail, delete, open;
	private JPanel FatherPanel;
	private Mission m;
	private EventListener listener;
	private GetLZ mission;
	
	public MissionPanel(JPanel a, Mission tm){
		FatherPanel = a;
		m = tm;
		popMenu();
		setLayout(null);
		setPreferredSize(new Dimension(415, 80));
		
		if (Integer.parseInt(m.Type) / 100 == 1){
			ImageIcon icon = new ImageIcon(getClass().getResource("/image/txt_icon.png"));
			JLabel Icon = new JLabel(icon);
			Icon.setBounds(10, 25, 32, 32);
			add(Icon);
		}else if (Integer.parseInt(m.Type) % 100 / 10 == 1){
			ImageIcon icon = new ImageIcon(getClass().getResource("/image/pdf_icon.png"));
			JLabel Icon = new JLabel(icon);
			Icon.setBounds(10, 25, 32, 32);
			add(Icon);
		}else if (Integer.parseInt(m.Type) % 10 == 1){
			ImageIcon icon = new ImageIcon(getClass().getResource("/image/image_icon.png"));
			JLabel Icon = new JLabel(icon);
			Icon.setBounds(10, 25, 32, 32);
			add(Icon);
		}
		
		
		JLabel title = new JLabel(m.Title);
		title.setBounds(60, 10, 300, 20);
		add(title);
		
		processnum = new JLabel("0%");
		processnum.setBounds(360, 10, 100, 20);
		add(processnum);
		
		processbar = new JProgressBar();
		processbar.setMinimum(0);
		processbar.setMaximum(100);
		processbar.setBounds(55,50,350,20);
		add(processbar);
		
		if (!m.Done){
			switch(tm.webType){
				case 1:
					mission = new GetLZ_Douban(tm,this);
					break;
				case 2:
					mission = new GetLZ_Tieba(tm,this);
					break;
				case 3:
					mission = new GetLZ_Tianya(tm,this);
				default:
					break;
			}
				
		}
		else {
			changeProcess(100);
			processing = false;
		}
		
		setVisible(true);
	}
	
	public void changeProcess(int i){
		processnum.setText(i + "%");
		processbar.setValue(i);
	}
	
	private void popMenu() {
		this.addMouseListener(this);
		pop = new JPopupMenu();
		pop.add(open = new JMenuItem("打开文件位置"));
		pop.addSeparator();
		pop.add(restart = new JMenuItem("重新下载"));
		pop.add(stop = new JMenuItem("停止下载"));
		pop.add(delete = new JMenuItem("删除任务"));
		pop.addSeparator();
		pop.add(detail = new JMenuItem("属性"));
		restart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action(e);
			}
		});
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action(e);
			}
		});
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action(e);
			}
		});
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action(e);
			}
		});
		detail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action(e);
			}
		});
		this.add(pop);
	}

	public void action(ActionEvent a) {
		if (a.getSource() == delete){
			XMLInfo.Remove(m);
			FatherPanel.remove(this);
			FatherPanel.updateUI();
			this.listener.handleEvent(new Delete_Mission());
		}else if (a.getSource() == open){
		    try {
				java.awt.Desktop.getDesktop().open(new File(m.Path));
			} catch (IOException e) {
				e.printStackTrace();
			}  
		}else if (a.getSource() == detail){
		    new DetailPanel(m);
		}else if (a.getSource() == stop){
			mission.StopProcess();
			processing = false;
			m.Done = false;
		}else if (a.getSource() == restart){
			m.Done = false;
			XMLInfo.Restart(m);
			processing = true;
			switch(m.webType){
			case 1:
				mission = new GetLZ_Douban(m,this);
				break;
			case 2:
				mission = new GetLZ_Tieba(m,this);
				break;
			case 3:
				mission = new GetLZ_Tianya(m,this);
			default:
				break;
		}
		}
	}

	public JPopupMenu getPop() {
		return pop;
	}
	
	public void setEventListener(EventListener listener){  
        this.listener = listener;  
    }

	public void setPop(JPopupMenu pop) {
	}

	public void mouseClicked(MouseEvent arg0) {
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			if (processing)
				restart.setEnabled(false);
			else
				stop.setEnabled(false);
			pop.show(this, e.getX(), e.getY());
		}
	}

	public void mouseReleased(MouseEvent arg0) {
	}
	
	
}
