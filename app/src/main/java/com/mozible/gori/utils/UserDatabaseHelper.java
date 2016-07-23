package com.mozible.gori.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mozible.gori.models.UserProfile;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by JunLee on 7/21/16.
 */
public class UserDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "gori_user.db";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase mSqliteDatabase;

    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mSqliteDatabase = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "create table " + GoriConstants.FOLLOWING_TABLE_NAME + "( " +
                        "id INT NOT NULL, username TEXT PRIMARY KEY NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GoriConstants.FOLLOWING_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertFollowingUsers(List<UserProfile> userProfileList) {
        if(userProfileList == null || userProfileList.size() == 0) {
            return false;
        } else {
            Iterator<UserProfile> iter = userProfileList.iterator();
            while(iter.hasNext()) {
                UserProfile userProfile = iter.next();
                ContentValues values = new ContentValues();
                values.put("id", userProfile.user.id);
                values.put("username", userProfile.user.username);
                mSqliteDatabase.insert(GoriConstants.FOLLOWING_TABLE_NAME, null, values);
            }

            return true;
        }
    }

    public HashMap<Integer, String> getFollowingUsers() {
        HashMap<Integer, String> result = new HashMap<Integer, String>();
        Cursor cursor = mSqliteDatabase.query(GoriConstants.FOLLOWING_TABLE_NAME, new String[]{"id", "username"}, null, null, null, null, null, null);
        if(cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                result.put(cursor.getInt(0), cursor.getString(1));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return result;
    }

    public boolean deleteFollowingUser(int id) {
        try {
            mSqliteDatabase.delete(GoriConstants.FOLLOWING_TABLE_NAME, "id=?", new String[]{String.valueOf(id)});
            return true;
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean insertFollowingUser(UserProfile userProfile) {
        try {
            ContentValues values = new ContentValues();
            values.put("id", userProfile.user.id);
            values.put("username", userProfile.user.username);
            mSqliteDatabase.insert(GoriConstants.FOLLOWING_TABLE_NAME, null, values);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean deleteAllFollowingUser() {
        try {
            mSqliteDatabase.delete(GoriConstants.FOLLOWING_TABLE_NAME, null, null);
            return true;
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
