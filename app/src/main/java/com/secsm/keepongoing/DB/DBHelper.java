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
        super(context, "KogDB.db", null, 2);
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

        String qCreate_Chat_New = "CREATE TABLE chat_new ("+
                "rid varchar(10) PRIMARY KEY, " +
                "isNew BOOLEAN default FALSE);";
        db.execSQL(qCreate_Chat_New);

        String qCreate_Quiz_New = "CREATE TABLE quiz_new ("+
                "rid varchar(10) PRIMARY KEY, " +
                "num varchar(10), " +
                "isNew BOOLEAN default FALSE);";
        db.execSQL(qCreate_Quiz_New);

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
        db.execSQL("DROP TABLE IF EXISTS chat_new;");
        db.execSQL("DROP TABLE IF EXISTS quiz_new;");
        onCreate(db);
    }

    public boolean isChatNew(String rid)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT isNew FROM chat_new WHERE rid='"+rid+"';", null);

        try{
            if(cursor.getCount() > 0)
            {
                if(cursor.moveToFirst())
                {
                    if (cursor.isNull(0) || cursor.getShort(0) == 0) {
                        db.close();
                        cursor.close();
//                        Log.i(LOG_TAG, "isChatNew false in rid: "  + rid);
                        return false;
                    } else {
                        db.close();
                        cursor.close();
//                        Log.i(LOG_TAG, "isChatNew true in rid: "  + rid);
                        return true;
                    }

                }
            }
        }catch (Exception e)
        {
            db.close();
            cursor.close();
            e.printStackTrace();
        }

        db.close();
        cursor.close();
        return false;
    }

    public void UpdateChatNew(String rid, boolean isNew)
    {
        Log.i(LOG_TAG, "UpdateChatNew : rid " + rid + " isNew " + isNew);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT isNew FROM chat_new WHERE rid='"+rid+"';", null);
        int insertIsNew = (isNew ? 1 : 0 );
        try{
            if(cursor.getCount() > 0) {
                // update row
//                db.execSQL("UPDATE chat_new SET isNew=" + insertIsNew + " WHERE rid='"+rid+"';");

//                Log.i(LOG_TAG, "UpdateChatNew : rid " + rid + " isNew " + insertIsNew);
                ContentValues value = new ContentValues();
                value.put("isNew", insertIsNew);
                String where = "rid=?";
                String[] wheresArgs = new String[] {String.valueOf(rid)};
                db.update("chat_new", value, where, wheresArgs);
//                Log.i(LOG_TAG, "UpdateChatNew : updateDone");

            }else
            {
//                Log.i(LOG_TAG, "UpdateChatNew : rid " + rid + " isNew " + insertIsNew);
                // insert row
//                db.execSQL("INSERT INTO chat_new (rid, isNew) VALUES (" +
//                        "'" + rid + "'," +
//                        "" + insertIsNew + "" +
//                        ");");

                ContentValues value = new ContentValues();
                value.put("rid", rid);
                value.put("isNew", insertIsNew);
                db.insert("chat_new", null, value);
//                Log.i(LOG_TAG, "UpdateChatNew : insertDone");

            }
        }
        catch (Exception e)
        {
            db.close();
            cursor.close();
            e.printStackTrace();
        }
        db.close();
        cursor.close();
    }

    public boolean isQuizNew(String rid)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT isNew FROM quiz_new WHERE rid='"+rid+"';", null);

        try{
            if(cursor.getCount() > 0)
            {
                if(cursor.moveToFirst())
                {
//                    Log.i(LOG_TAG, "isQuizNew cursor.isNull(0) : " + cursor.isNull(0));
//                    Log.i(LOG_TAG, "isQuizNew cursor.getShort(0) : " + cursor.getShort(0));
                    if (cursor.isNull(0) || cursor.getShort(0) == 0) {
                        db.close();
                        cursor.close();
                        return false;
                    } else {
                        db.close();
                        cursor.close();
//                        Log.i(LOG_TAG, "isQuizNew true");
                        return true;
                    }

                }
            }
        }catch (Exception e)
        {
            db.close();
            cursor.close();
            e.printStackTrace();
        }

        db.close();
        cursor.close();
        return false;
    }

    public String getQuizNew(String rid)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT num FROM quiz_new WHERE rid='"+rid+"';", null);

        try{
            if(cursor.getCount() > 0)
            {
                if(cursor.moveToFirst())
                {
                    db.close();
                    cursor.close();
                    return cursor.getString(0);
                }
            }
        }catch (Exception e)
        {
            db.close();
            cursor.close();
            e.printStackTrace();
        }

        db.close();
        cursor.close();
        return null;
    }

    public void UpdateQuizNew(String rid, String num, boolean isNew)
    {
        Log.i(LOG_TAG, "UpdateQuizNew : rid " + rid + " num " + num + " isNew " + isNew);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT isNew FROM quiz_new WHERE rid='"+rid+"';", null);
        int insertIsNew = (isNew ? 1 : 0 );
        try{
            if(cursor.getCount() > 0) {
                // update row
//                db.execSQL("UPDATE quiz_new SET num='" +num +"',isNew=" + insertIsNew + " WHERE rid='"+rid+"';");

                ContentValues value = new ContentValues();
                value.put("num", num);
                value.put("isNew", insertIsNew);
                String where = "rid=?";
                String[] wheresArgs = new String[] {String.valueOf(rid)};
                db.update("quiz_new", value, where, wheresArgs);
            }else
            {
                // insert row
//                db.execSQL("INSERT INTO quiz_new (rid, num, isNew) VALUES (" +
//                        "'" + rid + "'," +
//                        "'" + num + "'," +
//                        "" + insertIsNew + "" +
//                        ");");

                ContentValues value = new ContentValues();
                value.put("rid", rid);
                value.put("num", num);
                value.put("isNew", insertIsNew);
                db.insert("quiz_new", null, value);

            }
        }
        catch (Exception e)
        {
            db.close();
            cursor.close();
            e.printStackTrace();
        }
        db.close();
        cursor.close();
    }

    public boolean isImageExist(String key){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT name FROM " + TABLE_IMAGE+ " WHERE name='"+key+"';", null);

        try {
            if (cursor.getCount() > 0) {
                db.close();
                cursor.close();
                return true;
            }
        }catch (Exception e)
        {
            db.close();
            cursor.close();
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
            db.close();
            cursor.close();
            e.printStackTrace();
        }

        Log.i(LOG_TAG, "return null in getImage");
        db.close();
        cursor.close();
        return null;
    }

    public void insertImage(String name, Bitmap bitmapData)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmapData.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] data = stream.toByteArray();

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
            db.close();

            Log.i(LOG_TAG, "insertImage + " + name + " done ");
        }catch (Exception e)
        {
            db.close();
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
            db.close();
            cursor.close();
            e.printStackTrace();
        }

        db.close();
        cursor.close();
    }

}