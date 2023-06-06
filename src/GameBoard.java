
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;


public class GameBoard {

	private GamePanel panel;
	private Renderer renderer;
	private Generation generation;
	private OpeningRetreiver openingRetriever;
	private Thread engineThread;
	private Engine engineRunnable;
	private TimeManager timeManager;
	private GameDetails gameDetails;

	private BufferedImage[] pieces; 
	private BufferedImage[] originalPieces; 
	private boolean firstDraw = true;
	
	private int pickedUpI = -1;
	private int hoverI = -1;
	private int moveI = -1;
	private int moveS = -1;
	private int promote = -1;
	
	private int[] board;
	private int ply = 0;
	private int toMove = Constant.FIRST_MOVE;
	
	public GameBoard(GamePanel panel){
		this.panel = panel;
		gameDetails = new GameDetails(this);
		renderer = new Renderer(this);
		generation = new Generation();
		openingRetriever = new OpeningRetreiver();
		timeManager = new TimeManager();
		engineRunnable = new Engine(board, this);
		engineThread = new Thread(engineRunnable);
		initialise();
		reset();
	}
	
	public void reset() {	
		pickedUpI = -1;
		hoverI = -1;
		moveI = -1;
		moveS = -1;
		promote = -1;
		board = PC.newBoard.clone();
		ply = 0;
		toMove = Constant.FIRST_MOVE;
		gameDetails.reset();
		generation.reset(board);
		engineRunnable.newGame();
		timeManager.reset();
		timeManager.beginMove(1);
		gameDetails.setVisible(true);
	}

	public void draw(Graphics2D g2d) {
		if(firstDraw){
			firstDraw = false;
			return;
		}
		int xOffset = panel.getRL().getXOffset();
		int yOffset = panel.getRL().getYOffset();
		int squareSize = panel.getRL().getSquareSize();
		//Drawing board
		renderer.drawSquares(g2d, board);
		//Drawing pieces
		renderer.drawPieces(g2d, board);
		//Drawing border
		g2d.setColor(Color.DARK_GRAY);
		Stroke oldStroke = g2d.getStroke();
		g2d.setStroke(new BasicStroke(2));
		g2d.drawRect(xOffset, yOffset, 8*squareSize, 8*squareSize);
		g2d.setStroke(oldStroke);
		//Drawing time
		renderer.drawTime(g2d);
		//Drawing names
		renderer.drawNames(g2d);
		//Drawing pickedup piece
		renderer.drawPickedUp(g2d, board);
	}

	public void update() {
		if(firstDraw){
			return;
		}
		if(gameDetails.isVisible()){
			timeManager.reset();
			timeManager.beginMove(1);
			return;
		}
		int mouseX = panel.getIL().getMouseX();
		int mouseY = panel.getIL().getMouseY();
		int size = panel.getRL().getSquareSize();
		int xOff = panel.getRL().getXOffset();
		int yOff = panel.getRL().getYOffset();
		int xDim = 8*size;
		int yDim = 8*size;
		int relX;
		int relY;
		if(Constant.HUMAN_PLAYER == 1){
			relX = mouseX - xOff;
			relY = yDim - (mouseY - yOff);
		}else{
			relX = xDim - (mouseX - xOff);
			relY = mouseY - yOff;
		}
		
		//Square highlighting and hover stuff..
		if(Constant.HUMAN_PLAYER == toMove || Constant.TWO_PLAYER){
			if(relX >= 0 && relX < xDim && relY >= 0 && relY < yDim){
				int squareX = relX / size;
				int squareY = relY / size;
				if(panel.getIL().mouseDown(0) && pickedUpI == -1){
					pickedUpI = squareX + squareY*16;
					if(board[pickedUpI]*Constant.HUMAN_PLAYER <= 0) pickedUpI = -1;
				}
				if(!panel.getIL().mouseDown(0) || pickedUpI != -1){
					hoverI = squareX + squareY*16;
				}
				if(pickedUpI != -1 && !panel.getIL().mouseDown(0)){
					int moveS = squareX + squareY*16;
					ArrayList<int[]> moves = generation.moveGen(board, toMove, false);
					int[] move = null;
					for(int[] m : moves){
						if(m[0] == pickedUpI && m[1] == moveS){
							move = m;
							break;
						}
					}	
					if(move != null){
						if(move[5] != 0){
							Scanner in = new Scanner(System.in);
							PiecePicker pick = new PiecePicker(this);
							move[5] = -1;
							while(move[5] == -1){ //waiting for piece promotion selection
								move[5] = promote;
								try {
									Thread.sleep(33);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}	
							promote = -1;
						}
						generation.makeMove(board, move[0], move[1], move[2], move[3], move[4], move[5]);
						setLastMove(move[0], move[1]);
						moveComplete();
					}
				}
			}
		}	
		if(!panel.getIL().mouseDown(0)) pickedUpI = -1;
		timeManager.update();
		if(!engineThread.isAlive()){
			engineThread.start();
		}
	}
	
	//Reads in images from files
	public void initialise() {
		originalPieces = new BufferedImage[12];
		pieces = new BufferedImage[12];
		BufferedImage pieceImg = null;
		int pieceNo = 0;
		try{
			pieceImg = ImageIO.read(new File(Constant.PIECE_IMG_FILE));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			BufferedReader br = new BufferedReader(new FileReader(Constant.PIECE_TXT_FILE));
			try {
			    String line = br.readLine();
			    while (line != null) {
			    	String[] detail = line.split("#")[1].split(",");
			    	int[] locs = new int[4];
			    	for(int n = 0; n < 4; n++) locs[n] = Integer.parseInt(detail[n]);
			    	originalPieces[pieceNo] = pieceImg.getSubimage(locs[0], locs[1], locs[2], locs[3]);
			        pieceNo++;
			        line = br.readLine();
			    }
			} finally {
				br.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		panel.setSize(panel.getSize()); //forces onResize()
	}
	
	public GamePanel getPa(){
		return panel;
	}

	public BufferedImage[] getPieceImgs(){
		return pieces;
	}
	
	public BufferedImage[] getOriginalPieceImgs(){
		return originalPieces;
	}
	
	public Generation getMoveGen(){
		return generation;
	}
	
	public OpeningRetreiver getOpRet(){
		return openingRetriever;
	}
	
	public TimeManager getTimeManager() {
		return timeManager;
	}
	
	public Engine getEngine() {
		return engineRunnable;
	}
	
	public GameDetails getGD(){
		return gameDetails;
	}
	
	//the 120 board coordinate of the picked up piece square
	public int getPickedUpI(){
		return pickedUpI;
	}
	
	public int getHoverI(){
		return hoverI;
	}

	public int getPly() {
		return ply;
	}

	public void setLastMove(int i, int s) {
		moveI = i;
		moveS = s;
	}
	
	public int getLastI(){
		return moveI;
	}
	
	public int getLastS(){
		return moveS;
	}

	public int getToMove() {
		return toMove;
	}

	public void moveComplete() {
		if(toMove == 1) toMove = -1;
		else toMove = 1;
	}

	public int[] getBoard() {
		return board;
	}
	
	public void setBoard(int[] board) {
		this.board = board;
	}

	public void setToMove(int tm) {
		toMove = tm;
	}

	public void newGame() {
		engineRunnable.setReady(false);
		engineRunnable.endGame();
		if(panel.getGB().getToMove() != Constant.HUMAN_PLAYER){
			while(engineRunnable.getReady() != true){
				try {
					Thread.sleep(33);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}	
		panel.getGB().reset();
		engineRunnable.newGame();
	}

	public void setPromotion(int selectedId) {
		promote = selectedId;
	}
}
