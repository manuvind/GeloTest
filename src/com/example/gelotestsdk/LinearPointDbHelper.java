package com.example.gelotestsdk;

import com.example.gelotestsdk.LinearContract.LinearPoint;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LinearPointDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "LinearPoint.db";
        private static final String INTEGER_TYPE = " INTEGER";
        private static final String COMMA_SEP = ",";
        private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + LinearPoint.TABLE_NAME + " (" +
            LinearPoint._ID + " INTEGER PRIMARY KEY," +
            LinearPoint.COLUMN_NAME_POINT_ID + INTEGER_TYPE + COMMA_SEP +
            LinearPoint.COLUMN_NAME_DISTANCE + INTEGER_TYPE + COMMA_SEP +
            LinearPoint.COLUMN_NAME_RSSI + INTEGER_TYPE +
            " )";

        private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + LinearPoint.TABLE_NAME;
        

        public LinearPointDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
}
