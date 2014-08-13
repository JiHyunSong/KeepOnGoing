package com.secsm.keepongoing.DB;

/**
 * Created by JinS on 2014. 8. 10..
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//DB 컨트롤(관리) 하는 객체
public class DBHelper extends SQLiteOpenHelper {

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

        String qCreate_Chat_l ="CREATE TABLE Chat (" +
                "rid INTEGER, " +
                "senderID varchar(20), " +
                "senderText varchar(1024), " +
                "time DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ");";
        db.execSQL(qCreate_Chat_l);
        db.execSQL("CREATE TABLE image_profile (id INTEGER PRIMARY KEY AUTOINCREMENT, path VARCHAR(500));");
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
        db.execSQL("DROP TABLE IF EXISTS image_profile;");
        onCreate(db);
    }

}