import java.util.ArrayList;
import java.util.List;

public class Road {
	private int roadID;
	private int type; //what is type
	private String roadName;
	private String city;
	private int oneWay, speedLimit, roadClass, notForCar, notForPedestrians, notForBicycle;
	
	List<Segment> segs = new ArrayList<Segment>();
	
	public Road(int ID, int t, String rn, String c, int ow, int sl, int rc, int nfc, int nfp, int nfb){
		this.roadID = ID;
		this.type = t;
		this.roadName = rn;
		this.city = c;
		this.oneWay = ow;
		this.speedLimit = sl;
		this.roadClass = rc;
		this.notForCar = nfc;
		this.notForPedestrians = nfp;
		this.notForBicycle = nfb;
	}
	
	public List<Segment> getSegs(){
		return this.segs;
	}
	
	public void addSegs(Segment s){
		this.segs.add(s);
	}
	
	public boolean isOneWay(){
		return this.oneWay == 1?true:false; //return true if the road is one way
	}
	
	public boolean notForCar(){
		return this.notForCar == 1?true:false;
	}
	
	public String getName(){
		return this.roadName;
	}
	
	public int getSpeedLimit(){
		return this.speedLimit;
	}
}
