package com.horizon;

import com.badlogic.gdx.math.Rectangle;

public class Utils {

	public static String calculateChecksum (String nmea) {
		char ch ;
		int checksum = 0;
		
		for ( int i = 0; i < nmea . length () ; i ++) {
			ch = nmea . charAt ( i );
			if ( ch == '$') {
				continue;
			} else if ( ch == '*') {
				break;
			} else if ( checksum == 0) {
				checksum = ( byte ) ch;
			} else {
				checksum ^= ( byte ) ch;
			}
		}
		return String.format("%02X", checksum);
	}
	
	public static Boolean isValidNMEA(String nmea){
		String[] str ;
		String checksum;
		
		str = nmea.split("[\\*]");
		
		try {
			checksum = str[1];
		} catch (Exception e) {
			// incomplete NMEA message
			//Log.d("TOTO", "NMEA message is invalid");
			return false;
		}
		
		if(calculateChecksum(nmea).equals(checksum)){
			return true;
		} else {
			//Log.d("TOTO", "NMEA message is invalid - BAD CHECKSUM");
			return false;
		}
	}
	
	public static Point extractLocation(String nmea){
		String[] str;
		Point pos = null;
		
		str = nmea.split("[,]");

		if(str[0].equals("$PNGIFOOT")){
			pos = new Point(Float.parseFloat(str[2]), Float.parseFloat(str[3]), Float.parseFloat(str[4]));
		} else {
			System.out.print("NMEA message is not a position");
		}
		
		return pos;
	}
	
	public static boolean pointInRectangle (Rectangle r, float x, float y) {
		return r.x <= x && r.x + r.width >= x && r.y <= y && r.y + r.height >= y;
	}
}
