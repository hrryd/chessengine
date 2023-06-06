
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;


public class Interface extends JFrame {
	public static void main(String[] args){
		try {
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		} catch (Exception e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				Interface gui = new Interface();
			}
		});
	}
	
	private GamePanel gamePanel;
	
	public Interface(){
		gamePanel = new GamePanel();
		setTitle("Chess");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 800);
		setLocationRelativeTo(null);
		add(gamePanel);
		setJMenuBar(new GameMenuBar(gamePanel));
		setVisible(true);
	}
}
