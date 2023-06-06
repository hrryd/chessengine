
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


public class GameMenuBar extends JMenuBar {
	public GameMenuBar(GamePanel panel){
		MenuListener menuListener = new MenuListener(panel);
		JMenu fileMenu = new JMenu("File");
		//JMenu optionsMenu = new JMenu("Options");
		JMenuItem newGameItem = new JMenuItem("New Game");
		JMenuItem saveGameItem = new JMenuItem("Save Game");
		JMenuItem loadGameItem = new JMenuItem("Load Game");
		JMenuItem moveLogItem = new JMenuItem("Move Log");
		newGameItem.addActionListener(menuListener);
		saveGameItem.addActionListener(menuListener);
		loadGameItem.addActionListener(menuListener);
		moveLogItem.addActionListener(menuListener);
		fileMenu.add(newGameItem);
		fileMenu.add(saveGameItem);
		fileMenu.add(loadGameItem);
		//fileMenu.add(moveLogItem);
		add(fileMenu);
		//add(optionsMenu);
	}
}
