package perklun.divebox.utils;

import android.content.Context;

/**
 * Created by perklun on 5/2/2016.
 */
public class DiveBoxApplication extends com.activeandroid.app.Application{
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        DiveBoxApplication.context = this;
    }

    public static TwitterClient getRestClient() {
        return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, DiveBoxApplication.context);
    }
}
