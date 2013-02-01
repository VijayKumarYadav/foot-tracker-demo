package com.horizon;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;

public class Room {

	List<Point> points = new ArrayList<Point>();
	
	public float[] getVertices(){
		float color = Color.toFloatBits(255, 255, 255, 255);
		float[] vertices = new float[points.size()*4];
		
		for(int i=0; i<points.size(); i++){
			vertices[4*i] = points.get(i).x;
			vertices[4*i+1] = points.get(i).y;
			vertices[4*i+2] = points.get(i).z;
			vertices[4*i+3] = color;
		}	
		
		return vertices;
	}
	
	public short[] getIndices(){
		short[] indices = new short[points.size()];
		
		for(short i=0; i<points.size(); i++){
			indices[i] = i;
		}
		
		indices[points.size()-1] = 0;
		
		return indices;
	}
}
