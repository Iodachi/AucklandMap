import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Segment {
	private int roadID;
	private double length;
	private List<Location> coords = new ArrayList<Location>();
	
	private Road road;
	private Node startNode, endNode;
	
	public Segment(int ID, double l, List<Location> c, Road rd, Node sn, Node en){
		this.roadID = ID;
		this.length = l;
		this.coords = c;
		this.road = rd;
		this.startNode = sn;
		this.endNode = en;
	}
	
	public Road getRoad(){
		return this.road;
	}
	
	public Node getStartNode(){
		return this.startNode;
	}
	
	public Node getEndNode(){
		return this.endNode;
	}
	
	public double getLength(){
		return this.length;
	}
	
	public Set<Node> getNodes(){ 
		Set <Node> nodes = new HashSet<Node>();
		nodes.add(this.startNode);
		nodes.add(this.endNode);
		return nodes;
	}
	
	public void draw(Graphics g, Location origin, double scale, int shiftX, int shiftY, Point nw, Color c){
		for(int i = 0; i < this.coords.size()-1; i++){
			Point p1 = this.coords.get(i).asPoint(origin, scale);
			Point p2 = this.coords.get(i+1).asPoint(origin, scale);
			
			int x1 = (int)(p1.x + shiftX - nw.x);
			int y1 = (int)(p1.y + shiftY - nw.y);
			int x2 = (int)(p2.x + shiftX - nw.x);
			int y2 = (int)(p2.y + shiftY - nw.y);
			//fixed default graph display, now shown in middle
			g.setColor(c);
			g.drawLine(x1, y1, x2, y2);
		}
	}
	}


