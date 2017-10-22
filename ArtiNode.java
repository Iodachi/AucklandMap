import java.util.ArrayDeque;
import java.util.Queue;

public class ArtiNode {
	private Node node;
	private ArtiNode parent;
	private int depth;
	private int reach;
	private Queue<Node> children = new ArrayDeque<Node>();
	
	public ArtiNode(Node node, int depth, ArtiNode parent){
		this.node = node;
		this.depth = depth;
		this.parent = parent;
		this.reach = Integer.MAX_VALUE;
	}
	
	public Node getNode(){
		return this.node;
	}
	
	public int getDepth(){
		return this.depth;
	}
	
	public ArtiNode getParent(){
		return this.parent;
	}
	
	public void setReach(int reach){
		this.reach = reach;
	}
	
	public int getReach(){
		return this.reach;
	}
	
	public Queue<Node> getChildren(){
		return this.children;
	}
	
	public void addChild(Node child){
		this.children.offer(child);
	}
	
}
