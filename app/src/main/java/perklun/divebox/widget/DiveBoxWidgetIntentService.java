package perklun.divebox.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.widget.RemoteViews;

import perklun.divebox.R;
import perklun.divebox.activities.LoginActivity;

/**
 * Created by perklun on 5/11/2016.
 */
public class DiveBoxWidgetIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DiveBoxWidgetIntentService() {
        super("DiveBoxWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, DiveBoxWidgetProvider.class));
        for (int i=0; i<appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];
            // Get the layout for App Widget
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.divebox_widget);
            views.setTextViewText(R.id.tv_widget_btm_time_value, "CRAZY TIME");
            Intent open_app_intent = new Intent(this, LoginActivity.class);
            views.setOnClickPendingIntent(R.id.dive_box_widget, PendingIntent.getActivity(this,0, open_app_intent, PendingIntent.FLAG_CANCEL_CURRENT));
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
