import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Node {
	private static final int NODE_SIZE = 3;
	private int nodeID;
	private Location location;
	private int depth;
	
	private List <Segment>outSegs = new ArrayList<Segment>();
	private List <Segment>inSegs = new ArrayList<Segment>();
	
	public Node (int ID, Location loc){
		this.nodeID = ID;
		this.location = loc;
	}
	
	public Location getLoc(){
		return this.location;
	}
	
	public int getID(){
		return this.nodeID;
	}
	
	public void addInSegs(Segment s){
		this.inSegs.add(s);
	}
	
	public List<Segment> getInSegs(){
		return this.inSegs;
	}
	
	public void addOutSegs(Segment s){
		this.outSegs.add(s);
	}
	
	public List<Segment> getOutSegs(){
		return this.outSegs;
	}
	
	public List<Node> getNeighbours(){
		List<Node> neighbours = new ArrayList<Node>();
		for(Segment s: this.inSegs){
			neighbours.add(s.getStartNode());
		}
		for(Segment s: this.outSegs){
			neighbours.add(s.getEndNode());
		}
		return neighbours;
	}
	
	public double distanceTo(Node goal){
		return this.location.distance(goal.getLoc());
	}
	
	public void setDepth(int depth){
		this.depth = depth;
	}
	
	public int getDepth(){
		return this.depth;
	}
	
	public void draw(Graphics g, Location origin, double scale, int shiftX, int shiftY, Point nw, Color c){
		g.setColor(c);
		Point p = this.location.asPoint(origin, scale);
		int x = (int)(p.getX() + shiftX - nw.x);
		int y = (int)(p.getY() + shiftY - nw.y); 
		g.fillOval(x-NODE_SIZE/2, y-NODE_SIZE/2, NODE_SIZE, NODE_SIZE);
	}
}
	
