package com.horizon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsqlite.Stmt;

import android.content.Context;
import android.util.Log;

public class Database {
	
	private Context ctx;
	private jsqlite.Database db = new jsqlite.Database();
	
	public Database(Context ctx){
		this.ctx = ctx;
		
		String dbFile = SpatialiteActivityHelper.getDataBase(ctx,
				ctx.getString(R.string.db_name));
		
		if (dbFile == null) {
			this.installToApplication();
			dbFile = SpatialiteActivityHelper.getDataBase(ctx,
					ctx.getString(R.string.db_name));
		}

		try {
			db.open(dbFile.toString(), jsqlite.Constants.SQLITE_OPEN_READONLY);
		} catch (Exception e) {
			Log.e("TOTO", e.toString());
			e.printStackTrace();
		}
	}

	private void installToApplication(){
		try {
			SpatialiteAssetHelper.CopyAsset(ctx,
					SpatialiteActivityHelper.getPath(ctx, false),
					ctx.getString(R.string.db_name));
		} catch (IOException e) {
			Log.d("TOTO", e.toString());
		}
	}
	
	public List<Room> getRooms(int buildingId, int floorLevel){
		Stmt stmt;
		Pattern p = Pattern.compile("\\(\\((.*?)\\)");
		
		List<Room> rooms = new ArrayList<Room>();
		
		try {
			stmt = db.prepare("SELECT AsText(geometry) FROM rooms WHERE building_id = " + Integer.toString(buildingId) + " AND floor_lvl = " + floorLevel +";");
			while (stmt.step()) {
				String line = stmt.column_string(0);				
				Matcher m = p.matcher(line);
				while (m.find()) {
					Room room = new Room();
					String[] points = m.group(1).split("[,]");
					for(int i=0; i<points.length; i++){
						String[] coordinates = points[i].trim().split("[ ]");
						double lat = Double.parseDouble(coordinates[1]);
						double lon = Double.parseDouble(coordinates[0]);
						room.points.add(new Point(lat, lon, 0));
					}
					rooms.add(room);
				}
			}
		} catch (Exception e) {
			Log.e("TOTO", e.toString());
			e.printStackTrace();
		}
		return rooms;
	}
	
	public void setProjectionParametersToBuildingCentroid(int buildingId){
		Stmt stmt;
		Pattern p = Pattern.compile("\\(\\((.*?)\\)");
		
		try {
			stmt = db.prepare("SELECT AsText(Envelope(footprint)) FROM buildings WHERE id = " + buildingId + ";");
			
			stmt.step();
			String line = stmt.column_string(0);				
			Matcher m = p.matcher(line);
			while (m.find()) {
				String point = m.group(1).split("[,]")[0];

				String[] coordinates = point.trim().split("[ ]");
				double lat = Double.parseDouble(coordinates[1]);
				double lon = Double.parseDouble(coordinates[0]);
				
				ParticleFilterParameters.projection_latitude = lat;
				ParticleFilterParameters.projection_longitude = lon;
			}
		} catch (Exception e) {
			Log.e("TOTO", e.toString());
			e.printStackTrace();
		}
	}
	
}
