package AssistStaff;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

public class WebTextField extends JTextField implements MouseListener {

	private static final long serialVersionUID = 6471048622336276898L;

	private JPopupMenu pop;
	private JMenuItem  paste;
	private JMenuItem clear;

	public WebTextField(int i) {  
		super(i);
		init();
	}

	private void init() {
		this.addMouseListener(this);
		pop = new JPopupMenu();
		pop.add(paste = new JMenuItem("’≥Ã˘Õ¯÷∑"));
		pop.add(clear = new JMenuItem("«Âø’ ‰»Î"));
		paste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action(e);
			}
		});
		clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action(e);
			}
		});
		this.add(pop);
	}
  
	public void action(ActionEvent e) {
		String str = e.getActionCommand();
		if (str.equals(paste.getText())) {
			this.paste();
		} else if (str.equals(clear.getText())) {
			this.setText("");
		}
	}

	public JPopupMenu getPop() {
		return pop;
	}

	public void setPop(JPopupMenu pop) {
		this.pop = pop;
	}

	public boolean isClipboardString() {
		boolean b = false;
		Clipboard clipboard = this.getToolkit().getSystemClipboard();
		Transferable content = clipboard.getContents(this);
		try {
			if (content.getTransferData(DataFlavor.stringFlavor) instanceof String) {
				b = true;
			}
		} catch (Exception e) {}
		return b;
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			paste.setEnabled(isClipboardString());
			pop.show(this, e.getX(), e.getY());
		}
	}

	public void mouseReleased(MouseEvent e) {
	}

}