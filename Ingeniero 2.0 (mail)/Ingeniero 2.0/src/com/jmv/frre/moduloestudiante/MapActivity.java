package com.jmv.frre.moduloestudiante;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jmv.frre.moduloestudiante.R;
import com.swacorp.oncallpager.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

public class MapActivity extends FragmentActivity {

	private GoogleMap mMap;

	public static void showHome(Context context) {
		Intent intent = new Intent(context, MapActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		mMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.the_map)).getMap();
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mMap.setMyLocationEnabled(true);

		final LatLngBounds.Builder builder = new LatLngBounds.Builder();
		
		final LatLng pos = new LatLng(-27.450944, -58.979340);
		builder.include(pos);
		builder.include(new LatLng(-27.4504146,-58.9846012));
		builder.include(new LatLng(-27.4502147,-58.9740011));
		mMap.addMarker(new MarkerOptions()
				.position(pos)
				.title("UTN Chaco")
				.snippet("Edificio Central")
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.utn_logo)));
		
		final LatLng pos2 = new LatLng(-27.448021, -58.975709);
		builder.include(pos2);
		mMap.addMarker(new MarkerOptions()
		.position(pos2)
		.title("Anexo UTN Chaco")
		.snippet("Anexo - Intecnor - Laboratorios")
		.icon(BitmapDescriptorFactory.fromResource(R.drawable.utn_logo)));

		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos , 14.0f) );

        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        if (provider!=null){
        	Location location = locationManager.getLastKnownLocation(provider);

            if(location!=null){
    	        // Getting latitude of the current location
    	        double latitude = location.getLatitude();
    	
    	        // Getting longitude of the current location
    	        double longitude = location.getLongitude();
    	
    	        LatLng myPosition = new LatLng(latitude, longitude);
    	        builder.include(myPosition);
    	        
            }
        }
        
		mMap.setOnCameraChangeListener(new OnCameraChangeListener() {
            
            public void onCameraChange(CameraPosition arg0) {
            	mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50));
            	mMap.setOnCameraChangeListener(null);
            }
        });

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

}
