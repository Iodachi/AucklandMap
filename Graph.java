import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;


public class Graph extends GUI {
	public Map<Integer, Node> nodes = new HashMap<Integer, Node>();
	public Set<Segment> segments = new HashSet<Segment>(); //not sure why use collection in the lecture
	public Map<Integer, Road> roads = new HashMap<Integer, Road>();
	public Set<Polygonn> polygons = new HashSet<Polygonn>();
	public Set<Restriction> restrictions = new HashSet<Restriction>();
	public Location origin = Location.newFromLatLon(Location.CENTRE_LAT, Location.CENTRE_LON);
	public double scale;
	public int shiftX = 0;
	public int shiftY = 0;
	
	public double east = Location.CENTRE_LON;
	public double west = Location.CENTRE_LON;
	public double north = Location.CENTRE_LAT;
	public double south = Location.CENTRE_LAT;
	
	public Location southEast, northWest;
	public Point nw;
	//find east and west most in order to scale
	
	public Point prev; //for mouse drag
	
	public Node highlightedNode;
	public Set<Segment> highlightedSegs = new HashSet<Segment>();
	
	//for A* search, set start and end point
	public Node AStarStart;
	public Node AStarEnd;
	public List<Segment> path = new ArrayList<Segment>();
	public Map<String, Double> lengthCheck = new HashMap<String, Double>();
	//added another raodList of path without duplicate roads in order for printing
	public List<Segment> roadList = new ArrayList<Segment>();
	
	public Set<Node> artiPts = new HashSet<Node>();
	
	public Graph(){
		
	}
	
	@Override
	protected void redraw(Graphics g) {
		for(Polygonn p:polygons) p.draw(g, origin, scale, shiftX, shiftY, nw); //draw polygons first inorder to show the roads
		
		for(Segment s: segments) {
			if(s.getRoad().getName().contains("state highway")){
				s.draw(g, origin, scale, shiftX, shiftY, nw, new Color(246, 197, 128));
				//draw the state highways in orange
			}else{
				s.draw(g, origin, scale, shiftX, shiftY, nw, Color.WHITE);
			}
			if(!highlightedSegs.isEmpty()){
				s.draw(g, origin, scale, shiftX, shiftY, nw, Color.BLUE);
			} //draw highlighted segments
		}
		
		if(!path.isEmpty()){
			for(int i=0; i<path.size(); i++){
				path.get(i).draw(g, origin, scale, shiftX, shiftY, nw, Color.BLACK); //highlight path
			}
		}
		
		for(Node n: nodes.values()) 
			n.draw(g, origin, scale, shiftX, shiftY, nw, Color.RED);
		if(!artiPts.isEmpty()){
			for(Node n: artiPts)
				n.draw(g, origin, scale, shiftX, shiftY, nw, Color.BLACK);
		}
		if(highlightedNode != null) 
			highlightedNode.draw(g, origin, scale, shiftX, shiftY, nw, Color.GREEN);
			//draw highlighted nodes
		if(AStarStart != null) AStarStart.draw(g, origin, scale, shiftX, shiftY, nw, Color.YELLOW);
		if(AStarEnd != null) AStarEnd.draw(g, origin, scale, shiftX, shiftY, nw, Color.ORANGE);
	}
	
	@Override
	/**
	 * This function sets the start and end node for the map, while there is a node
	 * being highlighed, it will be set to start or end when pressing corresponding 
	 * buttons, pressing the button again will cancel it.
	 * 
	 */
	protected void chooseIntersection(Intersection i) {
		if(highlightedNode != null){
			if(i.equals(Intersection.START))	
				if(AStarStart == null || AStarStart != highlightedNode) AStarStart = highlightedNode;
				else AStarStart = null; //click again to cancel
			else if(i.equals(Intersection.END))	
				if(AStarEnd == null || AStarEnd != highlightedNode) AStarEnd = highlightedNode;
				else AStarEnd = null;
		}else{
			getTextOutputArea().setText("Choose a node first");
		}
	}
	
	@Override
	protected void onFindPath(boolean distance){
		//when both start and end nodes are selected(and are not the same), start A* search for shortest route
		if(AStarStart != null && AStarEnd != null && AStarStart != AStarEnd){
			if(distance){
				path = findPath(AStarStart, AStarEnd, true);
			}else{
				path = findPath(AStarStart, AStarEnd, false);
			}
			double totalLength = 0;
			for(int i=0; i<roadList.size(); i++){
				getTextOutputArea().append(String.format("%s, %.2f\n", roadList.get(i).getRoad().getName(), lengthCheck.get(roadList.get(i).getRoad().getName())));
				totalLength += lengthCheck.get(roadList.get(i).getRoad().getName());
			}
			getTextOutputArea().append(String.format("Total distance: %.2f\n", totalLength));
		}
	}
	
	@Override
	protected void onFindArti(){
		if(highlightedNode == null){
			for(Node n: nodes.values()){
				artiPts = findArtiPts(n, new ArtiNode(n, 0, null));
			}
		}else{
			artiPts = findArtiPts(highlightedNode, new ArtiNode(highlightedNode, 0, null));
		}
		getTextOutputArea().append(String.format("%d articulation points found\n", artiPts.size()));
	}
	
	@Override
	protected void onClick(MouseEvent e) {
		//highlight road segment when onClick
		Point mousePoint = new Point(e.getX(), e.getY()); //actual point, no need to reform
		prev = mousePoint; //store previous for drag action
		Point p = new Point(nw.x - shiftX, nw.y - shiftY);
		Location ori = Location.newFromPoint(p, origin, scale);
		
		Location mouseLoc = Location.newFromPoint(mousePoint, ori, scale);
		//had quite a long time to work this out, but honestly just randomly try and adjust stuff
		
		double closest = Double.MAX_VALUE; 
		//set closest distance to be max and loop through all the nodes to find any smaller ones
		for(Node n :nodes.values()){
			double distance = mouseLoc.distance(n.getLoc());
			if(distance < closest){
				highlightedNode = n;
				closest = distance;
				//System.out.println("found");
			}
		}
		
		Set<String> selectedRoads = new HashSet<String>();
		for(Segment s: segments)
			if(s.getNodes().contains(highlightedNode))
				selectedRoads.add(s.getRoad().getName());
		//if the segment contains the node, add it to an arraylist for display
		
		getTextOutputArea().append(String.format("Intersection ID: %d\n", highlightedNode.getID()));
		getTextOutputArea().append(String.format("Roads: %s\n", selectedRoads));
	}
	
	@Override
	protected void onSearch() {
		boolean findExact = false;
		Trie all = new Trie();
		for(Segment s:segments){
			all.insert(s.getRoad().getName());
		}
		highlightedSegs = new HashSet<Segment>();
		String prefix = getSearchBox().getText();
		
		for(Segment s:segments){
			if(s.getRoad().getName().equals(prefix)){
				findExact = true;
				highlightedSegs.add(s);
				getTextOutputArea().setText(prefix + " found");
			}
		}
		if(!findExact){
		if(all.startsWith(prefix)){
			for(int i=0;i<all.getOutput(prefix).size();i++){ 
					for(Segment s:segments){
					if(all.getOutput(prefix).get(i).equals(s.getRoad().getName())){
						highlightedSegs.add(s); 
					}
				}
			}
			getTextOutputArea().setText(String.format("Matching roads: %s\n", all.getOutput(prefix)));
		}else{
				getTextOutputArea().setText("Result not found");
		}
		}
		
		//getTextOutputArea().setText(getSearchBox().getText());
		//up to 10 result
}
	
	@Override
	protected void onMove(Move m) {
		if(m == Move.NORTH) shiftY += 10; 
		else if(m == Move.SOUTH) shiftY -= 10; 
		else if(m == Move.WEST) shiftX += 10;
		else if(m == Move.EAST) shiftX -= 10; 
		else if(m == Move.ZOOM_IN) scale = scale*1.1;
		else if(m == Move.ZOOM_OUT) scale = scale*0.91;
		//fixed zooming to top-left corner by passing Point nw instead of Location northWest
	}
	
	@Override
	protected void onDrag(MouseEvent e){
		//this is acting a bit weird
		if(prev != null){
		shiftX += prev.x - e.getX();
		shiftY += prev.y - e.getY();
		}
	}
	@Override
	protected void onWheelMove(MouseWheelEvent e){
		if(scale - e.getWheelRotation() > 0) //in case the graph goes upside down
		scale -= e.getWheelRotation();
	}
	
	public void loadNodes(File f){
		try{
			BufferedReader nodesData = new BufferedReader(new FileReader(f));
			String line;
			//nodesData.readLine();
			
			while((line = nodesData.readLine()) != null){
				String[] values = line.split("\t");
		
				int nodeID = Integer.parseInt(values[0]);
				double lat = Double.parseDouble(values[1]);
				double lon = Double.parseDouble(values[2]);
				
				if(lon > east) east = lon;
				if(lon < west) west = lon;
				if(lat < south) south = lat;
				if(lat > north) north = lat;
		
				Location lc = Location.newFromLatLon(lat, lon);
				Node newNode = new Node(nodeID, lc);
				nodes.put(nodeID, newNode);
				}
			nodesData.close();
		}catch (IOException e){
			e.printStackTrace();
		}
		southEast = Location.newFromLatLon(south, east);
		northWest = Location.newFromLatLon(north, west);
		scale = getDrawingAreaDimension().getWidth()/(southEast.x - northWest.x);
		nw = northWest.asPoint(origin, scale);
		}
	
	public void loadRoads(File f){
		try{
			BufferedReader roadsData = new BufferedReader(new FileReader(f));
			String line;
			roadsData.readLine();
			while((line = roadsData.readLine()) != null){
			String[] values = line.split("\t");
			
			int ID = Integer.parseInt(values[0]);
			int type = Integer.parseInt(values[1]);
			String roadName = values[2];
			String city = values[3];
			int oneWay = Integer.parseInt(values[4]);
			int speedLimit = Integer.parseInt(values[5]);
			int roadClass = Integer.parseInt(values[6]);
			int notForCar = Integer.parseInt(values[7]);
			int notForPedestrians = Integer.parseInt(values[8]);
			int notForBicycle = Integer.parseInt(values[9]);
			
			Road newRoad = new Road(ID, type, roadName, city, oneWay, speedLimit, roadClass, notForCar, notForPedestrians, notForBicycle);
			roads.put(ID, newRoad);
			}
			roadsData.close();
		}catch (IOException e){
			e.printStackTrace();
		}
		}
	
	public void loadSegs(File f){
		try{
			BufferedReader segsData = new BufferedReader(new FileReader(f));
			String line;
			segsData.readLine();
			while((line = segsData.readLine()) != null){
			String[] values = line.split("\t");
			
			int roadID = Integer.parseInt(values[0]);
			double length = Double.parseDouble(values[1]);
			int nodeID1 = Integer.parseInt(values[2]);
			int nodeID2 = Integer.parseInt(values[3]);
			
			List<Location> coords = new ArrayList<Location>();
			
			for(int i = 4; i<values.length; i+=2){
				double lat = Double.parseDouble(values[i]);
				double lon = Double.parseDouble(values[i+1]);
				coords.add(Location.newFromLatLon(lat, lon));
			}
			
			Road rd = roads.get(roadID);
			 
			Node startNode = nodes.get(nodeID1);
			Node endNode = nodes.get(nodeID2);
			 
			Segment newSeg = new Segment(roadID, length, coords, rd, startNode, endNode);
			segments.add(newSeg);
			 
			startNode.addOutSegs(newSeg);
			endNode.addInSegs(newSeg);
			 
			rd.addSegs(newSeg);
			 
			if(!rd.isOneWay()){
				 Segment opposite = new Segment(roadID, length, coords, rd, endNode, startNode);
				 segments.add(opposite);
				 startNode.addInSegs(opposite);
				 endNode.addOutSegs(opposite);
			 }
		}
			segsData.close();
		}catch (IOException e){
			e.printStackTrace();
		}
		}
	
	public void loadPolygon(File f){
		if(f == null) return; //if loading small map and no polygon file
		try{
			BufferedReader polyData = new BufferedReader(new FileReader(f));
			String line; 
			String type = "", label = "", endLevel = "", cityIdx = "";
			List<Location> coords = new ArrayList<Location>();
			line = polyData.readLine();
			while((line = polyData.readLine()) != null){
				if(line.equals("[POLYGON]")){
					line = polyData.readLine();
					if(!line.equals("[END]")){
						if(line.contains("Type")){ type = line.split("=")[1]; line = polyData.readLine();} //subtract string after "="
						if(line.contains("Label")){ label = line.split("=")[1]; line = polyData.readLine();}
						if(line.contains("EndLevel")){ endLevel = line.split("=")[1]; line = polyData.readLine();}
						if(line.contains("CityIdx")){ cityIdx = line.split("=")[1]; line = polyData.readLine();}
						if(line.contains("Data0")){
							String allCoordsLine = line.split("=")[1];
							coords = new ArrayList<Location>();
							String[] allCoords = allCoordsLine.replaceAll("\\(","").replaceAll("\\)","").split(","); //put all the coords into an array
							for(int i=0; i<allCoords.length-1; i+=2){ 
								Double lat = Double.parseDouble(allCoords[i]);
								Double lon = Double.parseDouble(allCoords[i+1]);
								
								Location l = Location.newFromLatLon(lat, lon);
								coords.add(l);
							}
							line = polyData.readLine();
						}
					}
					Polygonn p = new Polygonn(type, label, endLevel, cityIdx, coords);
					polygons.add(p);
				}
			}
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void loadRestrictions(File f){
		if(f == null) return;
		try{
			BufferedReader restrictionData = new BufferedReader(new FileReader(f));
			String line;
			restrictionData.readLine();
			while((line = restrictionData.readLine()) != null){
				String[] values = line.split("\t");
				int nodeID1 = Integer.parseInt(values[0]);
				int roadID1 = Integer.parseInt(values[1]);
				int nodeID = Integer.parseInt(values[2]);
				int nodeID2 = Integer.parseInt(values[3]);
				int roadID2 = Integer.parseInt(values[4]);
				
				Restriction newRes = new Restriction(nodeID1, roadID1, nodeID, nodeID2, roadID2);
				restrictions.add(newRes);
			}
			restrictionData.close();
		}catch (IOException e){
			e.printStackTrace();
		}
		
	}
		
		@Override
	protected void onLoad(File nodes, File roads, File segments, File polygons, File restrictions){
		loadNodes(nodes);
		loadRoads(roads);
		loadSegs(segments);
		loadPolygon(polygons);
		loadRestrictions(restrictions);
		System.out.println("loaded file");
	}
	
	public List<Segment> findPath(Node start, Node end, boolean distance){
		lengthCheck = new HashMap<String, Double>();
		roadList = new ArrayList<Segment>();
		Set<Node> visited = new HashSet<Node>();
		PriorityQueue<AStarNode> fringe = new PriorityQueue<AStarNode>();
		AStarNode initial = new AStarNode(start, null, 0, start.distanceTo(end));
		fringe.offer(initial);
			
		while(!fringe.isEmpty()){
			AStarNode current = fringe.poll();
			visited.add(current.getNode());
				
			if(current.getNode() == end){ //found;
				List<Segment> path = new ArrayList<Segment>();
				//backtrack from node to be stored in path
				AStarNode backTrack = current;
				while(backTrack != initial){
					Segment pathSeg = backTrack.pathSeg();
					path.add(pathSeg);
					if(lengthCheck.containsKey(pathSeg.getRoad().getName()))
						lengthCheck.put(pathSeg.getRoad().getName(), lengthCheck.get(pathSeg.getRoad().getName()) + pathSeg.getLength());
					else{
						roadList.add(pathSeg);
						lengthCheck.put(pathSeg.getRoad().getName(), pathSeg.getLength());
					}
					backTrack = backTrack.getFrom();
				}
				
				Collections.reverse(path);
				Collections.reverse(roadList);
				return path;
			}
			
			for(Segment s: current.getNode().getOutSegs()){
				//if the road can be used by car and no restriction
				if(!s.getRoad().notForCar() && current.canMove(s.getEndNode(), restrictions)){ 
					Node newNode = s.getEndNode();
					if(!visited.contains(newNode)){
						double costToNeigh = s.getLength();
						if(!distance){
							//if the intersection has many segments connected to it, traffic would be heavier
							//adjusted the multiply factor a bit to make it has more impact 
							costToNeigh *= 1 + (newNode.getNeighbours().size()*0.5);
							
							//cost to neigh adjust according to roadLength/speed, which is time
							if(s.getRoad().getSpeedLimit() == 7) costToNeigh /= 150;
							if(s.getRoad().getSpeedLimit() == 6) costToNeigh /= 110;
							if(s.getRoad().getSpeedLimit() == 5) costToNeigh /= 100;
							if(s.getRoad().getSpeedLimit() == 4) costToNeigh /= 80;
							if(s.getRoad().getSpeedLimit() == 3) costToNeigh /= 60;
							if(s.getRoad().getSpeedLimit() == 2) costToNeigh /= 40;
							if(s.getRoad().getSpeedLimit() == 1) costToNeigh /= 20;
							if(s.getRoad().getSpeedLimit() == 0) costToNeigh /= 5;
						}
						double newCost = current.getCostFromStart() + costToNeigh;
						double newTotal = newCost + newNode.distanceTo(end);
						AStarNode neighbour = new AStarNode(newNode, current, newCost, newTotal);
						fringe.offer(neighbour);
					}
				}
			}
		}
		return new ArrayList<Segment>();
	}
	
	public Set<Node> findArtiPts(Node first, ArtiNode root){
		Stack<ArtiNode> stack = new Stack<ArtiNode>();
		Set<Node> artiPts = new HashSet<Node>();
		for(Node n: nodes.values())
			n.setDepth(Integer.MAX_VALUE);
		stack.push(new ArtiNode(first, 1, root));
		while(!stack.isEmpty()){
			ArtiNode element = stack.peek();
			if(element.getNode().getDepth() == Integer.MAX_VALUE){	//first time
				element.getNode().setDepth(element.getDepth());
				element.setReach(element.getDepth());
				for(Node neighbour: element.getNode().getNeighbours()){
					if(neighbour != element.getParent().getNode()){
						element.addChild(neighbour);
					}
				}
			}else if(!element.getChildren().isEmpty()){	//children to process
				Node child = element.getChildren().poll();
				if(child.getDepth() < Integer.MAX_VALUE){
					element.setReach(Math.min(element.getReach(), child.getDepth()));
				}else{
					stack.push(new ArtiNode(child, element.getNode().getDepth()+1, element));
				}
			}else{	//last time
				if(element.getNode() != first){
					if(element.getReach() >= element.getParent().getDepth()){
						artiPts.add(element.getParent().getNode());
					}
					element.getParent().setReach(Math.min(element.getParent().getReach(), element.getReach()));
				}
				stack.pop();
			}
		}
		
		return artiPts;
	}
		
	public static void main(String[] args) {
		new Graph();
	}

	
}

