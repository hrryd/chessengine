

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Random;

public class OpeningRetreiver {
	
	public static void main(String[] args){
		OpeningRetreiver opening = new OpeningRetreiver();
	}
	
	private Random random;
	private File openingFile = new File("kingbase/opening.txt");
	private Hashtable<Long, Position> openingTable;
	private static final int MIN_POPULARITY = 1;
	
	public OpeningRetreiver(){
		random = new Random();
		openingTable = new Hashtable<Long, Position>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(openingFile));
			String line = br.readLine();
			while(line != null){
			    String[] data = line.split(" ");
			    long zobristKey = Long.parseLong(data[0]);
			    Position pos = new Position(zobristKey);
			    String[] moves = data[1].split("\\|");
			    for(String m : moves){
			    	String[] info = m.split(",");
			    	int[] move = new int[3];
			    	for(int i = 0; i < move.length; i++){
			    		move[i] = Integer.parseInt(info[i]);
			    	}
			    	if(move[2] >= MIN_POPULARITY){	
			    		if(pos.moves.size() > 0){
			    			int max = 0;
			    			for(int i = 0; i < pos.moves.size(); i++){
			    				if(pos.moves.get(i)[2] > max) max = pos.moves.get(i)[2];
			    			}
			    			if(move[2] > max*4){
			    				pos.moves.clear();
			    				pos.addComplete(move);
			    			}else if(move[2] > max/4){
			    				pos.addComplete(move);
			    			}
			    		}else{
			    			pos.addComplete(move);
			    		}
			    	}
			    }
			    if(pos.moves.size() >= 1) openingTable.put(pos.zobristKey, pos);
			    line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int[] getMove(long key){
		if(openingTable.containsKey(key)){
			Position pos = openingTable.get(key);
			return pos.moves.get(random.nextInt(pos.moves.size()));
		}else{
			return null;
		}	
	}
}
