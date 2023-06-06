import java.awt.Color;
import java.io.File;

public class Constant {
	public static final Color WHITE_COLOR = new Color(240, 217, 181);
	public static final Color BLACK_COLOR = new Color(181, 136, 99);
	public static final Color WHITE_YELLOW_COLOR = new Color(247, 236, 116);
	public static final Color BLACK_YELLOW_COLOR = new Color(218, 195, 74);
	public static final Color WHITE_LIGHT_COLOR = new Color(243, 224, 195);
	public static final Color BLACK_LIGHT_COLOR = new Color(195, 159, 130);
	public static final String PIECE_IMG_FILE = "assets/pieces.png";
	public static final String PIECE_TXT_FILE = "assets/pieceimg.txt";
	public static final String SAVE_FOLDER = "saves/";
	public static final int TEXT_PADDING = 10;
	
	public static long P1_START_TIME = 5*60*1000;
	public static long P2_START_TIME = 5*60*1000;
	public static int HUMAN_PLAYER = 1;
	public static int FIRST_MOVE = 1;
	
	public static int MAX_TIME = 5000;
	public static int MIN_DEPTH = 5;
	public static int START_DEPTH = 1;
	
	public static boolean FUN_MODE = false;
	public static boolean TWO_PLAYER = false;
}
