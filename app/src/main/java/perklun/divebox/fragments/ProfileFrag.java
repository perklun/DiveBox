package perklun.divebox.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import perklun.divebox.R;
import perklun.divebox.db.DiveBoxDatabaseHelper;
import perklun.divebox.utils.Constants;

public class ProfileFrag extends Fragment {
    // newInstance constructor for creating fragment with arguments
    private SharedPreferences mSettings;
    DiveBoxDatabaseHelper dbHelper;
    String googleID;
    int diveCount;
    TextView tvProfileDiveCount;

    public static ProfileFrag newInstance() {
        ProfileFrag fragmentFirst = new ProfileFrag();
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettings = getActivity().getSharedPreferences(getString(R.string.SHARED_PREFERENCE_FILE_KEY),0);
        dbHelper = DiveBoxDatabaseHelper.getDbInstance(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ImageView ivProfilePic = (ImageView) view.findViewById(R.id.iv_profile_picture);
        String uri = mSettings.getString(getString(R.string.SHARED_PREF_PHOTO_URL_KEY),"");
        if(uri.length() > 0){
            Picasso.with(this.getContext()).load(uri).into(ivProfilePic);
        }
        TextView tvProfileName = (TextView) view.findViewById(R.id.tv_profile_name);
        String profileName = mSettings.getString(getString(R.string.SHARED_PREF_USERNAME_KEY),"");
        if(profileName.length() > 0){
            tvProfileName.setText(profileName);
        }
        googleID = mSettings.getString(getString(R.string.SHARED_PREF_GOOGLE_ID_KEY),null);
        if(googleID != null){
            tvProfileDiveCount = (TextView) view.findViewById(R.id.tv_dive_count);
            diveCount = (int)dbHelper.getDiveCount(googleID);
            if(diveCount != Constants.DB_OPS_ERROR){
                tvProfileDiveCount.setText(getString(R.string.NUMBER_OF_DIVES) + String.valueOf(diveCount));
            }
        }
        return view;
    }

    public void increaseDiveCount(){
        if(diveCount >= 0){
            diveCount++;
        }
        else{
            diveCount = 1;
        }
        tvProfileDiveCount.setText(getString(R.string.NUMBER_OF_DIVES) + String.valueOf(diveCount));
    }

    public void decreaseDiveCount(){
        if(diveCount > 0){
            diveCount--;
        }
        else{
            diveCount = 0;
        }
        tvProfileDiveCount.setText(getString(R.string.NUMBER_OF_DIVES) + String.valueOf(diveCount));
    }
}
