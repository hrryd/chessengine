import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class FileManager {
	
	public static File chooseFile(){
		JFileChooser fileChooser = new JFileChooser();
		int ret = fileChooser.showOpenDialog(new JFrame());
		if(ret == JFileChooser.APPROVE_OPTION){
			return fileChooser.getSelectedFile();
		}
		return null;
	}
	
	public static void initSaveBoard(GameBoard gb){
		System.out.print("Saving.. ");
		TextReader tr = new TextReader(gb);
		System.out.println("Done.");
	}
	
	public static void saveBoard(String str, GameBoard gb){
		int[] board = gb.getBoard();
		int[] cr = gb.getMoveGen().getCastlingRightClone();
		int tm = gb.getToMove();
		long zobristKey = gb.getMoveGen().getZobristKey();
		long p1Time = gb.getTimeManager().getMoveTime(1);
		long p2Time = gb.getTimeManager().getMoveTime(2);
		try {
			PrintWriter pw = new PrintWriter(Constant.SAVE_FOLDER + str + ".txt");
			for(int i = 0; i < board.length; i++){	
				pw.write(board[i] + " "); //boards
				if(i % 16 == 15) pw.println(); //every 16 on new row (easier to read)
			}
			for(int i = 0; i < cr.length; i++){
				pw.write(cr[i] + " "); //castling rights
			}
			pw.println();
			pw.write(tm+""); //whose move
			pw.println();
			pw.write(zobristKey+""); //zobrist key
			pw.println();
			pw.write(p1Time+""); //player 1 time
			pw.println();
			pw.write(p2Time+""); //player 2 time
			pw.println();
			pw.write(gb.getGD().getFirstName());
			pw.println();
			pw.write(gb.getGD().getLastName());
			pw.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error saving game.", "Error", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	public static void loadBoard(GameBoard gb){
		int[] board = new int[128];
		int[] cr = new int[4];
		int tm = 0;
		long zobristKey = 0l;
		long p1Time = 0l;
		long p2Time = 0l;
		String firstName = "";
		String lastName = "";
		System.out.print("Loading.. ");
		File saveFile = chooseFile();
		if(saveFile != null){
			try {
				BufferedReader br = new BufferedReader(new FileReader(saveFile));
				String line = br.readLine();
				int lineNo = 0;
				while(line != null){
					String[] splitStr = line.trim().split(" ");
					if(lineNo <= 7){ //board
						for(int i = 0; i < splitStr.length; i++){
							board[i + lineNo*16] = Integer.parseInt(splitStr[i]);
						}
					}else if(lineNo == 8){ //castling rights
						for(int i = 0; i < splitStr.length; i++){
							cr[i] = Integer.parseInt(splitStr[i]);
						}
					}else if(lineNo == 9){ //tomove
						tm = Integer.parseInt(splitStr[0]);
					}else if(lineNo == 10){ //zobrist key
						zobristKey = Long.parseLong(splitStr[0]);
					}else if(lineNo == 11){ //player 1 time
						p1Time = Long.parseLong(splitStr[0]);
					}else if(lineNo == 12){ //player 2 time
						p2Time = Long.parseLong(splitStr[0]);
					}else if(lineNo == 13){ //player first name
						firstName = splitStr[0];
					}else if(lineNo == 14){ //player last name
						lastName = splitStr[0];
					}
					lineNo++;
					line = br.readLine();
				}
				gb.newGame();
				gb.setBoard(board);
				gb.setToMove(tm);
				gb.getMoveGen().setCastling(cr);
				gb.getMoveGen().setZobrist(zobristKey);
				gb.getTimeManager().setTime(p1Time, p2Time);
				gb.getGD().setFirstLastName(firstName, lastName);
				gb.getGD().setVisible(false);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Error loading game.", "Error", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		System.out.println("Done.");
	}
}
