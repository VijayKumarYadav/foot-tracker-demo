package com.horizon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jsqlite.Stmt;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TableListActivity extends ListActivity {
	private static final String TAG = "TableListActivity";
	TableListAdapter listAdapter = null;
	boolean isBuildingSet = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tablelist);

		Database db = new Database(this.getApplicationContext());
		
		listAdapter = new TableListAdapter(this, R.layout.tablelist_row,
				new ArrayList<TableInfo>());
		setListAdapter(listAdapter);
		fillList();
	}
	
   @Override
   protected void onListItemClick(ListView l, View v, int position, long id) {
       super.onListItemClick(l, v, position, id);
       TableInfo obj = (TableInfo) getListView().getItemAtPosition(position);
       if(!isBuildingSet){
           ParticleFilterParameters.buildingId = obj.getBuildingId();
           listAdapter = new TableListAdapter(this, R.layout.tablelist_row,
   				new ArrayList<TableInfo>());
           setListAdapter(listAdapter);
           getFloors();
           isBuildingSet = true;
       } else {
           ParticleFilterParameters.floor = obj.getBuildingId();
           Intent intent = new Intent(TableListActivity.this, Test.class);
           startActivity(intent);      
       }
   }

	private void fillList() {
		try {
			String dbFile = SpatialiteActivityHelper.getDataBase(this,
					getString(R.string.db_name));
			if (dbFile == null) {
				throw new IOException("ERROR");
			}

			jsqlite.Database db = new jsqlite.Database();
			db.open(dbFile.toString(), jsqlite.Constants.SQLITE_OPEN_READONLY);

			Stmt stmt = db
					.prepare("SELECT id, name FROM buildings;");
			while (stmt.step()) {
				int buildingId = stmt.column_int(0);
				String buildingName = stmt.column_string(1);
				listAdapter.add(new TableInfo(buildingName, buildingId));
			}
			db.close();
		} catch (jsqlite.Exception e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}
	
	private void getFloors(){
		try {
			String dbFile = SpatialiteActivityHelper.getDataBase(this,
					getString(R.string.db_name));
			if (dbFile == null) {
				throw new IOException("ERROR");
			}

			jsqlite.Database db = new jsqlite.Database();
			db.open(dbFile.toString(), jsqlite.Constants.SQLITE_OPEN_READONLY);

			Stmt stmt = db
					.prepare("SELECT distinct(floor_lvl) FROM rooms WHERE building_id = " + ParticleFilterParameters.buildingId + ";");
			System.out.println("SELECT distinct(floor_lvl) FROM rooms WHERE building_id = " + ParticleFilterParameters.buildingId + ";");
			while (stmt.step()) {
				int floorLvl = stmt.column_int(0);
				listAdapter.add(new TableInfo("Floor " + floorLvl, floorLvl));
			}
			db.close();
		} catch (jsqlite.Exception e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	private class TableInfo {
		private String buildingName;
		private int buildingId;

		TableInfo(String tableName, int buildingId) {
			this.buildingName = tableName;
			this.buildingId = buildingId;
		}

		public String getTableName() {
			return buildingName;
		}
		
		public int getBuildingId() {
			return buildingId;
		}
	}

	private class TableListAdapter extends ArrayAdapter<TableInfo> {
		List<TableInfo> objects;

		public TableListAdapter(Context context, int layoutResourceId,
				List<TableInfo> objects) {
			super(context, layoutResourceId, objects);
			this.objects = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = null;

			if (null == convertView) {
				v = getLayoutInflater().inflate(R.layout.tablelist_row, null);
			} else {
				v = convertView;
			}

			TextView name = (TextView) v.findViewById(R.id.table_name);
			name.setText(objects.get(position).getTableName());

			return v;
		}
	}
}