package ds.aueb.gr.androidclient;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

import gr.aueb.ds.domain.POI;
import gr.aueb.ds.domain.UserRequest;
import gr.aueb.ds.domain.UserResponse;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;

    private UserRequest userRequest;
    private UserResponse userResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to go to the previous page?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert1 = builder.create();
        alert1.show();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        userRequest = (UserRequest) getIntent().getExtras().get("userRequest");
        userResponse = (UserResponse) getIntent().getExtras().get("userResponse");

        fillMap();

        mMap.getUiSettings().setMapToolbarEnabled(true);
        LatLng currentLocation = new LatLng(userRequest.lat, userRequest.lon);
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

        Circle circle = mMap.addCircle(new CircleOptions().center(currentLocation).radius(userRequest.range * 1000).strokeColor(Color.RED));
        circle.setVisible(true);
        getZoomLevel(circle);

        mMap.moveCamera(CameraUpdateFactory.zoomTo(getZoomLevel(circle)));
        mMap.setOnInfoWindowClickListener(this);
    }

    public int getZoomLevel(Circle circle) {
        int zoomLevel = 0;
        if (circle != null) {
            double radius = circle.getRadius();
            double scale = radius / 500;
            zoomLevel = (int) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }

    protected void fillMap() {
        HashMap<Integer, POI> pois = userResponse.poiInfo;
        if (pois != null) {
            if (mMap != null) mMap.clear();
            int rank = 1;
            for (Map.Entry<Integer, POI> entry : pois.entrySet()) {

                Integer id = entry.getKey();
                POI poi = entry.getValue();

                LatLng pos = new LatLng(poi.latidude, poi.longitude);
                String title = poi.POI_name;
                String category = poi.POI_category_id;

                MarkerOptions mo = new MarkerOptions().position(pos).title(title)
                        .snippet("id: " + id + ", rank: " + rank + ", category: " + category);
                try {
                    mMap.addMarker(mo);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                rank++;
            }
        }
    }

    public void onInfoWindowClick(Marker marker) {
        HashMap<Integer, POI> pois = userResponse.poiInfo;
        LatLng markerPosition = marker.getPosition();
        for (Map.Entry<Integer, POI> entry : pois.entrySet()) {

            Integer id = entry.getKey();
            POI poi = entry.getValue();
            LatLng pos = new LatLng(poi.latidude, poi.longitude);

            if (poi.POI_name.equals(marker.getTitle()) && pos.equals(markerPosition)) {

                try {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(poi.photos));
                    startActivity(browserIntent);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Could not find photo.", Toast.LENGTH_LONG).show();

                }
                break;
            }
        }

    }

}
