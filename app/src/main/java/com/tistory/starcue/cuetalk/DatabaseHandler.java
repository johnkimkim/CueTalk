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

    private static final String TABLE_NAME = "id";
    private static final String UNIQUE_TABLE_NAME = "uniqueTable";

    public static final String CNAME = "name";
    public static final String CSEX = "sex";
    public static final String CAGE = "age";
    public static final String UNIQUE = "uniqueField";

    private static final String DATABASE_CREATE_TEAM = "create table if not exists " + TABLE_NAME + "(" + CNAME + " TEXT," + CSEX + " TEXT," + CAGE + "TEXT" + ")";
    private static final String UNIQUE_CREATE_TEAM = "create table if not exists " + UNIQUE_TABLE_NAME + "(" + UNIQUE + "TEXT" + ")";

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
        db.execSQL("DROP TABLE " + TABLE_NAME);
        db.execSQL(DATABASE_CREATE_TEAM);
        db.execSQL(UNIQUE_CREATE_TEAM);
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

    public void dbinsert(String name, String sex, String age) {
        sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CNAME, name);
        contentValues.put(CSEX, sex);
        contentValues.put(CAGE, age);
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    public void dbdelete() {
        sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from id");
        sqLiteDatabase.execSQL("vacuum");
        sqLiteDatabase.close();
    }

    public void insertUnique(String unique) {
        sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UNIQUE, unique);
        sqLiteDatabase.insert(UNIQUE_TABLE_NAME, null, contentValues);
    }

}
