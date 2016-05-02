package perklun.divebox.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import perklun.divebox.models.Dive;
import perklun.divebox.models.User;
import perklun.divebox.utils.Constants;

/**
 * Created by perklun on 4/23/2016.
 */
public class DiveBoxDatabaseHelper extends SQLiteOpenHelper{

    private static DiveBoxDatabaseHelper dbInstance;

    // Database Info
    private static final String DATABASE_NAME = "DiveLog";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "DBHELPER";

    // Table Name
    private static final String TABLE_DIVES = "dives";
    private static final String TABLE_USERS = "users";

    // Dives Table Columns
    private static final String KEY_DIVE_ID = "id";
    private static final String KEY_DIVE_USER_ID = "userID";
    private static final String KEY_DIVE_TITLE = "title";
    private static final String KEY_DIVE_LAT = "lat";
    private static final String KEY_DIVE_LONG = "long";

    // Users Table Columns
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_NAME = "username";

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
        String CREATE_DIVES_TABLE = "CREATE TABLE " + TABLE_DIVES +
                "(" +
                KEY_DIVE_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_DIVE_USER_ID + " INTEGER REFERENCES " + TABLE_USERS + "," + // Define a foreign key
                KEY_DIVE_TITLE + " TEXT," +
                KEY_DIVE_LAT + " REAL," +
                KEY_DIVE_LONG + " REAL" +
                ")";

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
                "(" +
                KEY_USER_ID + " INTEGER PRIMARY KEY," +
                KEY_USER_NAME + " TEXT" +
                ")";
        db.execSQL(CREATE_DIVES_TABLE);
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIVES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
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
            values.put(KEY_DIVE_USER_ID, userId);
            values.put(KEY_DIVE_TITLE, dive.getTitle());
            values.put(KEY_DIVE_LAT, dive.getLatLng().latitude);
            values.put(KEY_DIVE_LONG, dive.getLatLng().longitude);
            //primary key autoincremented
            long diveId = db.insertOrThrow(TABLE_DIVES, null, values);
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

    private long addOrUpdateUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        long userId = -1;
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(KEY_USER_NAME, user.username);
            int rows = db.update(TABLE_USERS, values, KEY_USER_NAME + "= ?", new String[]{user.username});
            if(rows == 1){
                // Get primary key
                String usersSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?", KEY_USER_ID, TABLE_USERS, KEY_USER_NAME);
                Cursor cursor = db.rawQuery(usersSelectQuery, new String[]{String.valueOf((user.username))});
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
                userId = db.insertOrThrow(TABLE_USERS, null, values);
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

    public List<Dive> getAllDives(){
        List<Dive> dives = new ArrayList<>();
        /*String DIVES_SELECT_QUERY = String.format("SELECT * FROM %s LEFT OUTER JOIN %s ON %s.%s = %s.%s",
                TABLE_DIVES,
                TABLE_USERS,
                TABLE_DIVES, KEY_DIVE_USER_ID,
                TABLE_USERS, KEY_USER_ID);
        */
        String DIVES_SELECT_QUERY = String.format("SELECT * FROM %s",
                TABLE_DIVES);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(DIVES_SELECT_QUERY, null);
        try{
           if(cursor.moveToFirst()){
               do{
                   //TODO: This looks to be getting all users and their dives
                   User newUser = new User(cursor.getString(cursor.getColumnIndex(KEY_DIVE_USER_ID)));
                   LatLng position = new LatLng(cursor.getDouble(cursor.getColumnIndex(KEY_DIVE_LAT)),cursor.getDouble(cursor.getColumnIndex(KEY_DIVE_LONG)));
                   Dive newDive = new Dive(newUser, cursor.getString(cursor.getColumnIndex(KEY_DIVE_TITLE)), position);
                   newDive.setId(cursor.getInt(cursor.getColumnIndex(KEY_DIVE_ID)));
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
        return dives;
    }

    public int deleteDive(Dive dive){
        int result = Constants.DB_OPS_ERROR;
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction(); //for consistency
        try{
            //primary key autoincremented
            db.delete(TABLE_DIVES, KEY_DIVE_ID + '=' + dive.getId(), null);
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
}
