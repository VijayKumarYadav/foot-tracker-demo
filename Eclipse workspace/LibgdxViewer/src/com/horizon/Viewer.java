package com.horizon;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;

public class Viewer implements ApplicationListener {
    public static final int WIDTH  = 1024;
    public static final int HEIGHT = 600;

    public OrthographicCamera      			cam;
    public OrthographicCamera      			uiCam;
    private Rectangle                       glViewport;
    private float                           rotationSpeed;
    public List<Mesh> 						polygonsMesh = new ArrayList<Mesh>();
    public ShapeRenderer 					renderer;
    public InitialisationController			initialisationController = null;
    CameraController 						controller;
    GestureDetector 						gestureDetector;
    
    List<float[]> 							particles = new ArrayList<float[]>();
    
    CoordinateTransformation 				transformer;
    
    @Override
    public void create() {
        
    	/***** TO CHANGE *****/
    	
    	transformer = new CoordinateTransformation();
    	transformer.init(-1.1840035, 52.9518518);
    	
    	Point.transformer = transformer;

    	/*********************/
    	
    	renderer = new ShapeRenderer();
    	    
        rotationSpeed = 0.5f;
        cam = new OrthographicCamera(WIDTH, HEIGHT);
        uiCam = new OrthographicCamera(WIDTH, HEIGHT);
        
    	controller = new CameraController(cam);
        gestureDetector = new GestureDetector(20, 0.5f, 2, 0.15f, controller);
        Gdx.input.setInputProcessor(gestureDetector);

        cam.position.set(0.0f, 0.0f, 0);
        cam.zoom = 1f;
        
        uiCam.position.set(0.0f, 0.0f, 0);
        uiCam.zoom = 1f;
        
        glViewport = new Rectangle(0, 0, WIDTH, HEIGHT);
    }

    @Override
    public void dispose() { }

    @Override
    public void pause() { }


    @Override
    public void render() {
        handleInput();
        GL10 gl = Gdx.graphics.getGL10();
        
        // Camera --------------------- /
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        gl.glViewport((int) glViewport.x, (int) glViewport.y,
                        (int) glViewport.width, (int) glViewport.height);
        controller.update();
        cam.update();
        cam.apply(gl);
         
    	if(initialisationController != null){
    		if(initialisationController.isInitialisationDone){
    			initialisationController = null;
    		} else {
    			initialisationController.render();
    		}    	
        }
        
        renderer.setProjectionMatrix(cam.combined);
        renderer.begin(ShapeType.Point);
        renderer.setColor(1, 0, 0, 1);

        try{
        	gl.glPointSize(5);  
	        for(int i=0; i<this.particles.size(); i++){
	        	renderer.point(this.particles.get(i)[0], this.particles.get(i)[1], 0);
	        }
	        gl.glPointSize(1);
        } catch(Exception e) {}
        renderer.end();      
        
        for(int i=0; i<polygonsMesh.size(); i++){
        	polygonsMesh.get(i).render(GL10.GL_LINE_STRIP);
        }
    }
    
    public void updatePosition(Point position){
    	//mesh.setVertices(new float[] {(float) position.lon,(float) position.lat,(float) position.height,Color.toFloatBits(0, 0, 255, 255)});
    }
    
    private void handleInput() {
        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
                cam.zoom += 0.1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
                cam.zoom -= 0.1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                        cam.translate(cam.zoom*-3, 0, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                        cam.translate(cam.zoom*3, 0, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                if (cam.position.y > 0)
                        cam.translate(0, cam.zoom*-3, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
                        cam.translate(0, cam.zoom*3, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
                cam.rotate(-rotationSpeed, 0, 0, 1);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.E)) {
                cam.rotate(rotationSpeed, 0, 0, 1);
        }
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void resume() { }
    

    public void getParticlesBluetooth(String msg){
    	List<float[]> myList = new ArrayList<float[]>();
    	String[] location;
        Pattern p = Pattern.compile("\\((.*?)\\)");
        Matcher m = p.matcher(msg); 
        
        myList.clear();
        
        while(m.find()){
            location = m.group(1).split("[,]");
            try {
            	myList.add(new float[]{Float.parseFloat(location[1]),Float.parseFloat(location[0])});
            } catch(NumberFormatException nfe) {
            	System.out.print("TOTO erreur\n");
            }
        }
        
        this.particles = myList;
    }
    
    public void startInitialisation(){
    	//this.initialisationController = new InitialisationController(this.cam, this.uiCam);
        //Gdx.input.setInputProcessor(new GestureDetector(this.initialisationController));
    }
}
