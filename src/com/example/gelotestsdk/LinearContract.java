package com.example.gelotestsdk;

import android.provider.BaseColumns;

public final class LinearContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public LinearContract() {}

    /* Inner class that defines the table contents */
    public static abstract class LinearPoint implements BaseColumns {
        public static final String TABLE_NAME = "point";
        public static final String COLUMN_NAME_POINT_ID = "pointid";
        public static final String COLUMN_NAME_DISTANCE = "distance";
        public static final String COLUMN_NAME_RSSI = "rssi";
    }
}