package perklun.divebox.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import perklun.divebox.R;
import perklun.divebox.utils.Constants;

/**
 * Created by perklun on 5/10/2016.
 */
public class DiveBoxWidgetProvider extends AppWidgetProvider {
    String googleId;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        startIntentService(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        startIntentService(context);
    }

    public void startIntentService(Context context){
        SharedPreferences mSettings = context.getSharedPreferences(context.getString(R.string.SHARED_PREFERENCE_FILE_KEY),0);
        googleId = mSettings.getString(context.getString(R.string.SHARED_PREF_GOOGLE_ID_KEY),"");
        Intent i = new Intent(context, DiveBoxWidgetIntentService.class);
        i.putExtra(Constants.WIDGET_GOOGLE_ID, googleId);
        context.startService(i);
    }
}
