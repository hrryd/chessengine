import java.util.ArrayList;


public class MoveGenTest {
	public static void main(String[] args){
		for(int i = 1; i < 8; i++){
			System.out.print(i + " ");
			MoveGenTest perf = new MoveGenTest(i);
		}
	}
	
	/*
	Known Perft Values:
	1  20
	2  400
	3  8902
	4  197281
	5  4865609
	6  119060324
	7  3195901860
	8  84998978956
	9  2439530234167
	10 69352859712417
	*/
	
	private Generation gen;
	private int toMove = 1;
	private int[] board = PC.newBoard.clone();
	
	public MoveGenTest(int depth){
		gen = new Generation();
		gen.reset(board);
		
		System.out.println(perft(depth));
	}
	
	public int perft(int depth){
		if(depth == 0) return 1;
		int moveCount = 0;
		ArrayList<int[]> moves = gen.moveGen(board, toMove, false);
		for(int[] m : moves){
			int[] cR = gen.getCastlingRightClone();
			int enP = gen.getEnpassant();
			int capture = gen.makeMove(board, m[0], m[1], m[2], m[3], m[4], m[5]);
			toMove *= -1;
			moveCount += perft(depth-1);
			gen.unmakeMove(board, m[0], m[1], m[2], m[3], m[4], m[5], capture, cR, enP);
			toMove *= -1;
		}
		return moveCount;
	}
}
