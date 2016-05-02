package perklun.divebox.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

import org.w3c.dom.Text;

import perklun.divebox.R;
import perklun.divebox.db.DiveBoxDatabaseHelper;
import perklun.divebox.models.Dive;
import perklun.divebox.utils.Constants;

public class ViewDiveActivity extends AppCompatActivity {

    DiveBoxDatabaseHelper dbHelper;
    private Dive dive;
    private MapView createMapView;
    private GoogleMap googleMap;
    private BitmapDescriptor defaultMarker;
    private Marker mapMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_dive);
        dive = getIntent().getParcelableExtra(Constants.DIVE);
        TextView tvDiveDetailTitle = (TextView)findViewById(R.id.tv_dive_detail_title);
        tvDiveDetailTitle.setText(dive.getTitle());
        //Load Map
        createMapView = (MapView) findViewById(R.id.view_mapview);
        createMapView.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(this.getApplicationContext());
        } catch (Exception e) {
            Toast.makeText(this, R.string.MAP_UNAVAILABLE, Toast.LENGTH_SHORT).show();
        }
        googleMap = createMapView.getMap();
        if(dive.hasLatLng()){
            LatLng position = dive.getLatLng();
            defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
            mapMarker = googleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .icon(defaultMarker));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(position).zoom(15).build();
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        else{
            Toast.makeText(this, R.string.LOCATION_UNAVAILABLE, Toast.LENGTH_SHORT).show();
        }
        createMapView.onResume();// needed to get the map to display immediately
        dbHelper = DiveBoxDatabaseHelper.getDbInstance(getApplicationContext());
        Button btnDeleteDive = (Button) findViewById(R.id.btn_view_delete_dive);
        btnDeleteDive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int resultCode = dbHelper.deleteDive(dive);
                Intent i = new Intent();
                i.putExtra(Constants.DIVE, dive);
                setResult(resultCode, i);
                finish();
            }
        });
    }
}
