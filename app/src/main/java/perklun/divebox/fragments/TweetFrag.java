package perklun.divebox.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import perklun.divebox.R;
import perklun.divebox.utils.DiveBoxApplication;
import perklun.divebox.utils.TwitterClient;

public class TweetFrag extends Fragment {
    TwitterClient client;

    // newInstance constructor for creating fragment with arguments
    public static TweetFrag newInstance() {
        TweetFrag fragmentFirst = new TweetFrag();
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet, container, false);
        /*
        client = DiveBoxApplication.getRestClient();
        if (!client.isAuthenticated()) {
            client.connect();
        }
        populateTimeLine();
        */
        return view;
    }

    /**
     * Handler to send API request to twitter to retrieve home timeline
     **/
    public void populateTimeLine() {
        client.getHomeTimeLine(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("Res:", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("Res:", String.valueOf(statusCode)+ " " + errorResponse.toString());
            }
        });
    }
}
