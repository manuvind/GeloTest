package com.example.gelotestsdk;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gelotestsdk.LinearContract.LinearPoint;
import com.example.gelotestsdk.LinearPointDbHelper;
import com.gelo.gelosdk.GeLoBeaconManager;
import com.gelo.gelosdk.Model.Beacons.GeLoBeacon;
import org.apache.commons.math3.stat.regression.*;


public class MainActivity extends Activity {
	
	GeLoBeaconManager ml;
	ArrayList<GeLoBeacon> beacons;
	LinearPointDbHelper pDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ml = GeLoBeaconManager.sharedInstance(getApplicationContext());
        ml.startScanningForBeacons();
        pDbHelper = new LinearPointDbHelper(this);
        Timer timer = new Timer();
		timer.scheduleAtFixedRate(new UpdateBeacon(), 0, 1*400);
        
    }
    
    public void delete(View view) {
    	SQLiteDatabase db = pDbHelper.getReadableDatabase();
    	// Define 'where' part of query.
    	String selection = LinearPoint.COLUMN_NAME_POINT_ID + " LIKE ?";
    	// Specify arguments in placeholder order.
    	String[] selectionArgs = { String.valueOf(1) };
    	// Issue SQL statement.
    	db.delete(LinearPoint.TABLE_NAME, selection, selectionArgs);
    }
    
    public void viewData(View view) {
    	SQLiteDatabase db = pDbHelper.getReadableDatabase();

    	// Define a projection that specifies which columns from the database
    	// you will actually use after this query.
    	String[] projection = {
    	    LinearPoint._ID,
    	    LinearPoint.COLUMN_NAME_DISTANCE,
    	    LinearPoint.COLUMN_NAME_RSSI
    	    };

    	// How you want the results sorted in the resulting Cursor
//    	String sortOrder =
//    	    LinearPoint.COLUMN_NAME_UPDATED + " DESC";

    	Cursor c = db.query(
    	    LinearPoint.TABLE_NAME,  // The table to query
    	    projection,                               // The columns to return
    	    null,                                // The columns for the WHERE clause
    	    null,                            // The values for the WHERE clause
    	    null,                                     // don't group the rows
    	    null,                                     // don't filter by row groups
    	    null                                 // The sort order
    	    );
    	c.moveToFirst();
    	c.moveToNext();
    	//c.moveToNext();
    	long itemId = c.getLong(
    			c.getColumnIndexOrThrow(LinearPoint.COLUMN_NAME_RSSI));
    	TextView rssiView = (TextView)findViewById(R.id.valueId);
    	rssiView.setText(Long.toString(itemId));
    			
    }
    
    public void calculate(View view) {
    	SimpleRegression plot = new SimpleRegression();
    	SQLiteDatabase db = pDbHelper.getReadableDatabase();
    	String[] projection = {
        	    LinearPoint._ID,
        	    LinearPoint.COLUMN_NAME_DISTANCE,
        	    LinearPoint.COLUMN_NAME_RSSI
        	    };
    	Cursor c = db.query(
        	    LinearPoint.TABLE_NAME,  // The table to query
        	    projection,                               // The columns to return
        	    null,                                // The columns for the WHERE clause
        	    null,                            // The values for the WHERE clause
        	    null,                                     // don't group the rows
        	    null,                                     // don't filter by row groups
        	    null                                 // The sort order
        	    );
        boolean inBound = c.moveToFirst();
        while(inBound == true) {
        	double v_x = (double) c.getLong(
        			c.getColumnIndexOrThrow(LinearPoint.COLUMN_NAME_DISTANCE));
        	double v_y = (double) c.getLong(
        			c.getColumnIndexOrThrow(LinearPoint.COLUMN_NAME_RSSI));
        	plot.addData(v_x, v_y);
        	inBound = c.moveToNext();	
        }
        
    	double intercept = plot.getIntercept();
    	double slope = plot.getSlope();
    	double r2 = plot.getRSquare();
    	String slopeIntercept = "Inter: " + Double.toString(intercept) + " Slope: " + Double.toString(slope) + " R2: " + Double.toString(r2);
    	TextView rssiView = (TextView)findViewById(R.id.slopeIntercept);
    	rssiView.setText(slopeIntercept);
    	
    }
    
    public void sendSave(View view) {
    	//ml = GeLoBeaconManager.sharedInstance(getApplicationContext());
    	//ml.startScanningForBeacons();
    	// Gets the data repository in write mode
    	SQLiteDatabase db = pDbHelper.getWritableDatabase();
    	
    	EditText editText = (EditText) findViewById(R.id.distance);
    	Integer dist = Integer.parseInt(editText.getText().toString());
    	
    	TextView rssiView = (TextView)findViewById(R.id.rssiLabel);
    	Integer rssi = Integer.parseInt(rssiView.getText().toString());

    	// Create a new map of values, where column names are the keys
    	ContentValues values = new ContentValues();
    	values.put(LinearPoint.COLUMN_NAME_POINT_ID, 1);
    	values.put(LinearPoint.COLUMN_NAME_DISTANCE, dist);
    	values.put(LinearPoint.COLUMN_NAME_RSSI, rssi);

    	// Insert the new row, returning the primary key value of the new row
    	long newRowId;
    	newRowId = db.insert(
    	         LinearPoint.TABLE_NAME,
    	         null,
    	         values);
//    	int rssi = 0;
//    	if (beacons.isEmpty() != true) {
//    		for (GeLoBeacon i : beacons) {
//    			if (i.getBeaconId() == 107) {
//    				rssi = i.getSignalStregth();
//    			}
//    		}
//    	}
//    	TextView rssiView = (TextView)findViewById(R.id.rssiLabel);
//    	rssiView.setText(Integer.toString(rssi));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    class UpdateBeacon extends TimerTask {
	    @Override
	    public  void run() {
	        MainActivity.this.runOnUiThread(new Runnable() {
	            @Override
	            public void run() {
	            	beacons = ml.getKnownBeacons();
	            	int rssi = 0;
	            	if (beacons.isEmpty() != true) {
	            		for (GeLoBeacon i : beacons) {
	            			if (i.getBeaconId() == 107) {
	            				rssi = i.getSignalStregth();
	            			}
	            		}
	            	}
	            	TextView rssiView = (TextView)findViewById(R.id.rssiLabel);
	            	rssiView.setText(Integer.toString(rssi));
	            }
	        });
	    }
	}
}
