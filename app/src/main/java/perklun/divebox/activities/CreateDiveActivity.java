package perklun.divebox.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import perklun.divebox.R;
import perklun.divebox.db.DiveBoxDatabaseHelper;
import perklun.divebox.models.Dive;
import perklun.divebox.models.User;
import perklun.divebox.utils.Constants;

public class CreateDiveActivity extends AppCompatActivity implements GoogleMap.OnMapLongClickListener {

    DiveBoxDatabaseHelper dbHelper;
    Button createDiveBtn;
    ImageButton selectTimeButton;
    ImageButton selectAirButton;
    ImageButton selectDateButton;
    private MapView createMapView;
    private GoogleMap googleMap;
    private LatLng position;
    private Marker mapMarker;
    private BitmapDescriptor defaultMarker;
    private SharedPreferences mSettings;
    EditText diveTitleEditText;
    EditText diveTitleCommentText;
    TextView tvCreateTimeInValue;
    TextView tvCreateTimeOutValue;
    TextView tvCreateAirInValue;
    TextView tvCreateAirOutValue;
    TextView tvCreateDateValue;
    TextView tvCreateBtmTimeValue;

    HashMap<TextView, String> textViewToStringKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_dive);
        loadMap(savedInstanceState);
        // Shared Preferences
        mSettings = getSharedPreferences(getString(R.string.SHARED_PREFERENCE_FILE_KEY), Context.MODE_PRIVATE);
        textViewToStringKey = new HashMap<>();
        loadTextView(savedInstanceState);
        setUpButtons();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        // Create the marker on the fragment
        position = latLng;
        mapMarker.remove();
        mapMarker = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(defaultMarker));
        dropPinEffect(mapMarker);
    }

    private void dropPinEffect(final Marker marker) {
        // Handler allows us to repeat a code block after a specified delay
        final android.os.Handler handler = new android.os.Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;
        // Use the bounce interpolator
        final android.view.animation.Interpolator interpolator =
                new BounceInterpolator();
        // Animate marker with a bounce updating its position every 15ms
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                // Calculate t for bounce based on elapsed time
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);
                // Set the anchor
                marker.setAnchor(0.5f, 1.0f + 14 * t);
                if (t > 0.0) {
                    // Post this event again 15ms from now.
                    handler.postDelayed(this, 15);
                } else { // done elapsing, show window
                    marker.showInfoWindow();
                }
            }
        });
    }

    public void loadTextView(Bundle savedInstanceState){
        diveTitleEditText = (EditText) findViewById(R.id.et_create_input_title);
        diveTitleCommentText = (EditText) findViewById(R.id.et_create_comments_value);
        tvCreateTimeInValue = (TextView)findViewById(R.id.tv_create_time_in_value);
        tvCreateTimeOutValue = (TextView)findViewById(R.id.tv_create_time_out_value);
        tvCreateAirInValue = (TextView) findViewById(R.id.tv_create_air_in_value);
        tvCreateAirOutValue = (TextView) findViewById(R.id.tv_create_air_out_value);
        tvCreateDateValue = (TextView) findViewById(R.id.tv_create_date_value);
        tvCreateBtmTimeValue = (TextView) findViewById(R.id.tv_create_btm_time_value);
        //Setup hashmap
        textViewToStringKey.put(tvCreateTimeInValue, getString(R.string.create_dive_time_in_key));
        textViewToStringKey.put(tvCreateTimeOutValue, getString(R.string.create_dive_time_out_key));
        textViewToStringKey.put(tvCreateAirInValue, getString(R.string.create_dive_air_in_key));
        textViewToStringKey.put(tvCreateAirOutValue, getString(R.string.create_dive_air_out_key));
        textViewToStringKey.put(tvCreateDateValue, getString(R.string.create_dive_date_key));
        textViewToStringKey.put(tvCreateBtmTimeValue, getString(R.string.create_dive_btm_time_key));
        //load saved values to handle rotation;
        if(savedInstanceState != null){
            String prevInput;
            for(TextView tv : textViewToStringKey.keySet()){
                prevInput = savedInstanceState.getString(textViewToStringKey.get(tv));
                if(prevInput != null){
                    tv.setText(prevInput);
                }
            }
            prevInput = savedInstanceState.getString(getString(R.string.create_dive_title_key));
            if(prevInput != null){
                diveTitleEditText.setText(prevInput);
            }
            prevInput = savedInstanceState.getString(getString(R.string.create_dive_comment_key));
            if(prevInput != null){
                diveTitleCommentText.setText(prevInput);
            }
        }
    }

    private void loadMap(Bundle savedInstanceState){
        //Map
        createMapView = (MapView) findViewById(R.id.create_mapview);
        createMapView.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(this.getApplicationContext());
        } catch (Exception e) {
            Toast.makeText(this, R.string.MAP_UNAVAILABLE, Toast.LENGTH_SHORT).show();
        }
        googleMap = createMapView.getMap();
        double lat = Constants.INVALID_LAT;
        double lng = Constants.INVALID_LONG;
        if(savedInstanceState != null){
            lat = savedInstanceState.getDouble(getString(R.string.create_dive_lat), Constants.INVALID_LAT);
            lng = savedInstanceState.getDouble(getString(R.string.create_dive_lng), Constants.INVALID_LONG);
        }
        position = new LatLng(lat, lng);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.MAP_PERMISSION_UNAVAILABLE, Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMapLongClickListener(this);
            // Get current latitude and longitude
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(bestProvider);
            defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
            if (location != null) {
                if(lat == Constants.INVALID_LAT || lng == Constants.INVALID_LONG){
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    position = new LatLng(latitude, longitude);
                }
                mapMarker = googleMap.addMarker(new MarkerOptions()
                        .position(position)
                        .icon(defaultMarker));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(position).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));
            }
            else{
                Toast.makeText(this, R.string.LOCATION_UNAVAILABLE, Toast.LENGTH_SHORT).show();
            }
        }
        createMapView.onResume();// needed to get the map to display immediately
    }

    public void setUpButtons(){
        createDiveBtn = (Button)findViewById(R.id.btn_create_dive_submit);
        createDiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mSettings.getString(getString(R.string.SHARED_PREF_USERNAME_KEY),"");
                String googleID = mSettings.getString(getString(R.string.SHARED_PREF_GOOGLE_ID_KEY),"");
                Dive dive = new Dive(new User(username, googleID), diveTitleEditText.getText().toString(), position);
                dive.setDate(tvCreateDateValue.getText().toString());
                dive.setComments(diveTitleCommentText.getText().toString());
                dive.setAirIn(tvCreateAirInValue.getText().toString());
                dive.setAirOut(tvCreateAirOutValue.getText().toString());
                dive.setTimeIn(tvCreateTimeInValue.getText().toString());
                dive.setTimeOut(tvCreateTimeOutValue.getText().toString());
                dive.setBtmTime(tvCreateBtmTimeValue.getText().toString());
                dbHelper = DiveBoxDatabaseHelper.getDbInstance(getApplicationContext());
                int resultCode = dbHelper.addDive(dive);
                Intent i = new Intent();
                i.putExtra(Constants.DIVE,dive);
                setResult(resultCode, i);
                finish();
            }
        });
        selectTimeButton = (ImageButton)findViewById(R.id.ibtn_create_select_time_btn);
        selectTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeInPickerDialog();
            }
        });
        selectAirButton = (ImageButton) findViewById(R.id.ibtn_create_select_air_btn);
        selectAirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAirInNumberPickerDialog();
            }
        });

        selectDateButton = (ImageButton) findViewById(R.id.ibtn_create_select_date);
        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    public void showDatePickerDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.setTitle(getResources().getString(R.string.SELECT_DATE));
        dialog.setContentView(R.layout.date_picker_create);
        Button btnSetTime = (Button) dialog.findViewById(R.id.btn_create_select_date);
        final DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.dp_create_select_date);
        btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCreateDateValue.setText(formatDate(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth()));
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public String formatDate(int year, int month, int day){
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d yyyy");
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, 0, 0);
        return sdf.format(cal.getTime());
    }

    public void showTimeInPickerDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.setTitle(getResources().getString(R.string.TIME_IN));
        dialog.setContentView(R.layout.time_picker);
        Button btnSetTime = (Button) dialog.findViewById(R.id.btn_create_set_time);
        final TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.tp_create_dive_time);
        btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCreateTimeInValue.setText(formatTime(timePicker.getCurrentHour(), timePicker.getCurrentMinute()));
                dialog.dismiss();
                showTimeOutPickerDialog(tvCreateTimeInValue.getText().toString());
            }
        });
        dialog.show();
    }

    public String formatTime(int hour, int minute){
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        return sdf.format(cal.getTime());
    }

    public void showTimeOutPickerDialog(final String timeIn){
        final Dialog dialog = new Dialog(this);
        dialog.setTitle(getResources().getString(R.string.TIME_OUT));
        dialog.setContentView(R.layout.time_picker);
        Button btnSetTime = (Button) dialog.findViewById(R.id.btn_create_set_time);
        final TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.tp_create_dive_time);
        btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCreateTimeOutValue.setText(formatTime(timePicker.getCurrentHour(), timePicker.getCurrentMinute()));
                String btmTime = calculatebottomTime(timeIn, tvCreateTimeOutValue.getText().toString());
                if(btmTime.equals(Constants.INVALID_TIME)){
                    Toast.makeText(getApplicationContext(), getString(R.string.TIME_ERROR), Toast.LENGTH_SHORT).show();
                }
                else{
                    tvCreateBtmTimeValue.setText(calculatebottomTime(timeIn, tvCreateTimeOutValue.getText().toString()));
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    public String calculatebottomTime(String timeIn, String timeOut){
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        String result = new String();
        try{
            Date dIn = sdf.parse(timeIn);
            Date dOut = sdf.parse(timeOut);
            long diff = dOut.getTime() - dIn.getTime();
            int diffMinutes = (int) diff / (60 * 1000) % 60;
            int diffHours = (int) diff / (60 * 60 * 1000) % 24;
            String hr = " hr";
            String min = " min";
            if(diffMinutes < 0 || diffHours < 0){
                result = Constants.INVALID_TIME;
            }
            else{
                if(diffMinutes > 1){
                    min = " mins";
                }
                if(diffHours > 0){
                    if(diffHours > 1){
                        hr = " hrs ";
                    }
                    result = diffHours + hr;
                    if(diffMinutes > 0) {
                        result += " " + diffMinutes + min;
                    }
                }
                else{
                    result = diffMinutes + min;
                }
            }
        }
        catch (ParseException e){
            Log.d("Date:", "Parse exception " +  timeIn + " " + timeOut + " " + e.toString());
        }
        return result;
    }

    public void showAirInNumberPickerDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.setTitle(getResources().getString(R.string.AIR_IN));
        dialog.setContentView(R.layout.number_picker_air);
        Button btnSetAirInBar = (Button) dialog.findViewById(R.id.btn_select_bar);
        Button btnSetAirInPSI = (Button) dialog.findViewById(R.id.btn_select_psi);
        final NumberPicker np = (NumberPicker) dialog.findViewById(R.id.np_air_value);
        np.setMaxValue(250); // max value 20
        np.setMinValue(100);   // min value 0
        np.setValue(200);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //Do nothing
            }
        });
        btnSetAirInBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCreateAirInValue.setText(np.getValue() + " "+ getResources().getString(R.string.bar));
                dialog.dismiss();
                showAirOutNumberPickerDialog();
            }
        });
        btnSetAirInPSI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCreateAirInValue.setText(np.getValue() + " "+ getResources().getString(R.string.psi));
                dialog.dismiss(); // dismiss the dialog
                showAirOutNumberPickerDialog();
            }
        });
        dialog.show();
    }

    public void showAirOutNumberPickerDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.setTitle(getResources().getString(R.string.AIR_OUT));
        dialog.setContentView(R.layout.number_picker_air);
        Button btnSetAirInBar = (Button) dialog.findViewById(R.id.btn_select_bar);
        Button btnSetAirInPSI = (Button) dialog.findViewById(R.id.btn_select_psi);
        final NumberPicker np = (NumberPicker) dialog.findViewById(R.id.np_air_value);
        np.setMaxValue(250); // max value 20
        np.setMinValue(0);   // min value 0
        np.setValue(50);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //Do nothing
            }
        });
        btnSetAirInBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCreateAirOutValue.setText(np.getValue() + " "+ getResources().getString(R.string.bar));
                dialog.dismiss();
            }
        });
        btnSetAirInPSI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCreateAirOutValue.setText(np.getValue() + " "+ getResources().getString(R.string.psi));
                dialog.dismiss(); // dismiss the dialog
            }
        });
        dialog.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.create_dive_title_key), diveTitleEditText.getText().toString());
        outState.putString(getString(R.string.create_dive_comment_key), diveTitleCommentText.getText().toString());
        for(TextView tv : textViewToStringKey.keySet()){
            outState.putString(textViewToStringKey.get(tv), tv.getText().toString());
        }
        //pass marker position
        outState.putDouble(getString(R.string.create_dive_lat), position.latitude);
        outState.putDouble(getString(R.string.create_dive_lng), position.longitude);
    }
}
