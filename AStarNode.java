import java.util.Set;

public class AStarNode implements Comparable<AStarNode>{
	private Node node;
	private AStarNode fromNode;
	private double costFromStart;
	private double estTotal;
	
	public AStarNode(Node node, AStarNode from, double costFromStart, double estTotal){
		this.node = node;
		this.fromNode = from;
		this.costFromStart = costFromStart;
		this.estTotal = estTotal;
	}
	
	public Node getNode(){
		return this.node;
	}
	
	public AStarNode getFrom(){
		return this.fromNode;
	}
	
	public double getCostFromStart(){
		return this.costFromStart;
	}
	
	public double getEstTotal(){
		return this.estTotal;
	}
	
	//returns the segment path between the node and from node
	public Segment pathSeg(){
		if(this.node != null && this.fromNode != null){
			for(Segment s:this.node.getInSegs()){
				if(this.fromNode.getNode().getOutSegs().contains(s))
					return s;
			}
		}
		return null;
	}
	
	public boolean canMove(Node neighbour, Set<Restriction> restrictions){
		for(Restriction r: restrictions){
			if(r.getMiddleNode() == this.node.getID() && r.getNode1() == fromNode.getNode().getID()){	//find current node from restrictions
				if(r.getNode2() == neighbour.getID())	return false;
			}
		}
		return true;
	}
	
	@Override
	public int compareTo(AStarNode other) {
		if(this.getEstTotal() > other.getEstTotal()) return 1;
		else if(this.getEstTotal() < other.getEstTotal()) return -1;
		return 0;
	}
}
