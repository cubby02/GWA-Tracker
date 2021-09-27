package com.example.gwatracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, "tracker_GWA", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String create_tbl = "CREATE TABLE tblGrades(ID INTEGER PRIMARY KEY AUTOINCREMENT, syTerm TEXT, yearFrom INT,subject TEXT, grades REAL, units REAL)";
        sqLiteDatabase.execSQL(create_tbl);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS tblGrades");
        onCreate(sqLiteDatabase);
    }

    public boolean addSucject(String syTerm, int yearFrom, String sub, double grades, double units){
        db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("syTerm", syTerm);
        contentValues.put("yearFrom", yearFrom);
        contentValues.put("subject", sub);
        contentValues.put("grades", grades);
        contentValues.put("units", units);

        long insert = db.insert("tblGrades", null, contentValues);

        if (insert == -1){
            return false;
        } else {
            return true;
        }
    }

    public Cursor getSYTerm(){
        db = this.getReadableDatabase();
        String query = "SELECT syTerm FROM tblGrades GROUP BY syTerm ORDER BY syTerm DESC";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public String getLastRow(){
        db = this.getReadableDatabase();
        String query = "SELECT syTerm FROM tblGrades GROUP BY syTerm ORDER BY syTerm DESC LIMIT 1";

        Cursor data = db.rawQuery(query, null);
        String lastRow = "";
        while (data.moveToNext()){
            lastRow = data.getString(0);
        }

        return lastRow;
    }

    public List<Subject> getSubjects(String term){
        db = this.getReadableDatabase();
        List<Subject> list = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT subject, grades, units FROM tblGrades WHERE syTerm='" + term +"' ORDER BY ID DESC", null);

        if (c.moveToFirst()){
            do{
                Subject s = new Subject();

                s.setSub(c.getString(c.getColumnIndex("subject")));
                s.setGrades(c.getDouble(c.getColumnIndex("grades")));
                s.setUnits(c.getDouble(c.getColumnIndex("units")));

                list.add(s);
            } while (c.moveToNext());
        }

        return list;
    }

    public double overallUnits(){
        db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(units) AS Total FROM tblGrades", null);
        double units = 0;

        if(c.moveToFirst()){
            units = c.getDouble(c.getColumnIndex("Total"));
        }

        return units;
    }

    public double termUnits(String term){
        db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(units) AS Total FROM tblGrades WHERE syTerm='"+ term +"'", null);
        double units = 0;

        if(c.moveToFirst()){
            units = c.getDouble(c.getColumnIndex("Total"));
        }

        return units;
    }

    public int getRowCount(){
        db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM tblGrades", null);
        int count = c.getCount();
        return count;
    }

    public double getAccumulatedGWA(){
        db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT (SUM(units * grades)/SUM(units)) AS GWA FROM tblGrades", null);
        double gwa = 0;

        if(c.moveToFirst()){
            gwa = c.getDouble(c.getColumnIndex("GWA"));
        }

        return gwa;
    }

    public double getTermGwa(String term){
        db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT (SUM(units * grades)/SUM(units)) AS GWA FROM tblGrades WHERE syTerm='"+ term +"'", null);
        double gwa = 0;

        if(c.moveToFirst()){
            gwa = c.getDouble(c.getColumnIndex("GWA"));
        }

        return gwa;
    }

    public List<String> showYear(){
        db = this.getReadableDatabase();
        List<String> items = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT * FROM tblGrades GROUP BY yearFrom", null);
        if (c.moveToFirst()){
            do {
                items.add(c.getString(c.getColumnIndex("yearFrom")));
            } while (c.moveToNext());
        }

        return items;
    }
}
