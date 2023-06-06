import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Renderer {
	
	private GameBoard gameBoard;
	private ResizeListener rl;
	
	public Renderer(GameBoard gameBoard){
		this.gameBoard = gameBoard;
		 rl = gameBoard.getPa().getRL();
	}
	
	public int convertX(int i){
		if(Constant.HUMAN_PLAYER == 1){
			return i;
		}else{
			return 119-i;
		}
	}
	
	public int convertY(int i){
		if(Constant.HUMAN_PLAYER == 1){
			return 119-i;
		}else{
			return i;
		}
	}
	
	public void drawPieces(Graphics2D g2d, int[] board){
		int size = rl.getSquareSize();
		for(int i = 0; i < board.length; i++){
			if((i & 0x88) != 0) continue;
			if(gameBoard.getPickedUpI() == i) continue;
			int colOff = board[i] > 0 ? 0 : 1;
			int piece = Math.abs(board[i]);
			if(piece == 0) continue;
			BufferedImage img = gameBoard.getPieceImgs()[(piece-1)*2+colOff];
			
			int pieceX = convertX(i)%16;
			int pieceY = convertY(i)/16;
			
			int squareX = pieceX*size+rl.getXOffset();
			int squareY = pieceY*size+rl.getYOffset();
			int drawX = (int) (squareX+(size-img.getWidth())/2.0);
			int drawY = (int) (squareY+(size*(1-ResizeListener.PIECE_FILL))/2.0);
			
			g2d.drawImage(img, null, drawX, drawY);
		}
	}
	
	//board paramater not used for now..
	public void drawSquares(Graphics g2d, int[] board){
		int size = rl.getSquareSize();
		for(int i = 0; i < board.length; i++){
			if((i & 0x88) != 0) continue;

			int pieceX = convertX(i)%16;
			int pieceY = convertY(i)/16;
			int drawX = pieceX*size+rl.getXOffset();
			int drawY = pieceY*size+rl.getYOffset();
			
			int pickedUpI = gameBoard.getPickedUpI();
			int hoverI = gameBoard.getHoverI();
			int lastI = gameBoard.getLastI();
			int lastS = gameBoard.getLastS();
			boolean whiteSquare = (i+i/16)%2 == 1;
			
			Color color = Constant.BLACK_COLOR;
			if(!whiteSquare && i == hoverI) color = Constant.BLACK_LIGHT_COLOR;
			if(!whiteSquare && i == pickedUpI) color = Constant.BLACK_YELLOW_COLOR;
			if(!whiteSquare && (i == lastI || i == lastS)) color = Constant.BLACK_YELLOW_COLOR;
			
			if(whiteSquare) color = Constant.WHITE_COLOR;
			if(whiteSquare && i == hoverI) color = Constant.WHITE_LIGHT_COLOR;
			if(whiteSquare && i == pickedUpI) color = Constant.WHITE_YELLOW_COLOR;
			if(whiteSquare && (i == lastI || i == lastS)) color = Constant.WHITE_YELLOW_COLOR;
			
			g2d.setColor(color);
			g2d.fillRect(drawX, drawY, size, size);
		}
	}

	public void drawPickedUp(Graphics2D g2d, int[] board) {
		int pickedUpI = gameBoard.getPickedUpI();
		if(pickedUpI != -1){
			int colOff = board[pickedUpI] > 0 ? 0 : 1;
			int piece = Math.abs(board[pickedUpI]);
			BufferedImage img = gameBoard.getPieceImgs()[(piece-1)*2+colOff];
			int drawX = (int) (gameBoard.getPa().getIL().getMouseX() - img.getWidth()/2.0);
			int drawY = (int) (gameBoard.getPa().getIL().getMouseY() - img.getHeight()/2.0);
			g2d.drawImage(img, null, drawX, drawY);
		}
	}
	
	public void drawTime(Graphics2D g2d) {
		int size = rl.getSquareSize();
		TimeManager tM = gameBoard.getTimeManager();
		int p1Time = (int) (tM.getMoveTime(1)/1000.0);
		int p2Time = (int) (tM.getMoveTime(2)/1000.0);
		
		g2d.setFont(g2d.getFont().deriveFont(20f*(size/83f)).deriveFont(Font.BOLD));
		
		String p1TimeStr = p1Time/60 + ":" + (p1Time%60<10 ? "0":"") + p1Time%60;
		int p1Width = g2d.getFontMetrics().stringWidth(p1TimeStr);
		int p1Height = g2d.getFontMetrics().getHeight();
		if(gameBoard.getToMove() == Constant.HUMAN_PLAYER) g2d.setColor(Color.RED);
		else g2d.setColor(Color.DARK_GRAY);
		g2d.drawString(p1TimeStr, rl.getXOffset()+size*8-p1Width-Constant.TEXT_PADDING, 
				rl.getYOffset()+size*8+p1Height);
		
		String p2TimeStr = p2Time/60 + ":" + (p2Time%60<10 ? "0":"") + p2Time%60;
		int p2Width = g2d.getFontMetrics().stringWidth(p2TimeStr);
		if(gameBoard.getToMove() != Constant.HUMAN_PLAYER) g2d.setColor(Color.RED);
		else g2d.setColor(Color.DARK_GRAY);
		g2d.drawString(p2TimeStr, rl.getXOffset()+size*8-p2Width-Constant.TEXT_PADDING, 
				rl.getYOffset()-Constant.TEXT_PADDING);
	}
	
	public void drawNames(Graphics2D g2d) {
		GameDetails gd = gameBoard.getGD();
		String firstName = gd.getFirstName();
		String lastName = gd.getLastName();

		g2d.setColor(Color.DARK_GRAY);
		int height = g2d.getFontMetrics().getHeight();
		int width = g2d.getFontMetrics().stringWidth(firstName+" ");
		g2d.drawString(firstName, rl.getXOffset()+8, 
				rl.getYOffset()-8);
		
		g2d.drawString(lastName, rl.getXOffset()+8+width, 
				rl.getYOffset()-8);
	}
}
