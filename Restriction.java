
public class Restriction {
	private int nodeID1;
	private int roadID1;
	private int nodeID;
	private int nodeID2;
	private int roadID2;
	
	public Restriction(int n1, int r1, int n, int n2, int r2){
		this.nodeID1 = n1;
		this.roadID1 = r1;
		this.nodeID = n;
		this.nodeID2 = n2;
		this.roadID2 = r2;
	}
	
	public int getNode1(){
		return this.nodeID1;
	}
	
	public int getRoad1(){
		return this.roadID1;
	}
	
	public int getMiddleNode(){
		return this.nodeID;
	}
	
	public int getNode2(){
		return this.nodeID2;
	}
	
	public int getRoad2(){
		return this.roadID2;
	}
}
