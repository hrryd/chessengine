
public class TimeManager {
	
	private long player1Time = Constant.P1_START_TIME;
	private long player2Time = Constant.P2_START_TIME;
	private long player1MoveTime = -1;
	private long player2MoveTime = -1;
	
	public TimeManager(){
		
	}

	public void reset() {
		player1Time = Constant.P1_START_TIME;
		player2Time = Constant.P2_START_TIME;
		player1MoveTime = -1;
		player2MoveTime = -1;
	}
	
	public long getMoveTime(int player){
		if(player == 1) return player1Time;
		if(player == 2) return player2Time;
		return 0;
	}
	
	public void update(){
		if(player1MoveTime != -1){
			player1Time -= (System.currentTimeMillis()-player1MoveTime);
			player1MoveTime = System.currentTimeMillis();
		}
		if(player2MoveTime != -1){
			player2Time -= (System.currentTimeMillis()-player2MoveTime);
			player2MoveTime = System.currentTimeMillis();
		}
	}

	public void beginMove(int player) {
		if(player == 1) player1MoveTime = System.currentTimeMillis();
		if(player == 2) player2MoveTime = System.currentTimeMillis();
	}

	public void endMove(int player) {
		if(player == 1) player1MoveTime = -1;
		if(player == 2) player2MoveTime = -1;
	}

	public void setTime(long p1Time, long p2Time) {
		player1Time = p1Time;
		player2Time = p2Time;
	}
}
