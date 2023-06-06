
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;


public class GamePanel extends JPanel {
	
	private GameBoard gameBoard;
	private Timer timer;
	private InputListener inputListener;
	private ResizeListener resizeListener;
	
	public GamePanel(){
		resizeListener = new ResizeListener();
		inputListener = new InputListener();
		addComponentListener(resizeListener);	
		addMouseListener(inputListener);
		addMouseMotionListener(inputListener);
		gameBoard = new GameBoard(this);	
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask(){
			@Override
			public void run() {
				update();
				repaint();
			}	
		}, 0, 33);
	}

	public void update(){
		gameBoard.update();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//Setting background to white
		setBackground(Color.WHITE);
		gameBoard.draw(g2d);
	}
	
	public GameBoard getGB(){
		return gameBoard;
	}
	
	public ResizeListener getRL(){
		return resizeListener;
	}
	
	public InputListener getIL(){
		return inputListener;
	}
}
