package com.horizon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class MainMenuScreen implements Screen {
	OpeningInDoorsGame game;

	OrthographicCamera guiCam;
	SpriteBatch batcher;

	Vector3 touchPoint;
	
	// TEXTURES
	Texture					newGameTexture;
	Texture					configurationTexture;
	Texture					demonstrateTexture;
	Texture					bluetoothConnectedTexture;
	Texture					bluetoothNotConnectedTexture;
	Texture					bluetoothStatusTexture;
	
	// BUTTONS BOUNDS
	Rectangle newGameBounds;
	Rectangle configurationBounds;
	Rectangle bluetoothBounds;
	Rectangle demonstrateBounds;
	
	BitmapFont font;
	
	public MainMenuScreen(OpeningInDoorsGame game){
		this.game = game;
		this.font = new BitmapFont();
		
		this.newGameTexture =  new Texture(Gdx.files.internal("buttons/new_game.png"));
		this.configurationTexture =  new Texture(Gdx.files.internal("buttons/configuration.png"));
		this.demonstrateTexture =  new Texture(Gdx.files.internal("buttons/demonstrate.png"));
		this.bluetoothConnectedTexture =  new Texture(Gdx.files.internal("buttons/bluetooth_connected.png"));
		this.bluetoothNotConnectedTexture =  new Texture(Gdx.files.internal("buttons/bluetooth_notconnected.png"));
		
		guiCam = new OrthographicCamera(game.WIDTH, game.HEIGHT);
		guiCam.position.set(game.WIDTH / 2, game.HEIGHT / 2, 0);
		batcher = new SpriteBatch();
		
		bluetoothBounds = new Rectangle(-128, game.HEIGHT/2 - 100, 256, 64);
		newGameBounds = new Rectangle(-128, 0, 256, 64);
		demonstrateBounds = new Rectangle(-128, -20-64, 256, 64);
		configurationBounds = new Rectangle(-128, (-20-64)*2, 256, 64);	
		
		touchPoint = new Vector3();
	}
	
	@Override
	public void render(float delta) {
		update(delta);
		draw(delta);	
	}

	private void draw(float deltaTime) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        batcher.setProjectionMatrix(guiCam.combined);
        batcher.begin();
		if(game.bluetoothInterface.isConnected()){
			batcher.flush();
			batcher.draw(bluetoothConnectedTexture, -128, game.HEIGHT/2 - 100);		
		} else {
			batcher.draw(bluetoothNotConnectedTexture, -128, game.HEIGHT/2 - 100);
		}     
        batcher.draw(newGameTexture, -128, 0);
        batcher.draw(demonstrateTexture, -128, - 20 - 64);
        batcher.draw(configurationTexture, -128, ( -20 -64)*2);
        batcher.end();
	}

	private void update(float deltaTime) {
		if (Gdx.input.justTouched()) {
			guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

			if (Utils.pointInRectangle(bluetoothBounds, touchPoint.x, touchPoint.y)) {
				game.bluetoothInterface.connect();
				System.out.println("CONNECT BLUETOOTH");
				return;
			} 
			if (Utils.pointInRectangle(newGameBounds, touchPoint.x, touchPoint.y)) {
				sendConfiguration();
				game.setScreen(game.initialisationScreen);
				System.out.println("NEW GAME");
				return;
			} 
			if (Utils.pointInRectangle(demonstrateBounds, touchPoint.x, touchPoint.y)) {
				sendConfiguration();
				game.setScreen(game.particleFilterScreen);
				System.out.println("DEMONSTRATE PARTICLE FILTER");
				return;
			} 
			if (Utils.pointInRectangle(configurationBounds, touchPoint.x, touchPoint.y)) {
				System.out.println("CONFIGURATION");
				game.setScreen(game.configurationScreen);
				return;
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		game.WIDTH = width;
		game.HEIGHT = height;
		guiCam = new OrthographicCamera(game.WIDTH, game.HEIGHT);
	}

	@Override
	public void show() {
		Gdx.input.setCatchBackKey(false);
	}

	@Override
	public void hide() {
		Gdx.input.setCatchBackKey(true);
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
	
	private void sendConfiguration(){
		game.bluetoothInterface.sendMessage("projection = GEO " + ParticleFilterParameters.projection_latitude + " " + ParticleFilterParameters.projection_longitude + " 0\n");
		game.bluetoothInterface.sendMessage("building id = " + ParticleFilterParameters.buildingId + "\n");
		game.bluetoothInterface.sendMessage("floor level = " + ParticleFilterParameters.floor + "\n");
		game.bluetoothInterface.sendMessage("initial position std = " + ParticleFilterParameters.positionStd + "\n");		
		game.bluetoothInterface.sendMessage("initial heading std = " + ParticleFilterParameters.headingStd + "\n");
		game.bluetoothInterface.sendMessage("particles number = " + ParticleFilterParameters.particlesNumber + "\n");
		game.bluetoothInterface.sendMessage("step length noise std = " + ParticleFilterParameters.stepLengthNoiseStd + "\n");
		game.bluetoothInterface.sendMessage("step course over ground noise std = " + ParticleFilterParameters.stepCOGNoiseStd + "\n");
		game.bluetoothInterface.sendMessage("display cloud as mean = " + ParticleFilterParameters.displayCloudAsMean + "\n");
	}

}
