package perklun.divebox.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import perklun.divebox.R;
import perklun.divebox.activities.LoginActivity;
import perklun.divebox.db.DiveBoxDatabaseHelper;
import perklun.divebox.utils.Constants;

public class ProfileFrag extends Fragment {
    // newInstance constructor for creating fragment with arguments
    private SharedPreferences mSettings;
    private SharedPreferences.Editor mSettingEditior;
    DiveBoxDatabaseHelper dbHelper;

    int diveCount;
    TextView tvProfileDiveCount;
    TextView tvDiveCert;
    TextView tvDiveLevel;
    TextView tvWeight;
    ImageButton ibSelectDiveCert;
    ImageButton ibSelectWeight;
    Button btnLogout;

    String googleID;
    String prefDiveCert;
    String prefDiveLevel;
    String prefDWeight;
    String prefDWeightMetric;
    String [] weights;

    public static ProfileFrag newInstance() {
        ProfileFrag fragmentFirst = new ProfileFrag();
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettings = getActivity().getSharedPreferences(getString(R.string.SHARED_PREFERENCE_FILE_KEY),0);
        mSettingEditior = mSettings.edit();
        dbHelper = DiveBoxDatabaseHelper.getDbInstance(getActivity().getApplicationContext());
        weights = new String[20];
        for(int i = 0; i < 20; i++){
            weights[i] = String.valueOf(i+1);
        }

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
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
        btnLogout = (Button) view.findViewById(R.id.btn_profile_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LoginActivity.class);
                i.putExtra(Constants.LOGOUT, Constants.REQUEST_CODE_LOGOUT);
                getActivity().startActivity(i);
            }
        });
        prefDiveLevel = mSettings.getString(getString(R.string.SHARED_PREF_DIVE_LEVEL),"Please select one");
        prefDiveCert = mSettings.getString(getString(R.string.SHARED_PREF_DIVE_CERT),"Please select one");
        tvDiveCert = (TextView) view.findViewById(R.id.tv_dive_cert);
        tvDiveCert.setText(prefDiveCert);
        tvDiveLevel = (TextView) view.findViewById(R.id.tv_dive_level);
        tvDiveLevel.setText(prefDiveLevel);
        ibSelectDiveCert = (ImageButton) view.findViewById(R.id.ibtn_selelct_dive_cert);
        ibSelectDiveCert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCertificationDialog();
            }
        });
        prefDWeight = mSettings.getString(getString(R.string.SHARED_PREF_WEIGHT),"Please select one");
        prefDWeightMetric = mSettings.getString(getString(R.string.SHARED_PREF_WEIGHT_METRIC),"");
        tvWeight = (TextView) view.findViewById(R.id.tv_weight_amount);
        tvWeight.setText(prefDWeight + " " + prefDWeightMetric);
        ibSelectWeight = (ImageButton) view.findViewById(R.id.ibtn_select_weight);
        ibSelectWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWeightDialog();
            }
        });
        return view;
    }

    public void showWeightDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setTitle(getContext().getResources().getString(R.string.WEIGHT));
        dialog.setContentView(R.layout.number_picker);
        Button btnSelectKG = (Button) dialog.findViewById(R.id.btn_select_kg);
        Button btnSelectLBS = (Button) dialog.findViewById(R.id.btn_select_lbs);
        final NumberPicker np = (NumberPicker) dialog.findViewById(R.id.np_weight_value);
        np.setMaxValue(20); // max value 20
        np.setMinValue(0);   // min value 0
        np.setValue(Integer.valueOf(prefDWeight));
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //Do nothing
            }
        });
        btnSelectKG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWeightMetrics(true, np.getValue());
                tvWeight.setText(prefDWeight + " " + prefDWeightMetric);
                dialog.dismiss();
            }
        });
        btnSelectLBS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWeightMetrics(false, np.getValue());
                dialog.dismiss(); // dismiss the dialog
            }
        });
        dialog.show();
    }

    public void setWeightMetrics(boolean kg, int weight_value){
        if(kg){
            prefDWeightMetric = getContext().getResources().getString(R.string.KG);
        }
        else{
            prefDWeightMetric = getContext().getResources().getString(R.string.LBS);
        }
        prefDWeight = String.valueOf(weight_value);
        tvWeight.setText(prefDWeight + " " + prefDWeightMetric);
        setPrefString(getString(R.string.SHARED_PREF_WEIGHT_METRIC), prefDWeightMetric);
        setPrefString(getString(R.string.SHARED_PREF_WEIGHT), prefDWeight);
    }

    public void showCertificationDialog(){
        AlertDialog.Builder b = createAlertDialog(getContext(), getContext().getResources().getString(R.string.DIVE_CERT));
        final String[] types = getContext().getResources().getStringArray(R.array.DIVE_CERTIFICATIIONS);
        b.setItems(types, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                dialog.dismiss();
                tvDiveCert.setText(types[position]);
                prefDiveCert = types[position];
                setPrefString(getString(R.string.SHARED_PREF_DIVE_CERT), prefDiveCert);
                AlertDialog.Builder b = createAlertDialog(getContext(), getContext().getResources().getString(R.string.DIVE_LEVELS));
                String[] levels = getContext().getResources().getStringArray(R.array.PADI_DIVE_LEVELS);
                if(position == 1){ //SSI is second one
                    levels = getContext().getResources().getStringArray(R.array.SSI_DIVE_LEVELS);
                }
                final String[] types = levels;
                b.setItems(types, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        tvDiveLevel.setText(types[which]);
                        prefDiveLevel = types[which];
                        setPrefString(getString(R.string.SHARED_PREF_DIVE_LEVEL), prefDiveLevel);
                    }
                });
                b.show();
            }
        });
        b.show();
    }

    public AlertDialog.Builder createAlertDialog(Context c, String title){
        AlertDialog.Builder b = new AlertDialog.Builder(c);
        b.setTitle(title);
        return b;
    }

    public void setPrefString(String prefKey, String prefValue){
        mSettingEditior.putString(prefKey, prefValue);
        mSettingEditior.commit();
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
