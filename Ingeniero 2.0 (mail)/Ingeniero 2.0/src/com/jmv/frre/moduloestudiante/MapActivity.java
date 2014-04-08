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
