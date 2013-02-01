package com.horizon;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;

public class Wall {
	
	public Point startPoint;
	public Point endPoint;
	
	public Wall(String txt){
		String[] points = txt.split(" ");
		//startPoint = new Point(points[0]);
		//endPoint = new Point(points[1]);
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("startPoint = ");
		builder.append(startPoint.toString());
		builder.append(" ---- endPoint = ");
		builder.append(endPoint.toString());
		return builder.toString();
	}
	
	public List<Float> getVertices(){
		float color = Color.toFloatBits(255, 0, 0, 255);
		
		List<Float> wallVertices = new ArrayList<Float>();
		
		//wallVertices.add((float) startPoint.lon);
		//wallVertices.add((float)startPoint.lat);
		//wallVertices.add((float)startPoint.height);
		//wallVertices.add(color);
		//wallVertices.add((float)endPoint.lon);
		//wallVertices.add((float)endPoint.lat);
		//wallVertices.add((float)endPoint.height);
		//wallVertices.add(color);
		
		return wallVertices;
	}
}
