package perklun.divebox.contentprovider;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by perklun on 5/15/2016.
 */
public class DiveBoxDatabaseContract {

    public static final String CONTENT_AUTHORITY = "perklun.divebox.diveboxdatabase";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_DIVE = "dive";
    public static final String PATH_LAST_DIVE = "last_dive";
    public static final String PATH_USER = "user";

    public static final class DiveEntry implements BaseColumns {
        // Content URI represents the base location for the table
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_DIVE).build();

        // These are special type prefixes that specify if a URI returns a list or a specific item
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_DIVE;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_DIVE;

        public static final String TABLE_DIVES = "dives";
        // Dives Table Columns
        public static final String KEY_DIVE_ID = "id";
        public static final String KEY_DIVE_USER_ID = "userID";
        public static final String KEY_DIVE_TITLE = "title";
        public static final String KEY_DIVE_LAT = "lat";
        public static final String KEY_DIVE_LONG = "long";
        public static final String KEY_DIVE_DATE = "date";
        public static final String KEY_DIVE_COMMENT = "comment";
        public static final String KEY_DIVE_AIR_IN = "air_in";
        public static final String KEY_DIVE_AIR_OUT = "air_out";
        public static final String KEY_DIVE_TIME_IN = "time_in";
        public static final String KEY_DIVE_TIME_OUT = "time_out";
        public static final String KEY_DIVE_BTM_TIME = "btm_time";

        // Define a function to build a URI to find a specific movie by it's identifier
        public static Uri buildDiveUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildLastDiveUri() {
            return CONTENT_URI.buildUpon().appendPath(PATH_LAST_DIVE).build();
        }
    }

    public static final class UserEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_USER;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_USER;

        public static final String TABLE_USERS = "users";
        // Users Table Columns
        public static final String KEY_USER_ID = "id";
        public static final String KEY_USER_NAME = "username";
        public static final String KEY_USER_GOOGLEID = "googleid";

        public static Uri buildUserUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
