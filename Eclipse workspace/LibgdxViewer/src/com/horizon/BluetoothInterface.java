package com.horizon;

public interface BluetoothInterface {
	
    public boolean isConnected();
    
    public void connect();
    
    public void sendMessage(String message);
    
}
