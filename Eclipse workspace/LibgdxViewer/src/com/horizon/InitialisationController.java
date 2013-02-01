package com.horizon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class InitialisationController implements GestureListener {
	OpeningInDoorsGame 	game;
	
    float 				velX, velY;
    boolean 			flinging = false;
    float 				initialScale = 1;
    
    OrthographicCamera 	worldCamera;
    OrthographicCamera 	uiCamera;
    ShapeRenderer 		renderer;
    
    Vector3 			touchPoint;
    
    Point 				startPt;
    float 				heading;
    
    Texture 			positionTexture;
    Texture 			validateTexture;
    SpriteBatch 		spriteBatch;
    BitmapFont			font;
    
    String 				leftTxt;
    
    String 				rightTxt;
    float				rightTxtWidth;
    
    Rectangle			validationButton;
    
    boolean 			startPtValidated = false;
    boolean				isInitialisationDone = false;

    public InitialisationController(OpeningInDoorsGame game, OrthographicCamera worldCamera, OrthographicCamera uiCamera){
    	this.game = game;
    	
    	this.worldCamera = worldCamera;
    	this.uiCamera = uiCamera;
    	
    	this.renderer = new ShapeRenderer();
    	
    	this.touchPoint = new Vector3();
    	this.startPt = null;
    	this.heading = 999;
    	
    	this.spriteBatch = new SpriteBatch();
    	
    	this.positionTexture =  new Texture(Gdx.files.internal("position_marker.png"));
    	this.validateTexture =  new Texture(Gdx.files.internal("validate.png"));
    	
    	this.font = new BitmapFont();
    	this.leftTxt = "Tap screen to input your start point";
    	this.rightTxt = "Validate start point ?";
    	this.rightTxtWidth = font.getBounds(rightTxt).width;
    	
    	this.validationButton = new Rectangle(Viewer.WIDTH/2 - 100, -Viewer.HEIGHT/2, 100, 100);
    }
    
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
        flinging = false;
        initialScale = worldCamera.zoom;
        return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
        Gdx.app.log("GestureDetectorTest", "tap at " + x + ", " + y + ", count: " + count);
        
        uiCamera.unproject(touchPoint.set(x, y, 0));
        
        if(!Utils.pointInRectangle(validationButton, touchPoint.x, touchPoint.y)){
        	worldCamera.unproject(touchPoint.set(x, y, 0));
        	
        	System.out.println("TOTO" + touchPoint.x + " " + touchPoint.y);
        	
        	if(!startPtValidated){
            	startPt = new Point(touchPoint.x, touchPoint.y, touchPoint.z);
            } else {
            	float deltaX = touchPoint.x - startPt.x;
            	float deltaY = touchPoint.y - startPt.y;
            	heading = (float) Math.toDegrees(Math.atan2(deltaX, deltaY));
            }
        } else {
            if(!startPtValidated){
            	if(startPt == null) return false;
            	this.leftTxt = "Tap screen to input your heading";
            	this.rightTxt = "Validate heading ?";
            	this.rightTxtWidth = font.getBounds(rightTxt).width;
            	startPtValidated = true;
            } else {
            	if(heading == 999) return false;
            	writeToConfig();
            	isInitialisationDone = true;
            }
        }    
        return false;
	}

	@Override
	public boolean longPress(float x, float y) {
        Gdx.app.log("GestureDetectorTest", "long press at " + x + ", " + y);
        return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
        flinging = true;
        velX = worldCamera.zoom * velocityX * 0.5f;
        velY = worldCamera.zoom * velocityY * 0.5f;
        return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
        worldCamera.position.add(-deltaX * worldCamera.zoom, deltaY * worldCamera.zoom, 0);
        return false;
	}

	@Override
	public boolean zoom(float originalDistance, float currentDistance) {
        float ratio = originalDistance / currentDistance;
        worldCamera.zoom = initialScale * ratio;
        System.out.println(worldCamera.zoom);
        return false;
	}

	@Override
	public boolean pinch(Vector2 initialFirstPointer,
			Vector2 initialSecondPointer, Vector2 firstPointer,
			Vector2 secondPointer) {
		// TODO Auto-generated method stub
		return false;
	}
	
    public void update () {
        if (flinging) {
                velX *= 0.98f;
                velY *= 0.98f;
                worldCamera.position.add(-velX * Gdx.graphics.getDeltaTime(), velY * Gdx.graphics.getDeltaTime(), 0);
                if (Math.abs(velX) < 0.01f) velX = 0;
                if (Math.abs(velY) < 0.01f) velY = 0;
        }
    }
    
    
    public void render() {
    	
       /*if(startPt != null){      
	        spriteBatch.setProjectionMatrix(this.camera.combined);
	        spriteBatch.begin();
	        spriteBatch.draw(positionTexture, startPt.x, startPt.y);
	        spriteBatch.end();
        }*/
    	
        GL10 gl = Gdx.graphics.getGL10();
         
        spriteBatch.setProjectionMatrix(this.uiCamera.combined);
        spriteBatch.begin();
        font.draw(spriteBatch, leftTxt, -Viewer.WIDTH/2 + 10, -Viewer.HEIGHT/2 + 30);
        font.draw(spriteBatch, rightTxt, Viewer.WIDTH/2 - rightTxtWidth - 100, -Viewer.HEIGHT/2 + 30);
        spriteBatch.draw(this.validateTexture, Viewer.WIDTH/2 - 75, -Viewer.HEIGHT/2);
        spriteBatch.end();
        
        if(startPt != null){
        	gl.glPointSize(10);
            renderer.setProjectionMatrix(this.worldCamera.combined);
            renderer.begin(ShapeType.Point);
            renderer.setColor(0, 0, 1, 1);
        	renderer.point(startPt.x, startPt.y, startPt.z);
        	renderer.end();
        	if(heading != 999){
        		renderer.begin(ShapeType.Line);
        		renderer.line(startPt.x, startPt.y, (float)(startPt.x + 5*Math.sin(Math.toRadians(heading))), (float) (startPt.y + 5*Math.cos(Math.toRadians(heading))));
        		renderer.end();
        	}
        	gl.glPointSize(1);
        }
    }
    
    private void writeToConfig(){
    	ParticleFilterParameters.startPt = this.startPt;
    	ParticleFilterParameters.heading = this.heading;
    	game.bluetoothInterface.sendMessage("initial position = GEO " + ParticleFilterParameters.startPt.getLatitude() + " " + ParticleFilterParameters.startPt.getLongitude() + " 0\n");
    	game.bluetoothInterface.sendMessage("initial heading = " + ParticleFilterParameters.heading + "\n");
    	game.bluetoothInterface.sendMessage("run particle filter = true\n");
		System.out.println("initial position = GEO " + ParticleFilterParameters.startPt.getLatitude() + " " + ParticleFilterParameters.startPt.getLongitude() + " 0\n");
		System.out.println("initial heading = " + ParticleFilterParameters.heading + "\n");
    }
}
