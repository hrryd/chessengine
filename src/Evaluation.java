
public class Evaluation {
	
	public static int[][] manHatTable = new int[120][120];
	
	public Evaluation(){
		computeManHatTable();
	}

	private void computeManHatTable() {
		System.out.print("Computing ManHat table..");
		for(int i = 0; i < 120; i++){
			if((i & 0x88) != 0) continue;
			for(int s = 0; s < 120; s++){
				if((s & 0x88) != 0) continue;
				int iX = i%16;
				int iY = i/16;
				int sX = s%16;
				int sY = s/16;
				int dist = 14 - (Math.abs(sX-iX) + Math.abs(sY-iY));
				manHatTable[i][s] = dist;
			}
		}
		System.out.println(" Done.");
	}
	
	public static double getManHatScore(int i, int s, int piece){
		int dist = Evaluation.manHatTable[i][s];
		if(piece == 2 || piece == 3){
			return dist*0.00025;
		}else if(piece == 4){
			return dist*0.00050;
		}else if(piece == 5){
			return dist*0.00100;
		}
		return 0;
	}
}
