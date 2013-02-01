package com.horizon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class ConfigurationScreen implements Screen{

	OpeningInDoorsGame 	game;
	
	private Stage 		stage;
	private Skin 		skin;
	
	private Label 		posStdValue;
	private Slider 		posStdSlider;
	
	private Label 		headingStdValue;
	private Slider 		headingStdSlider;
	
	private Label 		numParticlesValue;
	private Slider 		numParticlesSlider;
	
	private Label 		stepLengthStdValue;
	private Slider 		stepLengthStdSlider;
	
	private Label 		stepCOGStdValue;
	private Slider 		stepCOGStdSlider;
	
	private CheckBox 	mean;
	private CheckBox	cloud;
	
	public ConfigurationScreen(OpeningInDoorsGame game){
		this.game = game;
		
        stage = new Stage(game.WIDTH, game.HEIGHT, false);
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));      
        
        this.posStdValue = new Label("", skin);
        this.headingStdValue = new Label("", skin);
        this.numParticlesValue = new Label("", skin);
        this.stepLengthStdValue = new Label("", skin);
        this.stepCOGStdValue = new Label("", skin);
        
        posStdSlider = new Slider(0, 10, (float) 0.5, false, skin);
        posStdSlider.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				posStdValue.setText(" " + Float.toString(posStdSlider.getValue()) + " meters");
			}
		});
        headingStdSlider = new Slider(0, 360, (float) 1, false, skin);
        headingStdSlider.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				headingStdValue.setText(" " + Integer.toString((int)headingStdSlider.getValue()) + " degrees");
			}
		});
        numParticlesSlider = new Slider(0, 1000, (float) 10, false, skin);
        numParticlesSlider.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				numParticlesValue.setText(" " + Integer.toString((int)numParticlesSlider.getValue()) + " particles");
			}
		});
        stepLengthStdSlider = new Slider(0, (float) 0.5, (float) 0.05, false, skin);
        stepLengthStdSlider.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				stepLengthStdValue.setText(" " + Float.toString(stepLengthStdSlider.getValue()) + " meters");
			}
		});
        stepCOGStdSlider = new Slider(0, 10, (float) 0.5, false, skin);
        stepCOGStdSlider.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				stepCOGStdValue.setText(" " + Float.toString(stepCOGStdSlider.getValue()) + " degrees");
			}
		});
        
        posStdSlider.setValue(ParticleFilterParameters.positionStd);
        headingStdSlider.setValue(ParticleFilterParameters.headingStd);
        numParticlesSlider.setValue(ParticleFilterParameters.particlesNumber);
        stepLengthStdSlider.setValue(ParticleFilterParameters.stepLengthNoiseStd);
        stepCOGStdSlider.setValue(ParticleFilterParameters.stepCOGNoiseStd);
        
        mean = new CheckBox(" Mean", skin);
        cloud = new CheckBox(" Cloud", skin);
      
        ButtonGroup group = new ButtonGroup();
        group.add(cloud);
        group.add(mean);
        group.setMaxCheckCount(1);
        group.setUncheckLast(true);
        
        mean.setChecked(true);

        Table table = new Table();
        table.debug();
        table.setFillParent(true);
        stage.addActor(table);
		
        table.columnDefaults(1).width(200);
        table.columnDefaults(2).width(200);
        table.columnDefaults(3).width(50);
        table.add(new Label("INITIALISATION PARAMETERS", skin)).colspan(3);
        
        // POSITION STD
        table.row().spaceTop(10);
        table.add(new Label("Position std :", skin));
        table.add(posStdSlider);
        table.add(posStdValue);
        
        // HEADING STD
        table.row().spaceTop(10);
        table.add(new Label("Heading std :", skin));
        table.add(headingStdSlider);
        table.add(headingStdValue);
        
        // PARTICLES NUMBER
        table.row().spaceTop(10);
        table.add(new Label("Number of particles :", skin));
        table.add(numParticlesSlider);
        table.add(numParticlesValue);
        
        table.row().spaceTop(30);
        table.add(new Label("COMPUTING PARAMETERS", skin)).colspan(3);
        
        // STEP LENGTH NOISE STD
        table.row().spaceTop(10);
        table.add(new Label("Step length noise std :", skin));
        table.add(stepLengthStdSlider);
        table.add(stepLengthStdValue);
        table.row().space(30);
        
        // STEP COURSE OVER GROUND NOISE STD
        table.row().spaceTop(10);
        table.add(new Label("Step COG noise std :", skin));
        table.add(stepCOGStdSlider);
        table.add(stepCOGStdValue);
  
        table.row().spaceTop(30);
        table.add(new Label("DISPLAY PARAMETERS", skin)).colspan(3);
        
        // PARTICLES D		ISPLAY
        table.row().spaceTop(10);
        table.add(new Label("Particles display :", skin));
        table.add(mean);
        table.add(cloud);
        
        table.row().space(30);
        TextButton button = new TextButton("Apply", skin);
		button.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				writeToParamers();
				changeScreen();			
				return false;
			}
		});
		table.add(button).colspan(3);
	}
	
	private void changeScreen(){
		game.setScreen(game.mainMenuScreen);
	}
	
	private void writeToParamers() {
		ParticleFilterParameters.positionStd = posStdSlider.getValue();
		ParticleFilterParameters.headingStd = headingStdSlider.getValue();
		ParticleFilterParameters.particlesNumber = (int)numParticlesSlider.getValue();
		ParticleFilterParameters.stepLengthNoiseStd = stepLengthStdSlider.getValue();
		ParticleFilterParameters.stepCOGNoiseStd = stepCOGStdSlider.getValue();
		if(mean.isChecked()){
			ParticleFilterParameters.displayCloudAsMean = true;
		} else {
			ParticleFilterParameters.displayCloudAsMean = false;
		}
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        if(Gdx.input.isKeyPressed(Input.Keys.BACK)) {
        	changeScreen();
        } else {
            stage.act(Gdx.graphics.getDeltaTime());
            stage.draw();
        }
        //Table.drawDebug(stage); // This is optional, but enables debug lines for tables.
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height, true);
		
		game.WIDTH = width;
		game.HEIGHT = height;
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {
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
		stage.dispose();
	}
}
