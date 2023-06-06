

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class ResizeListener extends ComponentAdapter {

	private final double OFFSET = 0.05;
	public static final double PIECE_FILL = 0.7;
	private int xOffset;
	private int yOffset; 
	private int squareSize;
	
	public ResizeListener(){

	}

	@Override
	public void componentResized(ComponentEvent e) {
		super.componentResized(e);
		//Board re-placement
		GamePanel panel = (GamePanel) e.getComponent();
		squareSize = (int) ((panel.getHeight()*(1-OFFSET*2))/8.0);
		squareSize = (int) Math.min(squareSize, (panel.getWidth()*(1-OFFSET*2))/8.0);
		xOffset = (panel.getWidth()-squareSize*8)/2;
		yOffset = (int) (panel.getHeight()-squareSize*8)/2;
		//Piece re-scaling
		GameBoard gameBoard = panel.getGB();
	    for(int n = 0; n < gameBoard.getPieceImgs().length; n++){
	    	if(gameBoard.getOriginalPieceImgs()[n] != null){
	    		int width = gameBoard.getOriginalPieceImgs()[n].getWidth();
	    		int height = gameBoard.getOriginalPieceImgs()[n].getHeight();
	    		int max = Math.max(width, height);
		    	double scale = (double) PIECE_FILL*squareSize / max;
		    	AffineTransform scaleTransform = AffineTransform.getScaleInstance(scale, scale);
			    AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);
			    gameBoard.getPieceImgs()[n] = bilinearScaleOp.filter(gameBoard.getOriginalPieceImgs()[n], 
			    		new BufferedImage((int) (width*scale), (int) (height*scale), gameBoard.getOriginalPieceImgs()[n].getType()));
	    	}
	    }
	}	
	
	public int getXOffset(){
		return xOffset;
	}
	
	public int getYOffset(){
		return yOffset;
	}
	
	public int getSquareSize(){
		return squareSize;
	}
}
