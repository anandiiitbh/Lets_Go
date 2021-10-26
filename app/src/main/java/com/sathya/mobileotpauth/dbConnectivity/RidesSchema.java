package com.sathya.mobileotpauth.dbConnectivity;

import android.provider.BaseColumns;

public final class RidesSchema {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private RidesSchema() {}

    /* Inner class that defines the table contents */
    public static class PastRides implements BaseColumns {
        public static final String TABLE_NAME = "RideRecords";

        //columns
        public static final String COLUMN_NAME_FROM = "from_loc";
        public static final String COLUMN_NAME_TO = "to_loc";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_TYPE = "type_of_veh";
        public static final String COLUMN_NAME_DATE = "date_of_ride";
        public static final String COLUMN_NAME_DRIVER = "driver";

        //create statement
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_FROM + " TEXT," +
                        COLUMN_NAME_TO + " TEXT," +
                        COLUMN_NAME_PRICE + " TEXT," +
                        COLUMN_NAME_TYPE + " TEXT," +
                        COLUMN_NAME_DATE + " TEXT," +
                        COLUMN_NAME_DRIVER + " TEXT)";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static class BookMark implements BaseColumns {
        public static final String TABLE_NAME = "Bookmark";

        //columns
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_LOCATION= "LocationName";

        //create statement
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME + " TEXT," +
                        COLUMN_LOCATION + " TEXT)";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
