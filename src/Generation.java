

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class Generation {
	
	private int[] castlingRight = PC.castlingRight;
	private int enpassant = -1;
	private int wKingPos = 4;
	private int bKingPos = 116;
	private boolean wCastled = false;
	private boolean bCastled = false;
	
	private Random rnd = new Random();
	private long zobristKey;
	private long[][][] zobPieces = new long[6][2][120];
	private long[] zobEnpassant = new long[120];
	private long[] zobCastlingRight = new long[16];
	private long zobSide;
	private boolean loadFile = true;
	
	private int[][][] allKiller;
	private int[][] primaryKiller;
	private int[][] secondaryKiller;
	
	public Generation(){
		
	}
	
	public void reset(int[] board){
		castlingRight = PC.castlingRight;
		enpassant = -1;
		wKingPos = 4;
		bKingPos = 116;
		wCastled = false;
		bCastled = false;
		resetKiller();
		initZobrist(board);
	}
	
	public void addToKiller(int depth, int[] move){
		allKiller[depth][move[0]][move[1]]++;
		int value = allKiller[depth][move[0]][move[1]];
		int primary = allKiller[depth][primaryKiller[depth][0]][primaryKiller[depth][1]];
		int secondary = allKiller[depth][secondaryKiller[depth][0]][secondaryKiller[depth][1]];
		if(value > primary && !compareMove(move, primaryKiller[depth])){
			secondaryKiller[depth] = primaryKiller[depth].clone();
			primaryKiller[depth] = move.clone();	
		}else if(value > secondary && !compareMove(move, primaryKiller[depth]) && !compareMove(move, secondaryKiller[depth])){
			secondaryKiller[depth] = move.clone();
		}
	}
	
	public void setKiller(int depth, int[] move){
		if(primaryKiller[depth] == null || move[0] != primaryKiller[depth][0] 
				|| move[1] != primaryKiller[depth][1]){
			primaryKiller[depth] = move.clone();
			secondaryKiller[depth] = primaryKiller[depth].clone();
		}	
	}
	
	public void resetKiller(){
		allKiller = new int[20][120][120];
		primaryKiller = new int[20][];
		secondaryKiller = new int[20][];
	}
	
	public long getZobristKey(){
		return zobristKey;
	}
	
	private int cRBinary(){
		return castlingRight[0]*1 + castlingRight[1]*2 + castlingRight[2]*4 + castlingRight[3]*8;
	}
	
	private void initZobrist(int[] board){
		rnd = new Random();
		if(loadFile){
			loadKeys();
		}else{
			zobSide = rnd.nextLong();
			for(int i = 0; i < 16; i++){
				zobCastlingRight[i] = rnd.nextLong(); 
			}
			for(int i = 0; i < 120; i++){
				zobEnpassant[i] = rnd.nextLong();
			}
			for(int p = 0; p < 6; p++){
				for(int c = 0; c < 2; c++){
					for(int i = 0; i < 120; i++){
						zobPieces[p][c][i] = rnd.nextLong();
					}
				}
			}
		}
		zobristKey = 0;
		for(int i = 0; i < 120; i++){
			int piece = board[i];
			if(piece > 0) zobristKey ^= zobPieces[Math.abs(piece)-1][0][i];
			if(piece < 0) zobristKey ^= zobPieces[Math.abs(piece)-1][1][i];
		}
		zobristKey ^= zobCastlingRight[cRBinary()];
		if(getEnpassant() != -1) zobristKey ^= zobEnpassant[getEnpassant()];
		if(Constant.FIRST_MOVE == -1) zobristKey ^= zobSide;
	}
	
	public void saveKeys(){	
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("assets/keyPieces.txt"));
			outputStream.writeObject(zobPieces);
			outputStream = new ObjectOutputStream(new FileOutputStream("assets/keyEnpassant.txt"));
			outputStream.writeObject(zobEnpassant);
			outputStream = new ObjectOutputStream(new FileOutputStream("assets/keyCastlingRight.txt"));
			outputStream.writeObject(zobCastlingRight);
			outputStream = new ObjectOutputStream(new FileOutputStream("assets/keySide.txt"));
			outputStream.writeObject(zobSide);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadKeys(){
		try {
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("assets/keyPieces.txt"));
			zobPieces = (long[][][]) inputStream.readObject();
			inputStream = new ObjectInputStream(new FileInputStream("assets/keyEnpassant.txt"));
			zobEnpassant = (long[]) inputStream.readObject();
			inputStream = new ObjectInputStream(new FileInputStream("assets/keyCastlingRight.txt"));
			zobCastlingRight = (long[]) inputStream.readObject();
			inputStream = new ObjectInputStream(new FileInputStream("assets/keySide.txt"));
			zobSide = (long) inputStream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public int getKingSquare(int toMove){
		return toMove == 1 ? wKingPos : bKingPos;
	}
		
	public boolean getWCanCastle() {
		return PC.castlingRight[0] == 1 || PC.castlingRight[1] == 1;
	}
	
	public boolean getBCanCastle() {
		return PC.castlingRight[2] == 1 || PC.castlingRight[3] == 1;
	}
	
	public boolean getWCastled(){
		return wCastled;
	}
	
	public boolean getBCastled(){
		return bCastled;
	}
	
	public int getEnpassant(){
		return enpassant;
	}
	
	public int[] getCastlingRightClone(){
		return castlingRight.clone();
	}
	
	//r (0=K, 1=Q)
	private void changeCastle(int tm, int r){
		if(tm == 1){
			castlingRight[r] = 0;
		}else{
			castlingRight[r+2] = 0; 
		}
	}
	
	private void noCastle(int tm){
		if(tm == 1){
			castlingRight[0] = 0;
			castlingRight[1] = 0;
		}else{
			castlingRight[2] = 0; 
			castlingRight[3] = 0; 
		}
	}
	
	public int makeMove(int[] board, int i, int s, int c, int p, int tm, int pr){
		int tmi = tm > 0 ? 0 : 1;
		int etmi = tm > 0 ? 1 : 0;
		zobristKey ^= zobPieces[Math.abs(board[i])-1][tmi][i];
		if(board[s] != 0) zobristKey ^= zobPieces[Math.abs(board[s])-1][etmi][s];
		zobristKey ^= zobPieces[Math.abs(board[i])-1][tmi][s];
		int capture = board[s];
		board[s] = board[i];
		board[i] = 0;
		if(pr != 0){
			zobristKey ^= zobPieces[0][tmi][s];
			zobristKey ^= zobPieces[pr-1][tmi][s];
			board[s] = pr*tm;
		}
		if(p == 6){
			if(tm == 1){
				wKingPos = s;
			}else{
				bKingPos = s;
			}
		}
		if(enpassant != -1) zobristKey ^= zobEnpassant[enpassant];
		enpassant = -1;
		if(c == 2) enpassant = i+16*tm;
		if(c == 5){
			zobristKey ^= zobPieces[0][etmi][s-16*tm];
			board[s-16*tm] = 0;
		}
		if(p == 4){
			zobristKey ^= zobCastlingRight[cRBinary()];
			if(i % 16 == 7) changeCastle(tm, 0);
			if(i % 16 == 0) changeCastle(tm, 1);
			zobristKey ^= zobCastlingRight[cRBinary()];
		}
		if(p == 6){
			zobristKey ^= zobCastlingRight[cRBinary()];
			noCastle(tm);
			zobristKey ^= zobCastlingRight[cRBinary()];
		}
		if(c == 3){
			zobristKey ^= zobCastlingRight[cRBinary()];
			zobristKey ^= zobPieces[3][tmi][i+1];
			zobristKey ^= zobPieces[3][tmi][i+3];
			if(tm == 1) wCastled = true;
			else bCastled = true;
			board[i+1] = 4*tm;
			board[i+3] = 0;
			noCastle(tm);		
			zobristKey ^= zobCastlingRight[cRBinary()];
		}
		if(c == 4){
			zobristKey ^= zobCastlingRight[cRBinary()];
			zobristKey ^= zobPieces[3][tmi][i-1];
			zobristKey ^= zobPieces[3][tmi][i-4];
			if(tm == 1) wCastled = true;
			else bCastled = true;
			board[i-1] = 4*tm;
			board[i-4] = 0;
			noCastle(tm);
			zobristKey ^= zobCastlingRight[cRBinary()];
		}
		if(enpassant != -1) zobristKey ^= zobEnpassant[enpassant];
		zobristKey ^= zobSide;
		return capture;
	}
	
	public void unmakeMove(int[] board, int i, int s, int c, int p, int tm, int pr, int capture, int[] cR, int enP){
		int tmi = tm > 0 ? 0 : 1;
		int etmi = tm > 0 ? 1 : 0;
		zobristKey ^= zobPieces[Math.abs(board[s])-1][tmi][s];	
		zobristKey ^= zobPieces[Math.abs(board[s])-1][tmi][i];	
		if(capture != 0) zobristKey ^= zobPieces[Math.abs(capture)-1][etmi][s];
		board[i] = board[s];
		board[s] = capture;
		if(pr != 0){
			zobristKey ^= zobPieces[pr-1][tmi][i];
			zobristKey ^= zobPieces[0][tmi][i];
			board[i] = tm;
		}
		if(p == 6){
			if(tm == 1){
				wKingPos = i;
			}else{
				bKingPos = i;
			}
		}
		zobristKey ^= zobCastlingRight[cRBinary()];
		castlingRight = cR;
		zobristKey ^= zobCastlingRight[cRBinary()];
		if(enpassant != -1) zobristKey ^= zobEnpassant[enpassant];
		enpassant = enP;
		if(c == 5){
			zobristKey ^= zobPieces[0][etmi][s-16*tm];
			board[s-16*tm] = -1*tm;
		}
		if(c == 3){
			zobristKey ^= zobPieces[3][tmi][i+1];
			zobristKey ^= zobPieces[3][tmi][i+3];
			if(tm == 1) wCastled = false;
			else bCastled = false;
			board[i+1] = 0;
			board[i+3] = 4*tm;
		}
		if(c == 4){
			zobristKey ^= zobPieces[3][tmi][i-1];
			zobristKey ^= zobPieces[3][tmi][i-4];
			if(tm == 1) wCastled = false;
			else bCastled = false;
			board[i-1] = 0;
			board[i-4] = 4*tm;
		}
		if(enpassant != -1) zobristKey ^= zobEnpassant[enpassant];
		zobristKey ^= zobSide;
	}
	
	private void addMove(int[] board, ArrayList<int[]> moves, boolean capturesOnly, int tm, int i, int s, int c, int p){
		if(capturesOnly && board[s] == 0) return;
		int[] cR = getCastlingRightClone();
		int enP = getEnpassant();
		int capture = makeMove(board, i, s, c, p, tm, 0);
		boolean inCheck = inCheck(board, tm == 1 ? wKingPos : bKingPos, tm);
		unmakeMove(board, i, s, c, p, tm, 0, capture, cR, enP);
		if(!inCheck){
			if(p == 1 && ((s/16 == 7) || (s/16 == 0))){
				for(int pr = 2; pr <= 5; pr++){ //promotion piece
					moves.add(new int[]{i, s, c, p, tm, pr});
				}
			}else{
				moves.add(new int[]{i, s, c, p, tm, 0});
			}		
		}
	}
	
	public boolean inCheck(int[] board, int i, int toMove){
		for(int p = 2; p <= 6; p++){
			for(int j = 0; j < PC.pieceMoves[p]; j++){
				int s = i;
				while(true){
					s += PC.pieceOffsets[p][j]; //apply piece offsets to start index
					if((s & 0x88) != 0) break; //if off board - break
					if(board[s] == -p*toMove) return true; //if move results in capture
					if(board[s] != 0) break;
					if(!PC.pieceSliding[p]) break; //break if not sliding
				}
			}
		}
		int js = 0; //offset array structured so white pawn has 
		if(toMove == -1) js = 4; //positive offset (0-2) and black negative (3-5)
		for(int j = js; j < js+4; j++){ //iterate offsets
			if(PC.pieceOffsets[1][j] % 2 == 0) continue; //cant capture forward. forward is even offset
			int s = i + PC.pieceOffsets[1][j]; //start index + offset
			if((s & 0x88) != 0) continue; //if target square on board
			if(board[s] == -1*toMove) return true; //capture OR enpassant					
		}
		return false;
	}
	
	public ArrayList<int[]> moveGen(int[] board, int toMove, boolean capturesOnly){
		ArrayList<int[]> moves = new ArrayList<int[]>();
		for(int i = 0; i < 128; i++){ //for all indexes
			if((i & 0x88) == 0 && board[i]*toMove > 0){ //on board & correct colour
				int pieceType = Math.abs(board[i]); //pieceType
				if(pieceType != 1){ //if not a pawn
					for(int j = 0; j < PC.pieceMoves[pieceType]; j++){ //iterate offsets for piece
						int s = i; //start index
						while(true){
							s += PC.pieceOffsets[pieceType][j]; //apply piece offsets to start index
							if((s & 0x88) != 0) break; //if off board - break
							if(board[s] != 0){ //if move results in capture
								if(board[s]*toMove > 0) break; //make sure capture is not our own colour
								addMove(board, moves, capturesOnly, toMove, i, s, 1, pieceType); //move with capture
								break;	
							}
							addMove(board, moves, capturesOnly, toMove, i, s, 0, pieceType); //move without capture
							if(!PC.pieceSliding[pieceType]) break; //break if not sliding
						}
					}
				}else{ //pawn moves
					int js = 0; //offset array structured so white pawn has 
					if(toMove == -1) js = 4; //positive offset (0-2) and black negative (3-5)
					for(int j = js; j < js+4; j++){ //iterate offsets
						int s = i + PC.pieceOffsets[pieceType][j]; //start index + offset
						if((s & 0x88) != 0) continue; //if target square on board
						if(board[s] != 0 || s == enpassant){ //capture OR enpassant
							if(PC.pieceOffsets[pieceType][j] % 2 == 0) continue; //cant capture forward. forward is even offset
							if(!(board[s]*toMove < 0 || s == enpassant)) continue; //make sure capture is not our own colour
							addMove(board, moves, capturesOnly, toMove, i, s, board[s]==0?5:1, pieceType); //move with capture
						}else{ //no capture
							if(j == 3 || j == 7){ //double pawn move check
								//check if on row 2 or 7
								if((toMove == 1 && i <= 23 && i >= 16) || (toMove == -1 && i <= 103 && i >= 96)){
									if(board[i+(PC.pieceOffsets[pieceType][j]/2)] != 0) continue;
									//movetype=2 shows double move for enpassant check
									addMove(board, moves, capturesOnly, toMove, i, s, 2, pieceType); 
								}
								continue;
							}
							if(PC.pieceOffsets[pieceType][j] % 2 == 0) {
								//move without capture - checks if forward move
								addMove(board, moves, capturesOnly, toMove, i, s, 0, pieceType); 	
							}
						}
					}					
				}	
				if(pieceType == 6){
					int kingSide = toMove == 1 ? castlingRight[0] : castlingRight[2];
					int queenSide = toMove == 1 ? castlingRight[1] : castlingRight[3];
					int row = toMove == 1 ? 0 : 112;
					if(kingSide == 1){ //can castle king side
						if(board[row+7] == 4*toMove){
							boolean canCastle = true;
							for(int c = row+4; c <= row+6; c++){
								if(board[c] != 0 && c != row+4) canCastle = false;
								if(inCheck(board, c, toMove)) canCastle = false;
							}	
							if(canCastle) addMove(board, moves, capturesOnly, toMove, i, i+2, 3, pieceType);	
						}	
					}
					if(queenSide == 1){ //can castle queen side	
						if(board[row] == 4*toMove){
							boolean canCastle = true;
							for(int c = row+4; c >= row+2; c--){
								if(inCheck(board, c, toMove)) canCastle = false;
							}	
							for(int c = row+3; c >= row; c--){
								if(board[c] != 0 && c != row) canCastle = false;
							}
							if(canCastle) addMove(board, moves, capturesOnly, toMove, i, i-2, 4, pieceType);
						}	
					}
				}
			}	
		}
		return moves;
	}
	
	public ArrayList<int[]> sortMoves(int[] board, ArrayList<int[]> moves, int depth){
		ArrayList<int[]> sortedMoves = new ArrayList<int[]>();
		ArrayList<Integer> scores = new ArrayList<Integer>();
		for(int i = 0; i < moves.size(); i++){
			scores.add(scoreMove(board, moves.get(i), depth));
		}
		while(moves.size() > 0){
			int max = 0;
			int idx = 0;
			for(int i = 0; i < moves.size(); i++){
				if(scores.get(i) > max){
					max = scores.get(i);
					idx = i;
				}
			}
			sortedMoves.add(moves.get(idx));
			scores.remove(idx);
			moves.remove(idx);
		}
		return sortedMoves;
	}
	
	public int scoreMove(int[] board, int[] move, int depth){	
		if(depth > -1 && primaryKiller[depth] != null && compareMove(move, primaryKiller[depth])) return 1000;
		if(depth > -1 && secondaryKiller[depth] != null && compareMove(move, secondaryKiller[depth])) return 999;
		int agressor = Math.abs(board[move[0]]);
		int capture = Math.abs(board[move[1]]);
		if(capture != 0) return (int) (PC.value[capture] * 9 + (9-PC.value[agressor]));
		return 0;
	}
	
	public boolean compareMove(int[] m1, int[] m2){
		if(m1[0] == m2[0] && m1[1] == m2[1]) return true;
		return false;
	}
	
	public int[] attackArray(int[] board, int toMove){
		int[] attackArray = new int[128];
		for(int i = 0; i < 128; i++){ 											//for all indexes
			if((i & 0x88) == 0 && board[i]*toMove > 0){ 						//on board & correct piece colour
				int pieceType = Math.abs(board[i]);
				if(pieceType != 1){												//not a pawn
					for(int j = 0; j < PC.pieceMoves[pieceType]; j++){ 			//iterate offsets
						int s = i; 												//start index
						while(true){
							s += PC.pieceOffsets[pieceType][j]; 				//apply piece offsets to start index
							if((s & 0x88) != 0) break; 							//break if not on board
							attackArray[s]++;
							if(board[s] != 0) break; 							//if move results in capture
							if(!PC.pieceSliding[pieceType]) break; 				//if sliding end loop
						}
					}
				}else{ 															//pawn moves
					int js = 0; 												//offset array structured so white pawn has positive 
					if(toMove == -1) js = 4; 									//offset (0-2) and black negative (3-5) js=start offset
					for(int j = js; j < js+4; j++){ 							//iterate offsets
						int s = i + PC.pieceOffsets[pieceType][j]; 				//apply offset to start index
						if((s & 0x88) != 0) continue; 							//if target square on board
						if(PC.pieceOffsets[pieceType][j] % 2 == 0) continue; 	//cant capture forward (forward = even)
						attackArray[s]++;		
					}
				}
			}
		}
		return attackArray;	
	}

	public void setCastling(int[] cr) {
		castlingRight = cr;
	}

	public void setZobrist(long key) {
		zobristKey = key;
	}
}

