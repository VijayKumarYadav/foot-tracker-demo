package com.horizon;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

public class Walls {
	
	public List<Wall> walls = new ArrayList<Wall>();
	
	public Walls(){
		createFromAssets();
	}
	
    private void createFromAssets(){
    	FileHandle file = Gdx.files.internal("NGB_simple.txt");
    	String text = file.readString();
    	String walls_txt[] = text.split("\\r?\\n");
    	
    	for(int i=0; i<walls_txt.length; i++){
    		walls.add(new Wall(walls_txt[i]));
    	}
    }
    
    public String toString(){
    	StringBuilder builder = new StringBuilder();
    	
    	for(int i=0; i<walls.size(); i++){
    		builder.append(walls.get(i).toString());
    		builder.append("\n");
    	}
    	
    	return builder.toString();
    }
    
    public Mesh Draw(){
    	List<Float> wallsVertices = new ArrayList<Float>();
    	

    	
    	for(int i=0; i<walls.size(); i++){
    		wallsVertices.addAll(walls.get(i).getVertices());
    	}
    	
    	Mesh wallsMesh = new Mesh(true, wallsVertices.size(), wallsVertices.size(),
                new VertexAttribute(Usage.Position, 3, "a_position"),
                new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
    	
    	float[] verticesArray = new float[wallsVertices.size()];
    	short[] indicesArray = new short[wallsVertices.size()];
    	

    	for (short i = 0; i < wallsVertices.size(); i++) {
    	    Float f = wallsVertices.get(i);
    	    verticesArray[i] = (f != null ? f : Float.NaN);
    	    indicesArray[i] = i;
    	}
    	
        wallsMesh.setVertices(verticesArray);
        wallsMesh.setIndices(indicesArray);
    	
    	return wallsMesh;
    }
    
}
