package perklun.divebox.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by perklun on 4/23/2016.
 */
public class DiveBoxDatabaseHelper extends SQLiteOpenHelper{

    private static DiveBoxDatabaseHelper dbInstance;

    // Database Info
    private static final String DATABASE_NAME = "DiveLog";
    private static final int DATABASE_VERSION = 1;

    // Table Name
    private static final String TABLE_DIVES = "dives";
    private static final String TABLE_USERS = "users";

    // Dives Table Columns
    private static final String KEY_DIVE_ID = "id";
    private static final String KEY_DIVE_USER_ID = "userID";
    private static final String KEY_DIVE_TITLE = "title";

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
                KEY_DIVE_TITLE + " TEXT" +
                ")";

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
                "(" +
                KEY_USER_ID + " INTEGER PRIMARY KEY," +
                KEY_USER_NAME + " TEXT," +
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
}
