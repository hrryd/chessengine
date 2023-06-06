

import java.util.ArrayList;

public class Position {
	
	protected long zobristKey;
	protected ArrayList<int[]> moves;
	
	public Position(long key){
		zobristKey = key;
		moves = new ArrayList<int[]>();
	}
	
	public void addMove(int i, int s){
		boolean contains = false;
		for(int n = 0; n < moves.size(); n++){
			if(moves.get(n)[0] == i && moves.get(n)[1] == s){
				contains = true;
				moves.get(n)[2] += 1;
			}
		}
		if(!contains) moves.add(new int[]{i, s, 1});
	}
	
	public void addComplete(int[] move){
		moves.add(move);
	}
}
