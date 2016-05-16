package perklun.divebox.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import perklun.divebox.db.DiveBoxDatabaseHelper;

/**
 * Created by perklun on 5/15/2016.
 */
public class DiveBoxProvider extends ContentProvider {

    private static final int DIVE = 100;
    private static final int DIVE_ID = 101;
    private static final int USER = 200;
    private static final int USER_ID = 201;

    private DiveBoxDatabaseHelper diveBoxDatabaseHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {
        diveBoxDatabaseHelper = DiveBoxDatabaseHelper.getDbInstance(getContext());
        return true;
    }

    public static UriMatcher buildUriMatcher(){
        String content = DiveBoxDatabaseContract.CONTENT_AUTHORITY;
        UriMatcher uriMatcher = new UriMatcher((UriMatcher.NO_MATCH));
        uriMatcher.addURI(content, DiveBoxDatabaseContract.PATH_DIVE, DIVE);
        uriMatcher.addURI(content, DiveBoxDatabaseContract.PATH_DIVE + "/#", DIVE_ID);
        uriMatcher.addURI(content, DiveBoxDatabaseContract.PATH_USER, USER);
        uriMatcher.addURI(content, DiveBoxDatabaseContract.PATH_USER + "/#", USER_ID);
        return uriMatcher;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = diveBoxDatabaseHelper.getWritableDatabase();
        Cursor retCursor;
        switch(sUriMatcher.match(uri)){
            case DIVE:
                retCursor = db.query(DiveBoxDatabaseContract.DiveEntry.TABLE_DIVES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case DIVE_ID:
                long _id = ContentUris.parseId(uri);
                retCursor = db.query(DiveBoxDatabaseContract.DiveEntry.TABLE_DIVES,
                        projection,
                        DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_ID + "= ?",
                        new String[]{String.valueOf((_id))},
                        null,
                        null,
                        sortOrder);
            case USER:
                retCursor = db.query(DiveBoxDatabaseContract.UserEntry.TABLE_USERS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case USER_ID:
                _id = ContentUris.parseId(uri);
                retCursor = db.query(DiveBoxDatabaseContract.UserEntry.TABLE_USERS,
                        projection,
                        DiveBoxDatabaseContract.UserEntry.KEY_USER_ID + "= ?",
                        new String[]{String.valueOf((_id))},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = diveBoxDatabaseHelper.getWritableDatabase();
        long _id;
        Uri returnUri;
        switch(sUriMatcher.match(uri)){
            case DIVE:
                _id = db.insert(DiveBoxDatabaseContract.DiveEntry.TABLE_DIVES, null, values);
                if(_id > 0){
                    returnUri =  DiveBoxDatabaseContract.DiveEntry.buildDiveUri(_id);
                } else{
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case USER:
                _id = db.insert(DiveBoxDatabaseContract.UserEntry.TABLE_USERS, null, values);
                if(_id > 0){
                    returnUri = DiveBoxDatabaseContract.UserEntry.buildUserUri(_id);
                } else{
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Use this on the URI passed into the function to notify any observers that the uri has
        // changed.
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = diveBoxDatabaseHelper.getWritableDatabase();
        int rows; // Number of rows effected

        switch(sUriMatcher.match(uri)){
            case DIVE:
                rows = db.delete(DiveBoxDatabaseContract.DiveEntry.TABLE_DIVES, selection, selectionArgs);
                break;
            case USER:
                rows = db.delete(DiveBoxDatabaseContract.UserEntry.TABLE_USERS, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because null could delete all rows:
        if(selection == null || rows != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = diveBoxDatabaseHelper.getWritableDatabase();
        int rows;

        switch(sUriMatcher.match(uri)){
            case DIVE:
                rows = db.update(DiveBoxDatabaseContract.DiveEntry.TABLE_DIVES, values, selection, selectionArgs);
                break;
            case USER:
                rows = db.update(DiveBoxDatabaseContract.UserEntry.TABLE_USERS, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rows != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch ((sUriMatcher.match(uri))) {
            case DIVE:
                return DiveBoxDatabaseContract.DiveEntry.CONTENT_TYPE;
            case DIVE_ID:
                return DiveBoxDatabaseContract.DiveEntry.CONTENT_ITEM_TYPE;
            case USER:
                return DiveBoxDatabaseContract.UserEntry.CONTENT_TYPE;
            case USER_ID:
                return DiveBoxDatabaseContract.UserEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
    }
}
