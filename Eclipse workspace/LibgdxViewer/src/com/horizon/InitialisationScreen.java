package com.horizon;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;

public class InitialisationScreen implements Screen{
	private OpeningInDoorsGame 				game;
	
    public OrthographicCamera      			cam;
    public OrthographicCamera      			uiCam;
    private Rectangle                       glViewport;
    private float                           rotationSpeed;
    public ShapeRenderer 					renderer;
    public InitialisationController			initialisationController = null;
    CameraController 						controller;
    GestureDetector 						gestureDetector;
    
    List<float[]> 							particles = new ArrayList<float[]>();
    
    CoordinateTransformation 				transformer;
	
    public InitialisationScreen (OpeningInDoorsGame game) {
    	this.game = game;

    	renderer = new ShapeRenderer();
	    
        rotationSpeed = 0.5f;
        cam = new OrthographicCamera(game.WIDTH, game.HEIGHT);
        uiCam = new OrthographicCamera(game.WIDTH, game.HEIGHT);
        
    	controller = new CameraController(cam);
        gestureDetector = new GestureDetector(20, 0.5f, 2, 0.15f, controller);
        
        cam.position.set(0.0f, 0.0f, 0);
        cam.zoom = 1f;
        
        uiCam.position.set(0.0f, 0.0f, 0);
        uiCam.zoom = 1f;
        
        glViewport = new Rectangle(0, 0, game.WIDTH, game.HEIGHT);
        
    	this.initialisationController = new InitialisationController(game, this.cam, this.uiCam);
        Gdx.input.setInputProcessor(new GestureDetector(this.initialisationController));
    }
    
	@Override
	public void render(float delta) {
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

        	if(game.particles.size() == 1){
            	gl.glPointSize(5);
	        	renderer.point(game.particles.get(0)[0], game.particles.get(0)[1], 0);
        	} else {
    	        gl.glPointSize(1);
		        for(int i=0; i<game.particles.size(); i++){
		        	renderer.point(game.particles.get(i)[0], game.particles.get(i)[1], 0);
		        }
        	}
        } catch(Exception e) {}
        renderer.end();      
        
        for(int i=0; i<game.polygonsMesh.size(); i++){
        	game.polygonsMesh.get(i).render(GL10.GL_LINE_STRIP);
        }	
	}

	@Override
	public void resize(int width, int height) {
		game.WIDTH = width;
		game.HEIGHT = height;
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(gestureDetector);
		Gdx.input.setInputProcessor(new GestureDetector(this.initialisationController));
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
    private void handleInput() {
        if(Gdx.input.isKeyPressed(Input.Keys.BACK)) {
        	game.setScreen(game.mainMenuScreen);
        }
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

}
