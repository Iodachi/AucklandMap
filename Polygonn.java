import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

public class Polygonn {
	private String type, label, endLevel, cityIdx;
	private List<Location> location;
	private Color airport= new Color(245, 240, 227);
	private Color shoppingCentre = new Color(247, 232, 203);
	private Color water = new Color(170, 204, 251);
	private Color golf = new Color(218, 232, 190);
	private Color hospital = new Color(247,237,241);
	private Color airfield = new Color(215, 212, 208);
	private Color school = new Color(228, 228, 223);
	private Color forest = new Color(192, 226, 146);
	private Color park = new Color(208, 228, 170);
	private Color building = new Color(228, 225, 222);
	
	private Color c;
	
	public Polygonn(String t, String l, String el, String ci, List<Location>loc){
		this.type = t;
		this.label = l;
		this.endLevel = el;
		this.cityIdx = ci;
		this.location = loc;
	}
	
	public void colorCheck(){
		Integer decodedType = Integer.decode(type); 
		if(decodedType == 7) c = airport;
		if(decodedType == 5 || decodedType == 8) c = shoppingCentre; 
		if(decodedType == 10) c = school; 
		if(decodedType == 11) c = hospital;
		if(decodedType == 14) c = airfield; 
		if(decodedType == 22 || decodedType == 65) c = forest; 
		if(decodedType == 23 || decodedType == 80 || decodedType == 25 || decodedType == 26 || decodedType == 30) c = park; 
		if(decodedType == 24) c = golf; 
		if(decodedType == 40 || decodedType == 60 || decodedType == 62 || decodedType == 69 || decodedType == 71 || decodedType == 72) c = water;
		if(decodedType == 19) c = building; 
	}
	
	public void draw(Graphics g, Location origin, double scale, int shiftX, int shiftY, Point nw){
		//g.setColor(Color.BLUE);
		colorCheck();
		g.setColor(c);
		int[] pointX = new int[this.location.size()];
		int[] pointY = new int[this.location.size()];
		
		for(int i = 0; i < this.location.size(); i++){
			Point p = this.location.get(i).asPoint(origin, scale);
			pointX[i] = (int)(p.x + shiftX - nw.x);;
			pointY[i] = (int)(p.y + shiftY - nw.y);
		}
		g.fillPolygon(pointX,pointY,this.location.size());
	}
}
