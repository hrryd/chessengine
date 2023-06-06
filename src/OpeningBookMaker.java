

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class OpeningBookMaker {
	
	public static void main(String[] args){
		OpeningBookMaker openbookmaker = new OpeningBookMaker();
	}
	
	private Generation generation;
	private int[] board;
	private int toMove;
	private File gameFolder = new File("kingbase/");
	private File gameFile = new File("kingbase/KingBase2016-01-B20-B49.pgn");
	private File openingFile = new File("kingbase/opening.txt");
	private HashMap<Long, Position> positions;
	
	public OpeningBookMaker(){
		positions = new HashMap<Long, Position>();
		toMove = 1;
		board = PC.newBoard;
		generation = new Generation();
		generation.reset(board);
		File[] allGames = gameFolder.listFiles();
		for(File f : allGames){
			System.out.print(f.getName() + " ");
			if(f.getName().endsWith("pgn")){
				try {
					BufferedReader br = new BufferedReader(new FileReader(f));
					String line = br.readLine();
					while(line != null){
					    if(line.startsWith("1.")){
					    	int endIndex = line.indexOf("7.");
					    	if(endIndex != -1){
					    		line = line.substring(0, endIndex);
					    		generateOpening(line);
					    	}
					    }
					    line = br.readLine();
					}
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Done.");
		}
		for(Position pos : positions.values()){
			System.out.print(pos.zobristKey + " " + pos.moves.size());
			for(int[] move : pos.moves){
				System.out.print(" " + move[2]);
			}
			System.out.println();
		}
		System.out.println(positions.size());
		System.out.println("Writing to file...");
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(openingFile));
			for(Position pos : positions.values()){
				String writeS = "";
				writeS += pos.zobristKey + " ";
				for(int[] move : pos.moves){
					writeS += move[0] + "," + move[1] + "," + move[2] + "|";
				}
				bw.write(writeS);
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Done.");
	}

	public void generateOpening(String line) {
		String[] moves = line.split("\\d\\.");
		String[][] opening = new String[6][2];
		String[][] original = new String[6][2];
		for(int i = 1; i < moves.length; i++){
			opening[i-1] = moves[i].split(" ");
			original[i-1] = moves[i].split(" ");
			for(int n = 0; n < opening[i-1].length; n++){
				opening[i-1][n] = opening[i-1][n].replaceAll("\\+","").replaceAll("x", "");
			}
		}
		calculateOpening(opening, 0);
	}
	
	public void calculateOpening(String[][] opening, int depth){
		int[] move = getMove(opening[depth/2][depth%2]);
		addPositon(move);
		if(depth >= 11) return;
		int[] cR = generation.getCastlingRightClone();
		int enP = generation.getEnpassant();
		int capture = generation.makeMove(board, move[0], move[1], move[2], move[3], move[4], move[5]);
		toMove *= -1;
		calculateOpening(opening, depth+1);
		generation.unmakeMove(board, move[0], move[1], move[2], move[3], move[4], move[5], capture, cR, enP);
		toMove *= -1;
	}
	
	public void addPositon(int[] move){
		long key = generation.getZobristKey();
		if(positions.containsKey(key)){
			positions.get(key).addMove(move[0], move[1]);
		}else{
			Position pos = new Position(key);
			pos.addMove(move[0], move[1]);
			positions.put(key, pos);
		}
	}
	
	public int[] getMove(String moveStr){
		ArrayList<int[]> moves = generation.moveGen(board, toMove, false);
		if(moveStr.equals("O-O") || moveStr.equals("O-O-O")){
			for(int[] move : moves){
				if((moveStr.equals("O-O") ? 3 : 4) == move[2]){
					return move;
				}
			}
		}
		if(moveStr.length() == 2){
			int s = cordToInt(moveStr);
			for(int[] move : moves){
				if(s == move[1] && move[3] == 1){
					return move;
				}
			}
		}
		if(moveStr.length() == 3){
			int s = cordToInt(moveStr.substring(1, 3));
			if(Character.isLowerCase(moveStr.charAt(0))){
				int x = cordToLine(moveStr.substring(0, 1));
				for(int[] move : moves){
					if(s == move[1] && move[0] % 16 == x && move[3] == 1){
						return move;
					}
				}
			}
			int p = pieceToInt(moveStr.substring(0, 1));
			for(int[] move : moves){
				if(s == move[1] && p == move[3]){
					return move;
				}
			}
		}
		if(moveStr.length() == 4){
			int p = pieceToInt(moveStr.substring(0, 1));
			int x = cordToLine(moveStr.substring(1, 2));
			int s = cordToInt(moveStr.substring(2, 4));
			for(int[] move : moves){
				if(s == move[1] && move[0] % 16 == x && p == move[3]){
					return move;
				}
			}
			int y = Integer.parseInt(moveStr.substring(1, 2))-1;
			for(int[] move : moves){
				if(s == move[1] && move[0] / 16 == y && p == move[3]){
					return move;
				}
			}
		}
		System.out.println(moveStr + " " + moveStr.length());
		return null;
	}
	
	public int cordToLine(String s){
		return s.charAt(0) - 'a';
	}
	
	public int pieceToInt(String p){
		if(p.equals("B")|| p.equals("b")) return 3;
		if(p.equals("N")|| p.equals("n")) return 2;
		if(p.equals("Q")|| p.equals("q")) return 5;
		if(p.equals("R")|| p.equals("r")) return 4;
		if(p.equals("K")|| p.equals("k")) return 6;
		return 0;
	}

	public int cordToInt(String s){
		return (s.charAt(0) - 'a') + (s.charAt(1) - 49) * 16;
	}
}
