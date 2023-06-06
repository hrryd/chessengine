

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

public class MenuListener implements ActionListener {
	
	private GamePanel panel;
	
	public MenuListener(GamePanel panel){
		this.panel = panel;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String itemText = ((JMenuItem) e.getSource()).getText();
		GameBoard gb = panel.getGB();
		switch(itemText){
			case "New Game": 
				gb.newGame(); 
				break;
			case "Save Game": 
				FileManager.initSaveBoard(gb); 
				break;
			case "Load Game":
				FileManager.loadBoard(gb);
				break;
			case "Move Log":
				break;
		}
	}
}
