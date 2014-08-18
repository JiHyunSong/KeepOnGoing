package com.secsm.keepongoing.Adapters;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.secsm.keepongoing.DB.DBHelper;
import com.secsm.keepongoing.Shared.KogPreference;


/* for customized listview, we need class for message information */
public class Msg {
    private String Name;
    private String Text;
    private String Time;
    private String FileName;
    /* flag for the message from me or the other */
    private String Flag;
    private String MessageType;
    private String Id = null;

    /* if flag is true, me! exchange format */
    public Msg(Context context, String _Name, String _Text, String _Time, String _Flag) {
        this.Name = _Name + " : ";
        this.Text = _Text;
        this.Time = _Time;
        this.Flag = _Flag;
//        this.MessageType = _MessageType;

        String prof = null;
        SQLiteDatabase db;
        DBHelper mDBHelper;
        mDBHelper = new DBHelper(context);
        Cursor cursor = null;
        db = mDBHelper.getReadableDatabase();
        String myID = "" + KogPreference.getInt(context, "uid");

        Log.i("MSG", "이름 ? " + _Name);
        if ("나".equals(_Name)) {
            Id = myID;
            cursor = db.rawQuery("SELECT path FROM image_profile", null);
            if (cursor.moveToNext()) {
                prof = cursor.getString(0);
                Log.i("나:", "getString(0) : " + cursor.getString(0));
                prof = prof.substring(prof.lastIndexOf("/") + 1, prof.length());
                Log.i("나:", "prof : " + prof);
            }
        } else {
            Log.i("MSGSelcet", "friendList_ph, Name is : " + _Name);
            cursor = db.rawQuery("SELECT * FROM friendList_ph where name = '" + _Name + "'", null);
            if (cursor.moveToNext()) {
                Log.i("MSGSelect", "id : " + cursor.getString(0));
                Id = cursor.getString(0);
                prof = cursor.getString(3);
                Log.i("getNameFromSQLite", "getNameFromSQLite catch right name! : " + prof);
            } else {
                prof = "fail";
            }
        }
        cursor.close();
        db.close();
        mDBHelper.close();

        this.FileName = prof;

    }

    public String getName() {
        return Name;
    }

    public String getText() {
        return Text;
    }

    public String getTime() {
        return Time;
    }

    public String getFlag() {
        return Flag;
    }

    public String getFileName() {
        return FileName;
    }

    public String getId() {
        return Id;
    }
}
