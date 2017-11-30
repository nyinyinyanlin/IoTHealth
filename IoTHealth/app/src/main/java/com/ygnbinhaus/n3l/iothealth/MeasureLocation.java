package com.ygnbinhaus.n3l.iothealth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MeasureLocation extends FragmentActivity implements OnMapReadyCallback, OnMapClickListener, OnMapLongClickListener, OnMarkerDragListener {

    private GoogleMap mMap;
    private Snackbar snackbar;
    private Marker marker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_location);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.userMap);
        mapFragment.getMapAsync(this);
        snackbar = Snackbar.make(findViewById(R.id.measureLocationCoordinatorView),"Select this location?",Snackbar.LENGTH_INDEFINITE).setAction("SELECT", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent measureIntent = new Intent(getApplicationContext(),Measure.class);
                measureIntent.putExtra("EXTRA_LATITUDE",marker.getPosition().latitude);
                measureIntent.putExtra("EXTRA_LONGITUDE",marker.getPosition().longitude);
                Toast.makeText(getApplicationContext(),String.valueOf(marker.getPosition().latitude)+":"+String.valueOf(marker.getPosition().longitude),Toast.LENGTH_SHORT).show();
                startActivity(measureIntent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();
        marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)).draggable(true).title("Where you are?"));
        if(!snackbar.isShown()) {
            snackbar.show();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear();
        snackbar.dismiss();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker draggedMarker) {
        if(!snackbar.isShown()) {
            snackbar.show();
        }
        marker.setPosition(draggedMarker.getPosition());
    }
}
