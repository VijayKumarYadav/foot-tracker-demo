package com.horizon;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Polygon;

public class PolygonBuilder {
	
	public List<MyPolygon> myPolygons = new ArrayList<MyPolygon>();

    public PolygonBuilder() {
    	/***** TO CHANGE *****/
    	
    	CoordinateTransformation transformer = new CoordinateTransformation();
    	transformer.init(-1.1840035, 52.9518518);

    	/*********************/
    	FileHandle file = Gdx.files.internal("polygons.txt");
    	String txt = file.readString();
    	String polygons_txt[] = txt.split("\\r?\\n");
    	
    	for(int i=0; i<polygons_txt.length; i++){
    		myPolygons.add(new MyPolygon(polygons_txt[i], transformer));
    	}
    }
    
    public String toString(){
    	StringBuilder builder = new StringBuilder();
    	
    	for(int i=0; i<myPolygons.size(); i++){
    		builder.append(myPolygons.get(i).toString());
    		builder.append("\n");
    	}
    	
    	return builder.toString();
    }
    
    public List<Mesh> toMesh(){
    	List<Mesh> polygonsMesh = new ArrayList<Mesh>();
    	
    	for(int i=0; i<myPolygons.size(); i++){
    		polygonsMesh.add(myPolygons.get(i).toMesh());
    	}
    	
    	return polygonsMesh;
    }
    
    public List<Polygon> toPolygon(){
    	List<Polygon> polygonsMesh = new ArrayList<Polygon>();
    	
    	for(int i=0; i<myPolygons.size(); i++){
    		polygonsMesh.add(myPolygons.get(i).toPolygon());
    	}
    	
    	return polygonsMesh;
    }
	
}
