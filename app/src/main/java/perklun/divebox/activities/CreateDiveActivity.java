package perklun.divebox.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
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

import perklun.divebox.R;
import perklun.divebox.db.DiveBoxDatabaseHelper;
import perklun.divebox.models.Dive;
import perklun.divebox.models.User;
import perklun.divebox.utils.Constants;

public class CreateDiveActivity extends AppCompatActivity implements GoogleMap.OnMapLongClickListener {

    DiveBoxDatabaseHelper dbHelper;
    Button createDiveBtn;
    EditText diveTitleEditText;
    private MapView createMapView;
    private GoogleMap googleMap;
    private LatLng position;
    private Marker mapMarker;
    private BitmapDescriptor defaultMarker;
    private SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_dive);
        //Map
        createMapView = (MapView) findViewById(R.id.create_mapview);
        createMapView.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(this.getApplicationContext());
        } catch (Exception e) {
            Toast.makeText(this, R.string.MAP_UNAVAILABLE, Toast.LENGTH_SHORT).show();
        }
        googleMap = createMapView.getMap();
        position = new LatLng(Constants.INVALID_LAT, Constants.INVALID_LONG);
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
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                position = new LatLng(latitude, longitude);
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
        // Shared Preferences
        mSettings = getSharedPreferences(getString(R.string.SHARED_PREFERENCE_FILE_KEY), Context.MODE_PRIVATE);
        diveTitleEditText = (EditText) findViewById(R.id.et_input_title);
        createDiveBtn = (Button)findViewById(R.id.btn_create_dive_submit);
        createDiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mSettings.getString(getString(R.string.SHARED_PREF_USERNAME_KEY),"");
                Dive dive = new Dive(new User(username), diveTitleEditText.getText().toString(), position);
                dbHelper = DiveBoxDatabaseHelper.getDbInstance(getApplicationContext());
                int resultCode = dbHelper.addDive(dive);
                Intent i = new Intent();
                i.putExtra(Constants.DIVE,dive);
                setResult(resultCode, i);
                //TODO: May not call finish, depends on what addDive returns
                finish();
            }
        });
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        // Create the marker on the fragment
        position = latLng;
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
}
