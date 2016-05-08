package perklun.divebox.utils;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/**
 * Created by perklun on 5/2/2016.
 */
/*
 * This is the object responsible for communicating with a REST API.
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes:
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 */
public class TwitterClient extends OAuthBaseClient {

    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
    public static final String REST_URL = "https://api.twitter.com/1.1/";
    public static final String REST_CONSUMER_KEY = "OCuSiqYI2LArfIjTzWRciUSGW";
    public static final String REST_CONSUMER_SECRET = "hlf1zjp31EyyptIunY4luL0Jg6ctvR09grzbYpe6IK9kmkDmss";
    public static final String REST_CALLBACK_URL = "oauth://diveBoxTwitterHandler"; //Change this in manifest
    public static final int count = 25;

    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    /**
     * Obtain home timeline from twitter, with count and since ID, success and failure handled by handler
     *
     * @param handler
     */
    public void getHomeTimeLine(AsyncHttpResponseHandler handler){

        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", count);
        getClient().get(apiUrl, params, handler);
    }

    /**
     * Makes a API request to obtain user timeline
     *
     * @param screenname
     * @param max_id
     * @param handler
     */
    public void getUserTimeLine(String screenname, long max_id, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", count);
        params.put("screen_name", screenname);
        if(max_id > 0){
            params.put("max_id", max_id);
        }
        getClient().get(apiUrl, params, handler);
    }

}
