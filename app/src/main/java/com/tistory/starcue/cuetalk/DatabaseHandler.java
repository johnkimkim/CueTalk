package com.tistory.starcue.cuetalk;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DatabaseHandler extends SQLiteOpenHelper {

    private Context mContext;
    private SQLiteDatabase sqLiteDatabase;

    private static final int DATABASE_VERSTION = 1;

    public static final String DATABASE_NAME = "id.sqlite";
    public static final String DBLOCATION = "/data/data/com.tistory.starcue.cuetalk/databases/";

    private static final String UNIQUE_TABLE_NAME = "uniqueTable";
    private static final String ADRESS_TABLE_NAME = "adress";
    private static final String WHERE_TABLE_NAME = "whereTable";
    private static final String NAME_TABLE_NAME = "nameTable";

    public static final String UNIQUE = "uniqueField";
    public static final String ADRESS = "adressField";
    public static final String WHERE = "whereField";
    public static final String NAME = "nameField";

    private static final String UNIQUE_CREATE_TEAM = "create table if not exists " + UNIQUE_TABLE_NAME + "(" + UNIQUE + "TEXT" + ")";
    private static final String ADRESS_CREATE_TEAM = "create table if not exists " + ADRESS_TABLE_NAME + "(" + ADRESS + "TEXT" + ")";
    private static final String WHERE_CREATE_TEAM = "create table if not exists " + WHERE_TABLE_NAME + "(" + WHERE + "TEXT" + ")";
    private static final String NAME_CREATE_TEAM = "create table if not exists " + NAME_TABLE_NAME + "(" + NAME + "TEXT" + ")";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSTION);
        this.mContext = context;
    }

    public static void setDB(Context context) {
        File folder = new File(DBLOCATION);
        if (folder.exists()) {
        } else {
            folder.mkdirs();
        }
        AssetManager assetManager = context.getResources().getAssets();
        File outfile = new File(DBLOCATION + DATABASE_NAME);
        InputStream is = null;
        FileOutputStream fo = null;
        long filesize = 0;
        try {
            is = assetManager.open(DATABASE_NAME, AssetManager.ACCESS_BUFFER);
            filesize = is.available();
            if (outfile.length() <= 0) {
                byte[] tempdata = new byte[(int) filesize];
                is.read(tempdata);
                is.close();
                outfile.createNewFile();
                fo = new FileOutputStream(outfile);
                fo.write(tempdata);
                fo.close();
            } else {
            }
        } catch (IOException e) {}
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE " + UNIQUE_TABLE_NAME);
        db.execSQL("DROP TABLE " + ADRESS_TABLE_NAME);
        db.execSQL("DROP TABLE " + WHERE_TABLE_NAME);
        db.execSQL("DROP TABLE " + NAME_TABLE_NAME);
        db.execSQL(UNIQUE_CREATE_TEAM);
        db.execSQL(ADRESS_CREATE_TEAM);
        db.execSQL(WHERE_CREATE_TEAM);
        db.execSQL(NAME_CREATE_TEAM);
    }

    public void openDatabase() {
        String dbPath = DBLOCATION + DATABASE_NAME;
        if (sqLiteDatabase != null && sqLiteDatabase.isOpen()) {
            return;
        }
        sqLiteDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public void closeDatabase() {
        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
        }
    }

    public void adressinsert(String adress) {
        sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ADRESS, adress);
        sqLiteDatabase.insert(ADRESS_TABLE_NAME, null, contentValues);
    }
    public void adressdelete() {
        sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from adress");
    }

    public void uniquedelete() {
        sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("vacuum");
        sqLiteDatabase.close();
    }

    public void insertUnique(String unique) {
        sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UNIQUE, unique);
        sqLiteDatabase.insert(UNIQUE_TABLE_NAME, null, contentValues);
    }

    public void changeUnique(String unique) {
        sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from uniqueTable");
        ContentValues contentValues = new ContentValues();
        contentValues.put(UNIQUE, unique);
        sqLiteDatabase.insert(UNIQUE_TABLE_NAME, null, contentValues);
    }

    public void insertWhere(String where) {
        sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WHERE, where);
        sqLiteDatabase.insert(WHERE_TABLE_NAME, null, contentValues);
    }

    public void deleteWhere() {
        sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from whereTable");
        sqLiteDatabase.execSQL("vacuum");
    }

    public void insertName(String name) {
        sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        sqLiteDatabase.insert(NAME_TABLE_NAME, null, contentValues);
    }

    public void changeName(String name) {
        sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from nameTable");
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        sqLiteDatabase.insert(NAME_TABLE_NAME, null, contentValues);
    }

    public void deleteName() {
        sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from nameTable");
        sqLiteDatabase.execSQL("vacuum");
    }

}
