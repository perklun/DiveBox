package perklun.divebox.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import perklun.divebox.contentprovider.DiveBoxDatabaseContract;
import perklun.divebox.models.Dive;
import perklun.divebox.models.User;
import perklun.divebox.utils.Constants;

/**
 * Created by perklun on 4/23/2016.
 */
public class DiveBoxDatabaseHelper extends SQLiteOpenHelper{

    private static DiveBoxDatabaseHelper dbInstance;

    // Database Info
    private static final String DATABASE_NAME = "divelog.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "DBHELPER";

    // Private constructor
    private DiveBoxDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Singleton pattern for application
    public static synchronized DiveBoxDatabaseHelper getDbInstance(Context context){
        if(dbInstance == null){
            dbInstance = new DiveBoxDatabaseHelper(context.getApplicationContext());
        }
        return dbInstance;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DIVES_TABLE = "CREATE TABLE " + DiveBoxDatabaseContract.DiveEntry.TABLE_DIVES +
                "(" +
                DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_USER_ID + " INTEGER REFERENCES " + DiveBoxDatabaseContract.UserEntry.TABLE_USERS + "," + // Define a foreign key
                DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_TITLE + " TEXT," +
                DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_LAT + " REAL," +
                DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_LONG + " REAL," +
                DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_DATE + " REAL," +
                DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_COMMENT + " REAL," +
                DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_AIR_IN + " REAL," +
                DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_AIR_OUT + " REAL," +
                DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_TIME_IN + " REAL," +
                DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_TIME_OUT + " REAL," +
                DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_BTM_TIME + " REAL" +
                ")";

        String CREATE_USERS_TABLE = "CREATE TABLE " + DiveBoxDatabaseContract.UserEntry.TABLE_USERS +
                "(" +
                DiveBoxDatabaseContract.UserEntry.KEY_USER_ID + " INTEGER PRIMARY KEY," +
                DiveBoxDatabaseContract.UserEntry.KEY_USER_NAME + " TEXT, " +
                DiveBoxDatabaseContract.UserEntry.KEY_USER_GOOGLEID + " TEXT" +
                ")";
        db.execSQL(CREATE_DIVES_TABLE);
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + DiveBoxDatabaseContract.DiveEntry.TABLE_DIVES);
            db.execSQL("DROP TABLE IF EXISTS " + DiveBoxDatabaseContract.UserEntry.TABLE_USERS);
            onCreate(db);
        }
    }

    // Insert a dive
    public int addDive(Dive dive){
        int result = Constants.DB_OPS_ERROR;
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction(); //for consistency
        try{
            long userId = addOrUpdateUser(dive.getUser());
            ContentValues values = new ContentValues();
            values.put(DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_USER_ID, userId);
            values.put(DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_TITLE, dive.getTitle());
            values.put(DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_LAT, dive.getLatLng().latitude);
            values.put(DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_LONG, dive.getLatLng().longitude);
            values.put(DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_DATE, dive.getDate());
            values.put(DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_COMMENT, dive.getComments());
            values.put(DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_AIR_IN, dive.getAirIn());
            values.put(DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_AIR_OUT, dive.getAirOut());
            values.put(DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_TIME_IN, dive.getTimeIn());
            values.put(DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_TIME_OUT, dive.getTimeOut());
            values.put(DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_BTM_TIME, dive.getBtmTime());
            //primary key autoincremented
            long diveId = db.insertOrThrow(DiveBoxDatabaseContract.DiveEntry.TABLE_DIVES, null, values);
            Log.d(TAG, "Added dive key" + diveId);
            db.setTransactionSuccessful();
            result = Constants.DB_OPS_SUCCESS;
        }
        catch (Exception e){
            Log.d(TAG, "Error trying to add dive data");
        }
        finally {
            db.endTransaction();
        }
        return result;
    }

    public long addOrUpdateUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        long userId = -1;
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(DiveBoxDatabaseContract.UserEntry.KEY_USER_GOOGLEID, user.googleID);
            values.put(DiveBoxDatabaseContract.UserEntry.KEY_USER_NAME, user.username);
            int rows = db.update(DiveBoxDatabaseContract.UserEntry.TABLE_USERS, values, DiveBoxDatabaseContract.UserEntry.KEY_USER_GOOGLEID + "= ?", new String[]{user.googleID});
            if(rows == 1){
                // Get primary key
                String usersSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?", DiveBoxDatabaseContract.UserEntry.KEY_USER_ID, DiveBoxDatabaseContract.UserEntry.TABLE_USERS, DiveBoxDatabaseContract.UserEntry.KEY_USER_GOOGLEID);
                Cursor cursor = db.rawQuery(usersSelectQuery, new String[]{String.valueOf(user.googleID)});
                try{
                    if(cursor.moveToFirst()){
                        userId = cursor.getInt(0);
                        db.setTransactionSuccessful();
                    }
                }
                finally {
                    if(cursor != null && !cursor.isClosed()){
                        cursor.close();
                    }
                }
            }
            else{
                userId = db.insertOrThrow(DiveBoxDatabaseContract.UserEntry.TABLE_USERS, null, values);
                db.setTransactionSuccessful();
            }
        }
        catch (Exception e){
            Log.d(TAG,"Error trying to add/update user");
        }
        finally {
            db.endTransaction();
        }
        return  userId;
    }

    public List<Dive> getAllDives(String googleId){
        List<Dive> dives = new ArrayList<>();
        int userId = getUserId(googleId);
        if(userId >= 0){
            String DIVES_SELECT_QUERY = String.format("SELECT * FROM %s WHERE %s = ?",
                    DiveBoxDatabaseContract.DiveEntry.TABLE_DIVES, DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_USER_ID);
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery(DIVES_SELECT_QUERY, new String[]{String.valueOf(userId)});
            try{
                if(cursor.moveToFirst()){
                    do{
                        User newUser = new User(googleId);
                        LatLng position = new LatLng(cursor.getDouble(cursor.getColumnIndex(DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_LAT)),cursor.getDouble(cursor.getColumnIndex(DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_LONG)));
                        Dive newDive = new Dive(newUser, cursor.getString(cursor.getColumnIndex(DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_TITLE)), position);
                        newDive.setId(cursor.getInt(cursor.getColumnIndex(DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_ID)));
                        newDive.setDate(cursor.getString(cursor.getColumnIndex(DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_DATE)));
                        newDive.setComments(cursor.getString(cursor.getColumnIndex(DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_COMMENT)));
                        newDive.setAirIn(cursor.getString(cursor.getColumnIndex(DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_AIR_IN)));
                        newDive.setAirOut(cursor.getString(cursor.getColumnIndex(DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_AIR_OUT)));
                        newDive.setTimeIn(cursor.getString(cursor.getColumnIndex(DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_TIME_IN)));
                        newDive.setTimeOut(cursor.getString(cursor.getColumnIndex(DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_TIME_OUT)));
                        newDive.setBtmTime(cursor.getString(cursor.getColumnIndex(DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_BTM_TIME)));
                        dives.add(newDive);
                    }
                    while(cursor.moveToNext());
                }
            }
            catch (Exception e){
                Log.d(TAG, "Error retrieving dives");
            }
            finally {
                if(cursor != null && !cursor.isClosed()){
                    cursor.close();
                }
            }
        }
        else{
            Log.d(TAG, "Error retrieving user");
        }
        return dives;
    }

    public int deleteDive(Dive dive){
        int result = Constants.DB_OPS_ERROR;
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction(); //for consistency
        try{
            //primary key autoincremented
            db.delete(DiveBoxDatabaseContract.DiveEntry.TABLE_DIVES, DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_ID + '=' + dive.getId(), null);
            Log.d(TAG, "Deleted dive key" + dive.getId());
            db.setTransactionSuccessful();
            result = Constants.DB_OPS_SUCCESS;
        }
        catch (Exception e){
            Log.d(TAG, "Error trying to delete dive data");
        }
        finally {
            db.endTransaction();
        }
        return result;
    }

    public int getUserId(String googleId){
        SQLiteDatabase db = getReadableDatabase();
        int userId = -1;
        String usersSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?", DiveBoxDatabaseContract.UserEntry.KEY_USER_ID, DiveBoxDatabaseContract.UserEntry.TABLE_USERS, DiveBoxDatabaseContract.UserEntry.KEY_USER_GOOGLEID);
        Cursor cursor = db.rawQuery(usersSelectQuery, new String[]{googleId});
        if(cursor.getCount() == 1){
            try{
                if(cursor.moveToFirst()){
                    userId = cursor.getInt(0);
                }
            }
            finally {
                if(cursor != null && !cursor.isClosed()){
                    cursor.close();
                }
            }
        }
        if(userId >= 0){
            return userId;
        }
        return Constants.DB_OPS_ERROR;
    }

    public long getDiveCount(String googleId){
        long userId = getUserId(googleId);
        SQLiteDatabase db = getReadableDatabase();
        if(userId > 0){
            return DatabaseUtils.queryNumEntries(db, DiveBoxDatabaseContract.DiveEntry.TABLE_DIVES, DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_USER_ID + "=" + userId);
        }
        return Constants.DB_OPS_ERROR;
    }
}
