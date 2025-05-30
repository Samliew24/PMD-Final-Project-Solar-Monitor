package com.example.solarmonitorapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SolarMonitor.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USER = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";

    private static final String TABLE_ENERGY = "energy_data";
    private static final String COL_ENERGY_ID = "id";
    private static final String COL_DATE = "date";
    private static final String COL_ENERGY = "energy";
    private static final String COL_EFFICIENCY = "efficiency";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USER + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT, " +
                COL_PASSWORD + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_ENERGY + " (" +
                COL_ENERGY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DATE + " TEXT, " +
                COL_ENERGY + " REAL, " +
                COL_EFFICIENCY + " REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENERGY);
        onCreate(db);
    }

    // ---------- USER AUTH ----------
    public boolean insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        long result = db.insert(TABLE_USER, null, values);
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER +
                        " WHERE " + COL_USERNAME + "=? AND " + COL_PASSWORD + "=?",
                new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // ---------- ENERGY DATA CRUD ----------
    public boolean insertEnergy(String date, float energy, float efficiency) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DATE, date);
        values.put(COL_ENERGY, energy);
        values.put(COL_EFFICIENCY, efficiency);
        long result = db.insert(TABLE_ENERGY, null, values);
        return result != -1;
    }

    public Cursor getAllEnergyData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ENERGY + " ORDER BY " + COL_DATE + " ASC", null);
    }

    public Cursor getEnergyById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ENERGY + " WHERE " + COL_ENERGY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public Cursor getLatestEnergyData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ENERGY + " ORDER BY " + COL_DATE + " DESC LIMIT 1", null);
    }

    public Cursor getTotalEnergy() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT SUM(" + COL_ENERGY + ") FROM " + TABLE_ENERGY, null);
    }

    public Cursor getPeakPower() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT MAX(" + COL_ENERGY + ") FROM " + TABLE_ENERGY, null);
    }

    public Cursor getPeakHour() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT " + COL_DATE + " FROM " + TABLE_ENERGY + " WHERE " + COL_ENERGY +
                " = (SELECT MAX(" + COL_ENERGY + ") FROM " + TABLE_ENERGY + ")", null);
    }

    public boolean updateEnergy(int id, String date, float energy, float efficiency) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DATE, date);
        values.put(COL_ENERGY, energy);
        values.put(COL_EFFICIENCY, efficiency);
        int result = db.update(TABLE_ENERGY, values, COL_ENERGY_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    public boolean deleteEnergy(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_ENERGY, COL_ENERGY_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    // ---------- DATE RANGE FILTER ----------
    public Cursor getEnergyDataByDateRange(String startDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ENERGY + " WHERE date >= ? ORDER BY date ASC",
                new String[]{startDate});
    }
}
