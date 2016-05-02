package perklun.divebox.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

import perklun.divebox.R;
import perklun.divebox.adapters.DiveRecyclerViewAdapter;
import perklun.divebox.db.DiveBoxDatabaseHelper;
import perklun.divebox.models.Dive;

//http://stackoverflow.com/questions/19353255/how-to-put-google-maps-v2-on-a-fragment-using-viewpager
public class MapFrag extends Fragment {
    private MapView mapFragMapView;
    private GoogleMap googleMap;
    DiveRecyclerViewAdapter diveRecyclerViewAdapter;
    DiveBoxDatabaseHelper dbHelper;
    List<Dive> divesList;
    HashMap<Integer, Marker> mapMarkers;
    private LatLng position;

    // newInstance constructor for creating fragment with arguments
    public static MapFrag newInstance() {
        MapFrag fragmentFirst = new MapFrag();
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = DiveBoxDatabaseHelper.getDbInstance(this.getContext());
        divesList = dbHelper.getAllDives();
        diveRecyclerViewAdapter = new DiveRecyclerViewAdapter(divesList);
        mapMarkers = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate and return the layout
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mapFragMapView = (MapView) v.findViewById(R.id.mapView);
        mapFragMapView.onCreate(savedInstanceState);
        mapFragMapView.onResume();// needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            Toast.makeText(this.getContext(), R.string.MAP_UNAVAILABLE, Toast.LENGTH_SHORT).show();
        }
        googleMap = mapFragMapView.getMap();
        mapFragMapView.onResume();// needed to get the map to display immediately
        createMarkers();
        moveCameraToCurrentPosition();
        return v;
    }

    public void createMarkers() {
        for (Dive dive : divesList) {
            addMarker(dive.getLatLng(), dive.getTitle(), dive.getId());
        }
    }

    public void moveCameraToCurrentPosition() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), R.string.MAP_PERMISSION_UNAVAILABLE, Toast.LENGTH_SHORT).show();
            return;
        }
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            position = new LatLng(latitude, longitude);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(position).zoom(5).build();
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        else{
            Toast.makeText(getContext(), R.string.LOCATION_UNAVAILABLE, Toast.LENGTH_SHORT).show();
        }
    }

    public void addMarker(LatLng position, String title, int id){
        // create marker
        MarkerOptions marker = new MarkerOptions().position(
                position).title(title);
        marker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        // adding marker
        mapMarkers.put(id, googleMap.addMarker(marker));
    }

    public void addDiveUpdateRecyclerViewAdapter(Dive dive){
        divesList.add(dive);
        addMarker(dive.getLatLng(), dive.getTitle(), dive.getId());
        diveRecyclerViewAdapter.notifyItemInserted(divesList.size()-1);
    }

    public void deleteDiveUpdateRecyclerViewAdapter(Dive dive){
        int divePos = 0;
        //TODO: Hackyway to keep find index as id != to index in list
        for(divePos = 0; divePos < divesList.size(); divePos++){
            if(divesList.get(divePos).getId() == dive.getId()){
                break;
            }
        }
        if(divePos >= 0){
            divesList.remove(divePos);
            removeMarker(dive.getId());
            diveRecyclerViewAdapter.notifyItemRemoved(divePos);
        }
    }

    public void removeMarker(int id){
        mapMarkers.get(id).remove();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapFragMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapFragMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapFragMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapFragMapView.onLowMemory();
    }
}
