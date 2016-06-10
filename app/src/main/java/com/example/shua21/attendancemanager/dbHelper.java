package com.example.shua21.attendancemanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class dbHelper extends SQLiteOpenHelper {
    //public static final String ProgramName = "AttM";
    private static final String DATABASE_NAME = "AttM.db";
    private static final int DATABASE_VERSION =10;
    public SQLiteDatabase db1;
    public dbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        try {
            db1 = getWritableDatabase();
        } catch (SQLiteException ex) {
            db1 = getReadableDatabase();
        }
    }


    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ClassList (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "classname TEXT,img int);");
        db.execSQL("CREATE TABLE StudentList ( id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "classid INTEGER,name TEXT, phone char(20),stuid INTEGER,FOREIGN KEY(classid) REFERENCES ClassList(id));");
        try {
            db.execSQL("CREATE TABLE AttendLog (id INTEGER primary key AUTOINCREMENT,classid INTEGER,student INTEGER,nday DATE,ntime INTEGER,status int);");
        }catch(Exception e)
        {
            Log.i("err",e.toString());
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS StudentList");
        db.execSQL("DROP TABLE IF EXISTS ClassList");
        db.execSQL("DROP TABLE IF EXISTS AttendLog");
                onCreate(db);
    }

}

