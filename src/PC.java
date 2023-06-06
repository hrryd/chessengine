

public class PC { //piece constants
	
	public static final int[] castlingRight = new int[]{1, 1, 1, 1}; //W(K,Q) B(K,Q)
	
	public static final boolean[] pieceSliding = { //sliding=bishop, queen etc..
		false, false, false, true, true, true, false
	};
		
	public static final int[] pieceMoves = { //how many offsets each piece has, e.g bishop 4 directions, queen 8 directions
		0, 8, 8, 4, 4, 8, 8	//0=padding for ease
	};
	
	public static final double[] value = {
		0, 1, 3.25, 3.50, 5, 9.75, 0	
	};
		
	public static final int[][] pieceOffsets = {
		{   0,   0,   0,   0,   0,   0,   0,   0}, //padding deltas (as 1 = pawn and array start index = 0)
		{  15,  17,  16,  32, -15, -17, -16, -32}, //pawn deltas
		{  31,  33,  18, -14, -31, -33, -18,  14}, //knight deltas
		{ -15, -17,  15,  17,   0,   0,   0,   0}, //bishop deltas
		{  -1, -16,   1,  16,   0,   0,   0,   0}, //rook deltas 
		{ -15, -17,  15,  17,  -1, -16,   1,  16}, //queen deltas
		{ -15, -17,  15,  17,  -1, -16,   1,  16}  //king deltas
	};
		
	public static final int[] newBoard = {	 
		4,  2,  3,  5,  6,  3,  2,  4,  0,  0,  0,  0,  0,  0,  0,  0,
		1,  1,  1,  1,  1,  1,  1,  1,  0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	   -1, -1, -1, -1, -1, -1, -1, -1,  0,  0,  0,  0,  0,  0,  0,  0,
	   -4, -2, -3, -5, -6, -3, -2, -4,  0,  0,  0,  0,  0,  0,  0,  0
	};
	
	public static final int[] testBoard = {	 
		4,  5,  3,  5,  6,  3,  5,  4,  0,  0,  0,  0,  0,  0,  0,  0,
		1,  1,  1,  1,  1,  1,  1,  1,  0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	   -1, -1, -1, -1, -1, -1, -1, -1,  0,  0,  0,  0,  0,  0,  0,  0,
	   -4, -5, -3, -5, -6, -3, -5, -4,  0,  0,  0,  0,  0,  0,  0,  0
	};
	

	public static void displayBoard(int[] board){
		for(int i = 0; i < 128; i++){
			if(i % 16 == 0 && i != 0) System.out.println();
			if(i % 16 <= 7){
				System.out.print((board[i]>=0 ? " " : "") + board[i] + " ");
			}	
		}
		System.out.println();
	}
}
