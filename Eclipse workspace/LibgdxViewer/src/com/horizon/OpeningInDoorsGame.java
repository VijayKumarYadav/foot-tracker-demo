package com.horizon;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Mesh;

public class OpeningInDoorsGame extends Game {
	
	public int 							WIDTH = 1024;
	public int 							HEIGHT = 600;

	public BluetoothInterface 			bluetoothInterface;
	public CoordinateTransformation 	transformer;
	
	public List<Mesh>					polygonsMesh = new ArrayList<Mesh>();
	public List<float[]> 				particles = new ArrayList<float[]>();
	
	public MainMenuScreen				mainMenuScreen;
	public ConfigurationScreen			configurationScreen;
	public InitialisationScreen			initialisationScreen;
	public ParticleFilterScreen			particleFilterScreen;
	
	
	public OpeningInDoorsGame(BluetoothInterface bluetoothInterface){
		this.bluetoothInterface = bluetoothInterface;
	}
	
	@Override
	public void create() {
		transformer = new CoordinateTransformation();
		transformer.init(ParticleFilterParameters.projection_longitude, ParticleFilterParameters.projection_latitude);
		Point.transformer = transformer;
		
		mainMenuScreen = new MainMenuScreen(this);
		configurationScreen = new ConfigurationScreen(this);
		initialisationScreen = new InitialisationScreen(this);
		particleFilterScreen = new ParticleFilterScreen(this);
		
		setScreen(mainMenuScreen);
	}

	public void getParticlesBluetooth(String msg) {
    	List<float[]> myList = new ArrayList<float[]>();
    	String[] location;
        Pattern p = Pattern.compile("\\((.*?)\\)");
        Matcher m = p.matcher(msg); 
        
        myList.clear();
        
        while(m.find()){
            location = m.group(1).split("[,]");
            try {
            	myList.add(new float[]{Float.parseFloat(location[0]),Float.parseFloat(location[1])});
            	System.out.println("POINT X = " + myList.get(myList.size()-1)[0] + " Y = " + myList.get(myList.size()-1)[1]);
            } catch(NumberFormatException nfe) {
            	System.out.print("TOTO erreur\n");
            }
        }
        
        this.particles = myList;		
	}
}
