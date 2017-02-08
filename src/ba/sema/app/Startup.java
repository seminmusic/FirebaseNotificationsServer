package ba.sema.app;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;


public class Startup 
{
	public Startup() 
	{
		super();
	}
	
	public static void main(String[] args) {
		//
		JFrame frame = new JFrame("Firebase Notifications Server");
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
	    frame.setLocation(x - 250, y - 175);
	    frame.setResizable(false);
	    //
		MainPanel panel = new MainPanel();
		panel.setLayout(null);
		panel.postaviKomponente();
		//
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.velicinaPanela());
		frame.setVisible(true);
	}
}
