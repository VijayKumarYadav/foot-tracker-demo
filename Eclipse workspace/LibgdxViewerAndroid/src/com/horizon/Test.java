package com.horizon;

import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.horizon.BluetoothService;
import com.horizon.Utils;

public class Test extends AndroidApplication implements BluetoothInterface {
	
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothService bts;
    
    private OpeningInDoorsGame game;
    
    CoordinateTransformation transformer;
    
    private boolean			bluetoothConnected = false;
	
    public void onCreate (android.os.Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            Database db = new Database(this.getApplicationContext());
            db.setProjectionParametersToBuildingCentroid(ParticleFilterParameters.buildingId);
            
        	transformer = new CoordinateTransformation();
        	transformer.init(ParticleFilterParameters.projection_longitude, ParticleFilterParameters.projection_latitude);
        	Point.transformer = transformer;
        	        	
            game = new OpeningInDoorsGame(this);
            initialize(game, false);

        	List<Room> rooms = db.getRooms(ParticleFilterParameters.buildingId, ParticleFilterParameters.floor);
        	for(int i=0; i<rooms.size(); i++){
        		Room room = rooms.get(i);
            	Mesh mesh = new Mesh(true, room.points.size(), 0,
                        new VertexAttribute(Usage.Position, 3, "a_position"),
                        new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
            	
            	mesh.setVertices(room.getVertices());
            	game.polygonsMesh.add(mesh);
        	}
    }
    
    @Override
    public synchronized void onPause() {
        super.onPause();
        Log.e("TOTO", "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("TOTO", "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (bts != null) bts.stop();
        Log.e("TOTO", "--- ON DESTROY ---");
    }
    
    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
            	if(msg.arg1 == 1){
            		if (bts != null) bts.stop();
            	}
            	if(msg.arg1 == 3){
            		bluetoothConnected = true;
            	}
                Log.i("TOTO", "MESSAGE_STATE_CHANGE: " + msg.arg1);
                break;
            case MESSAGE_READ:
            	Log.d("TOTO", (String) msg.obj);
            	Point position = decryptMessage((String) msg.obj);
            	if(position != null){
            		//view.updatePosition(position);
            	} else {
            		game.getParticlesBluetooth((String) msg.obj);
            		
            	}
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
    
    private Point decryptMessage(String msg){
        if(Utils.isValidNMEA(msg)){
        	Point pos = Utils.extractLocation(msg);
        	return pos;
        }
        return null; 
    }
      
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
    	String tmp;
        switch (item.getItemId()) {
            case R.id.init_position:
        		tmp = "initial coordinate = GEO 52.0 -1.1 0.0\ninitial attitude = 0.0 0.0 180\nreset\n";
        		bts.write(tmp.getBytes());
                return true;
            case R.id.start_particle_filter:
            	//view.startInitialisation();
                
        		/*tmp = "initPoint = " + Float.toString(initController.startPt.x) + " " + Float.toString(initController.startPt.y) + ";\n";
        		bts.write(tmp.getBytes());
        		
        		tmp = "heading = " + Float.toString(initController.heading) + ";\n";
        		bts.write(tmp.getBytes());
                
        		tmp = "start filter;\n";
        		bts.write(tmp.getBytes());
            	Gdx.app.postRunnable(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						view.startInitialisation();
					}
				});*/
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return bluetoothConnected;
	}

	@Override
	public void connect() {
		Test.this.runOnUiThread(new Runnable() {
		    public void run() {
		    	// Get local Bluetooth adapter
		        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		    	bts = new BluetoothService(getApplicationContext(), mHandler);
		    	BluetoothDevice device = mBluetoothAdapter.getRemoteDevice("00:12:6F:21:51:24");
		    	bts.connect(device);
		    }
		});
	}

	@Override
	public void sendMessage(String message) {
		if(isConnected()){
			bts.write(message.getBytes());	
		}
	}
}
