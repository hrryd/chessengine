import java.util.ArrayList;

public class Engine implements Runnable {
	
	private boolean ready;
	private boolean newgame;
	
	private boolean overrun;
	private int curDepth;
	private int[] bestmove;
	private int[] oldbestmove;
	
	private GameBoard gameBoard;
	private Generation generation;
	private OpeningRetreiver opRet;
	private TimeManager timeManager;
	private Evaluation evaluation;
	
	private int[] board;
	private int toMove;
	
	public Engine(int[] board, GameBoard gameBoard){
		this.gameBoard = gameBoard;
		this.generation = gameBoard.getMoveGen();
		this.opRet = gameBoard.getOpRet();
		this.timeManager = gameBoard.getTimeManager();
		evaluation = new Evaluation();
	}
	
	public boolean getReady(){
		return ready;
	}
	
	public void setReady(boolean value){
		ready = value;
	}
	
	public void newGame(){
		newgame = false;
	}
	
	public void endGame(){
		newgame = true;
	}
	
	@Override
	public void run() {
		while(true){
			while(gameBoard.getToMove() != Constant.HUMAN_PLAYER && !newgame && !gameBoard.getGD().isVisible() && !Constant.TWO_PLAYER){
				timeManager.endMove(1);
				timeManager.beginMove(2);
				toMove = gameBoard.getToMove();
				if(Constant.FUN_MODE) board = gameBoard.getBoard();
				else board = gameBoard.getBoard().clone();
				long startTime = System.currentTimeMillis();
				long moveTime = 0;
				double score = 0;	
				generation.resetKiller();
				for(curDepth = Constant.START_DEPTH;; curDepth++){
					System.out.print(curDepth);
					bestmove = null;
					overrun = false;
					int[] opMove = opRet.getMove(generation.getZobristKey());
					if(opMove != null){
						ArrayList<int[]> moves = generation.moveGen(board, toMove, false);
						for(int[] move : moves){
							if(move[0] == opMove[0] && move[1] == opMove[1]){
								bestmove = move;
								break;
							}
						}
						break;
					}
					score = alphabeta(board, toMove, Integer.MIN_VALUE, Integer.MAX_VALUE, curDepth, startTime);	
					System.out.print(" ");
					if(Math.abs(score) == Double.POSITIVE_INFINITY) break;
					if(!overrun) oldbestmove = bestmove;	
					else bestmove = oldbestmove;
					moveTime = System.currentTimeMillis() - startTime;
					if(moveTime > Constant.MAX_TIME || newgame) break;
				}
				System.out.println();
				if(!newgame) {
					generation.makeMove(board, bestmove[0], bestmove[1], bestmove[2], bestmove[3], bestmove[4], bestmove[5]);
					gameBoard.setLastMove(bestmove[0], bestmove[1]);
					gameBoard.moveComplete();
					gameBoard.setBoard(board);
					timeManager.beginMove(1);
					timeManager.endMove(2);	
				}else{
					ready = true;
					System.out.println("Game ended..");
				}
				timeManager.endMove(2);	
			}
			try {
				Thread.sleep(33);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public double alphabeta(int[] board, int toMove, double alpha, double beta, int depth, long startTime){
		if(newgame) return 0;
		if(System.currentTimeMillis() - startTime > Constant.MAX_TIME && curDepth > Constant.MIN_DEPTH){
			overrun = true;
			return 0;
		}
		if(depth == 0) return evaluate(board, toMove);
		ArrayList<int[]> moves = generation.moveGen(board, toMove, false);
		moves = generation.sortMoves(board, moves, curDepth-depth);
		if(moves.size() == 0){
			if(depth == 1 && curDepth == 1); //checkmated
			if(generation.inCheck(board, generation.getKingSquare(toMove), toMove)) return -Double.POSITIVE_INFINITY;
			return 0;
		}
		double maxScore = -Double.POSITIVE_INFINITY;
		for(int[] move : moves){
			int[] cR = generation.getCastlingRightClone();
			int enP = generation.getEnpassant();
			int capture = generation.makeMove(board, move[0], move[1], move[2], move[3], move[4], move[5]);
			double score = -alphabeta(board, -toMove, -beta, -alpha, depth-1, startTime);	
			generation.unmakeMove(board, move[0], move[1], move[2], move[3], move[4], move[5], capture, cR, enP);
			if(score > maxScore || bestmove == null){
				maxScore = score;
				if(depth == curDepth) bestmove = move;
			}
			if(score >= beta){
				generation.setKiller(curDepth-depth, move);
				generation.addToKiller(curDepth-depth, move);
				return beta;   
			}
			if(score > alpha) alpha = score;	
		}
		return alpha;
	}

	public double quiescence(int[] board, int toMove, double alpha, double beta, int depth, long startTime){
		if(depth <= -6) return evaluate(board, toMove);
		if(newgame) return 0;
		if(System.currentTimeMillis() - startTime > Constant.MAX_TIME){
			overrun = true;
			return 0;
		}
		ArrayList<int[]> moves = generation.moveGen(board, toMove, true);
		if(moves.size() == 0) return evaluate(board, toMove);
		for(int[] move : moves){
			int[] cR = generation.getCastlingRightClone();
			int enP = generation.getEnpassant();
			int capture = generation.makeMove(board, move[0], move[1], move[2], move[3], move[4], move[5]);
			double score = -quiescence(board, -toMove, -beta, -alpha, depth-1, startTime);	
			generation.unmakeMove(board, move[0], move[1], move[2], move[3], move[4], move[5], capture, cR, enP);
			if(score >= beta) return beta;   
			if(score > alpha) alpha = score;	
		}
		return alpha;
	}
	
	private double evaluate(int[] board, int toMove){ //side as +1 for white -1 for black
		double eval = 0;
		if(generation.getWCanCastle()) eval += 0.75;
		if(generation.getBCanCastle()) eval -= 0.75;
		if(generation.getWCastled()) eval += 1.25;
		if(generation.getBCastled()) eval -= 1.25;
		
		int[] pawnsInFileW = new int[8];
		int[] pawnsInFileB = new int[8];
		int[] furthestPawn = new int[8];
		int wBishops = 0;
		int bBishops = 0;
		int wKing = generation.getKingSquare(1);
		int bKing = generation.getKingSquare(-1);
		for(int i = 0; i < 128; i++){
			if((i & 0x88) == 0){
				if(board[i] != 0){
					int piece = Math.abs(board[i]);
					int file = i % 16;
					int rank = i / 16;
					int colour = board[i] > 0 ? 1 : -1; //colour of piece
					boolean center = (rank == 3 || rank == 4) && (file == 3 || file == 4);
					if(piece == 1){
						if((i-colour*15 & 0x88) == 0 && board[i-colour*15] == colour) 
							eval += colour*0.015;
						if((i-colour*17 & 0x88) == 0 && board[i-colour*17] == colour) 
							eval += colour*0.015;	
						int distance = colour == 1 ? 7-rank : rank;
						if(furthestPawn[file] == 0 || furthestPawn[file] > distance){
							furthestPawn[file] = distance;
						}
					}
					eval += PC.value[piece]*colour; //material
					if(board[i] == 1) pawnsInFileW[file]++; //doubled pawns
					if(board[i] == -1) pawnsInFileB[file]++; //doubled pawns
					if(board[i] == 3) wBishops++; //bishop pair
					if(board[i] == -3) bBishops++; //bishop pair	
					if(center && piece == 1) eval += 0.2*colour; //center pawns
					if((rank == 0 || rank == 7 || file == 0 || file == 7) && (piece == 2 || piece == 3)) 
						eval -= colour*0.25; //knights on sides
					if(piece == 5){ //dont bring queen out early
						if(gameBoard.getPly() <= 14){
							if(colour == 1){
								if(rank != 0 && rank != 1) eval -= 0.1;
							}else{
								if(rank != 7 && rank != 6) eval += 0.1;
							}
						}
					}
					//KING SAFETY
					//ENEMY PIECE DISTANCE FROM KING
					if(colour == 1) eval += Evaluation.getManHatScore(i, bKing, piece);
					else eval -= Evaluation.getManHatScore(i, wKing, piece);
				}
			}
		}
		if(wBishops == 2) wBishops += 0.5;
		if(bBishops == 2) wBishops -= 0.5;
		//doubled pawns
		for(int i : pawnsInFileW) if(i > 1) eval -= i*0.125;
		for(int i : pawnsInFileB) if(i > 1) eval += i*0.125;
		//isolated pawns
		if(pawnsInFileW[1] == 0 && pawnsInFileW[0] >= 1) eval -= 0.2;
		if(pawnsInFileB[1] == 0 && pawnsInFileB[0] >= 1) eval += 0.2; 
		if(pawnsInFileW[6] == 0 && pawnsInFileW[7] >= 1) eval -= 0.2;
		if(pawnsInFileB[6] == 0 && pawnsInFileB[7] >= 1) eval += 0.2; 
		if(pawnsInFileW[3] == 0) eval -= 0.15;
		if(pawnsInFileB[3] == 0) eval += 0.15;
		if(pawnsInFileW[4] == 0) eval -= 0.15;
		if(pawnsInFileB[4] == 0) eval += 0.15;
		for(int i = 1; i < 7; i++){
			if(pawnsInFileW[i+1] == 0 && pawnsInFileW[i-1] == 0 && pawnsInFileW[i] >= 1) eval -= 0.25;
			if(pawnsInFileB[i+1] == 0 && pawnsInFileB[i-1] == 0 && pawnsInFileB[i] >= 1) eval += 0.25; 
		}
		//passed pawn
		if(pawnsInFileW[0] >= 1 && pawnsInFileB[0] == 0 && pawnsInFileB[1] == 0) 
			eval += 0.05*(6-furthestPawn[0])*(6-furthestPawn[0]);
		if(pawnsInFileB[0] >= 1 && pawnsInFileW[0] == 0 && pawnsInFileW[1] == 0) 
			eval -= 0.05*(6-furthestPawn[0])*(6-furthestPawn[0]);
		if(pawnsInFileW[7] >= 1 && pawnsInFileB[7] == 0 && pawnsInFileB[6] == 0) 
			eval += 0.05*(6-furthestPawn[7])*(6-furthestPawn[7]);
		if(pawnsInFileB[7] >= 1 && pawnsInFileW[7] == 0 && pawnsInFileW[6] == 0) 
			eval -= 0.05*(6-furthestPawn[7])*(6-furthestPawn[7]);
		for(int i = 1; i < 7; i++){
			if(pawnsInFileW[i] >= 1 && pawnsInFileB[i] == 0 && pawnsInFileB[i+1] == 0 && pawnsInFileB[i-1] == 0) 
				eval += 0.05*(6-furthestPawn[i])*(6-furthestPawn[i]);
			if(pawnsInFileB[i] >= 1 && pawnsInFileW[i] == 0 && pawnsInFileW[i+1] == 0 && pawnsInFileW[i-1] == 0) 
				eval -= 0.05*(6-furthestPawn[i])*(6-furthestPawn[i]);
		}
		return eval*toMove;
	}
}
