package com.example.madassessment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.location.LocationManager;
import android.location.LocationListener;
import android.location.Location;
import android.content.Context;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private Double latitude = Constants.DEFAULT_LAT;
    private Double longitude = Constants.DEFAULT_LON;
    private Double zoom = Constants.DEFAULT_ZOOM;

    MapView mv;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        *   Performing all initial checks and
        *   setting the appropriate values
        */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "Please enable the LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();
        }

        LocationManager mgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);

        Location location = mgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        // This line sets the user agent, a requirement to download OSM maps
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        mv = findViewById(R.id.main_map);
        mv.setMultiTouchControls(true);
        mv.getController().setZoom(zoom);
        mv.getController().setCenter(new GeoPoint(latitude, longitude));
    }

    /*
     *   Handling changes on menu selection
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addplace) {
            Intent intent = new Intent(this, AddPlaceToStay.class);
            startActivityForResult(intent, 0);
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            // TO-BE IMPLEMENTED...
        }
    }

    /*
     *   Handling changes in location preferences
     */
    public void onLocationChanged(Location newLoc) {
        Toast.makeText(this, "Location=" + newLoc.getLatitude() + " " + newLoc.getLongitude(), Toast.LENGTH_LONG).show();
        mv.getController().setCenter(new GeoPoint(newLoc.getLatitude(), newLoc.getLongitude()));
    }

    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Provider " + provider + " disabled", Toast.LENGTH_LONG).show();
    }

    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Provider " + provider + " enabled", Toast.LENGTH_LONG).show();
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(this, "Status changed: " + status, Toast.LENGTH_LONG).show();
    }

}

