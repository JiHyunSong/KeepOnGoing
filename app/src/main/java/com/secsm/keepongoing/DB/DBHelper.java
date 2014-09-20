package com.secsm.keepongoing.DB;

/**
 * Created by JinS on 2014. 8. 10..
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.com.lanace.connecter.HttpAPIs;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

//DB 컨트롤(관리) 하는 객체
public class DBHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = "DBHelper";
    // Contacts table name
    private static final String TABLE_IMAGE = "Image_table";

    public DBHelper(Context context) {
        super(context, "KogDB.db", null, 1);
        /* (context,dbname,null,dbversion) */

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String qCreate_Friend_l = "CREATE TABLE Friend_l (" +
                "fid_l INTEGER, " +
                "nickname_fl VARCHAR(15), " +
                "profile VARCHAR(50)" +
                ");";
        db.execSQL(qCreate_Friend_l);

        String qCreate_LifeRoom_l = "CREATE TABLE LifeRoom_l (" +
                "rid INTEGER, " +
                "name VARCHAR(100), " +
                "max_holiday_count_l INTEGER" +
                ");";
        db.execSQL(qCreate_LifeRoom_l);

        String qCreate_SubjectRoom_l = "CREATE TABLE SubjectRoom_l (" +
                "rid INTEGER, " +
                "name VARCHAR(100), " +
                "rule_l VARCHAR(1024), " +
                "start_time_I DATETIME, " +
                "duration_time_I DATETIME, " +
                "show_up_time DATETIME, " +
                "meet_days VARCHAR(45) " +
                ");";
        db.execSQL(qCreate_SubjectRoom_l);

        String qCreate_Chat_l = "CREATE TABLE Chat (" +
                "rid INTEGER, " +
                "senderID varchar(20), " +
                "senderText varchar(1024), " +
                "year varchar(20), " +
                "month varchar(10), " +
                "day varchar(10), " +
                "time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "me varchar(20), " +
                "messageType varchar(20)" +
                ");";
        db.execSQL(qCreate_Chat_l);

        // Image_table create
        db.execSQL("CREATE TABLE " + TABLE_IMAGE + " (" +
                "name VARCHAR(100) PRIMARY KEY," +
                "data BLOB," +
                "time DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
		/* when update the SQLite */
        db.execSQL("DROP TABLE IF EXISTS Friend_l;");
        db.execSQL("DROP TABLE IF EXISTS Room_l;");
        db.execSQL("DROP TABLE IF EXISTS LifeRoom_l;");
        db.execSQL("DROP TABLE IF EXISTS SubjectRoom_l;");
        db.execSQL("DROP TABLE IF EXISTS Chat;");
        db.execSQL("DROP TABLE IF EXISTS Image_table;");
        onCreate(db);
    }

    public boolean isImageExist(String key){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT name FROM " + TABLE_IMAGE+ " WHERE name='"+key+"';", null);

        try {
            if (cursor.getCount() > 0) {
                return true;
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        db.close();
        cursor.close();
        return false;
    }

    public Bitmap getImage(String key){

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT name, data FROM " + TABLE_IMAGE+ " WHERE name='"+key+"';";
        Cursor cursor = db.rawQuery(query, null);

//        Log.i(LOG_TAG, "query : " + query + " in getImage");
        try {
            if(cursor.getCount() > 0)
            {
//                Log.i(LOG_TAG, "cursor count : " + cursor.getCount());
                if(cursor.moveToFirst())
                {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(cursor.getBlob(1), 0, cursor.getBlob(1).length);
                    db.close();
                    cursor.close();
                    return bitmap;
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        Log.i(LOG_TAG, "return null in getImage");
        db.close();
        cursor.close();
        return null;
    }

    public void insertImage(String name, Bitmap bitmapData)
    {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmapData.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] data = stream.toByteArray();

            SQLiteDatabase db = this.getReadableDatabase();
//            String query = "INSERT INTO " + TABLE_IMAGE +
//                    "(name, data) " +
//                    "VALUES " +
//                    "(" +
//                    "'" + name + "'," +
//                    data +
//                    ");";
//
//            db.execSQL(query);
//            db.close();
            ContentValues value = new ContentValues();
            value.put("name", name);
            value.put("data", data);

            db.insert(TABLE_IMAGE, null, value);

            Log.i(LOG_TAG, "insertImage + " + name + " done ");
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void initImageList()
    {

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT name FROM " + TABLE_IMAGE+ ";";
        Cursor cursor = db.rawQuery(query, null);

        HttpAPIs.imageList = new ArrayList<String>();

//        Log.i(LOG_TAG, "query : " + query + " in getImage");
        try {
            if(cursor.getCount() > 0)
            {
//                Log.i(LOG_TAG, "cursor count : " + cursor.getCount());
                while(cursor.moveToNext())
                {
//                    Log.i(LOG_TAG, "add initImageList : " + cursor.getString(0));
                    if(!HttpAPIs.imageList.contains(cursor.getString(0)))
                    {
                        HttpAPIs.imageList.add(cursor.getString(0));
                    }
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        db.close();
        cursor.close();
    }

}