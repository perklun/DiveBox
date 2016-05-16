package perklun.divebox.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

import perklun.divebox.R;
import perklun.divebox.activities.LoginActivity;
import perklun.divebox.contentprovider.DiveBoxDatabaseContract;
import perklun.divebox.utils.Constants;

/**
 * Created by perklun on 5/11/2016.
 */
public class DiveBoxWidgetIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public DiveBoxWidgetIntentService() {
        super("DiveBoxWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String googleId = intent.getStringExtra(Constants.WIDGET_GOOGLE_ID);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, DiveBoxWidgetProvider.class));
        String[] projectionFields = new String[] {
                DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_BTM_TIME,
                DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_DATE};
        Cursor cursor = getContentResolver().query(DiveBoxDatabaseContract.DiveEntry.buildLastDiveUri(), projectionFields, null, null, null);
        for (int i=0; i<appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];
            // Get the layout for App Widget
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.divebox_widget);
            if(cursor != null){
                if(cursor.moveToFirst()){
                    views.setTextViewText(R.id.tv_widget_btm_time_value, cursor.getString(cursor.getColumnIndex(DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_BTM_TIME)));
                    views.setTextViewText(R.id.tv_widget_time_since_value, cursor.getString(cursor.getColumnIndex(DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_DATE)));
                    //TODO: set max depth
                    //views.setTextViewText(R.id.tv_widget_depth_value, cursor.getString(cursor.getColumnIndex(DiveBoxDatabaseContract.DiveEntry.KEY_D)));
                }
            }
            Intent open_app_intent = new Intent(this, LoginActivity.class);
            views.setOnClickPendingIntent(R.id.dive_box_widget, PendingIntent.getActivity(this,0, open_app_intent, PendingIntent.FLAG_CANCEL_CURRENT));
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
