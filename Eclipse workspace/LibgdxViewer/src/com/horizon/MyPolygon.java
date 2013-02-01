package com.horizon;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Polygon;

public class MyPolygon {

	public List<Point> points = new ArrayList<Point>();
	private int arraySize;
	
	public MyPolygon(String txt, CoordinateTransformation transformer){
		Point pt;
		String[] points_txt = txt.split(" ");
		for(int i=0; i<points_txt.length; i++){
			//pt = new Point(points_txt[i]);
			//pt = transformer.geoToXY(pt);
			//points.add(pt);
		}
		arraySize = points.size()*4;
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		for(int i=0; i<points.size(); i++){
			builder.append(points.get(i).toString());
		}
		short[] indices = this.getIndices();
		return builder.toString();
	}
	
	public Mesh toMesh(){
    	Mesh mesh = new Mesh(true, points.size(), points.size()+1,
                new VertexAttribute(Usage.Position, 3, "a_position"),
                new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
    	
    	mesh.setVertices(this.getVertices());
    	mesh.setIndices(this.getIndices());
    	
    	return mesh;
	}
	
	public Polygon toPolygon(){
		return new Polygon(getVertices());
	}
	
	public float[] getVertices(){
		float color = Color.toFloatBits(255, 255, 255, 255);
		float[] vertices = new float[arraySize];
		
		for(int i=0; i<points.size()-1; i++){
			//vertices[4*i] = points.get(i).lat;
			//vertices[4*i+1] = points.get(i).lon;
			vertices[4*i] = (float)points.get(i).x;
			vertices[4*i+1] = (float)points.get(i).y;
			vertices[4*i+2] = (float)points.get(i).z;
			vertices[4*i+3] = color;
			System.out.print("TOTAL polygon X = " + points.get(i).x + " " + points.get(i).y);
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
