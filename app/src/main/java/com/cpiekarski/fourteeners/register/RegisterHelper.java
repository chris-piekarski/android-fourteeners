package com.cpiekarski.fourteeners.register;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RegisterHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "summit_register";
    public static final String COLUMN_ID = "_id";
    public static final String TABLE_NAME = "register";
    public static final String MNT_NAME = "mnt_name";
    public static final String MNT_RANGE = "mnt_range";
    public static final String START_TIME = "start_time";
    public static final String END_TIME = "end_time";
    public static final String START_ELEVATION = "start_elevation";
    public static final String END_ELEVATION = "end_elevation";
    public static final String PEEK_ELEVATION = "peek_elevation";
    public static final String DISTANCE = "distance";
    public static final String START_LOC = "start_location";
    public static final String END_LOC = "end_location";
    public static final String NOTES = "notes";
    public static final String PROOF = "proof";
    public static final String SUMMIT = "summit";
    
    private static final String TABLE_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MNT_NAME + " TEXT, " +
                        MNT_RANGE + " TEXT, "+
                        START_TIME + " TEXT, " +
                        END_TIME + " TEXT, " +
                        START_ELEVATION + " INTEGER, " +
                        END_ELEVATION + " INTEGER, " +
                        PEEK_ELEVATION + " INTEGER, " +
                        START_LOC + " TEXT, " +
                        END_LOC + " TEXT, " +
                        DISTANCE + " TEXT, " +
                        SUMMIT + " INTEGER, " +
                        NOTES + " TEXT, " +
                        PROOF + " TEXT);";

    public RegisterHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
        
    }
}