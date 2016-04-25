package perklun.divebox.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.SupportMapFragment;
import com.squareup.picasso.Picasso;

import perklun.divebox.R;

public class ProfileFrag extends Fragment {
    // newInstance constructor for creating fragment with arguments
    private SharedPreferences mSettings;

    public static ProfileFrag newInstance() {
        ProfileFrag fragmentFirst = new ProfileFrag();
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettings = getActivity().getSharedPreferences(getString(R.string.SHARED_PREFERENCE_FILE_KEY),0);
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
        return view;
    }
}
