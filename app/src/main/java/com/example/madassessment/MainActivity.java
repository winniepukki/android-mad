package com.example.madassessment;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.location.LocationManager;
import android.location.LocationListener;
import android.location.Location;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.example.madassessment.dao.PointOfInterestDAO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements LocationListener {

    private Double latitude = Constants.DEFAULT_LAT;
    private Double longitude = Constants.DEFAULT_LON;
    private Double zoom = Constants.DEFAULT_ZOOM;
    private static final String TAG = "MainActivity";

    ArrayList<PointOfInterestDAO> storesPointsOfInterest;
    MapView mv;
    ItemizedIconOverlay<OverlayItem> items;
    ItemizedIconOverlay.OnItemGestureListener<OverlayItem> markerGestureListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        storesPointsOfInterest = new ArrayList<PointOfInterestDAO>();

        /*
        *   Performing all initial checks and
        *   setting the appropriate values
        */

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "Please enable the LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();
        }
        else {
            // REQUEST PERMISSION......
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

        markerGestureListener = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            public boolean onItemLongPress(int i, OverlayItem item) {
                Toast.makeText(MainActivity.this, item.getSnippet(), Toast.LENGTH_SHORT).show();
                return true;
            }

            public boolean onItemSingleTapUp(int i, OverlayItem item) {
                Toast.makeText(MainActivity.this, item.getSnippet(), Toast.LENGTH_SHORT).show();
                return true;
            }
        };

        items = new ItemizedIconOverlay<OverlayItem>(this, new ArrayList<OverlayItem>(), markerGestureListener);
        OverlayItem randomLocation = new OverlayItem("Random location", "Bad place to stay as it smells", new GeoPoint(latitude, longitude));

        randomLocation.setMarker(getResources().getDrawable(R.drawable.marker_default));

        //items.addItem(randomLocation);
        mv.getOverlays().add(items);
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
        if (item.getItemId() == R.id.setprefs) {
            Intent intent = new Intent(this, Preferences.class);
            startActivityForResult(intent, 0);
            return true;
        }
        else if (item.getItemId() == R.id.addplace) {
            Intent intent = new Intent(this, AddPlaceToStay.class);
            startActivityForResult(intent, 1);
            return true;
        }
        else if (item.getItemId() == R.id.saveplaces) {
            PrintWriter printWriter = null;

            if(storesPointsOfInterest.size() <= 0) {
                popupMessage("There is nothing to save! Add some Points of Interest");
                return false;
            }

            try {
                boolean recordFileExists = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/records.csv").isFile();

                if(recordFileExists) {
                    new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/records.csv").delete();
                }

                File recordsFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/records.csv");
                printWriter = new PrintWriter(recordsFile);

                for (int i = 0; i < storesPointsOfInterest.size(); i++) {
                    String magicString = storesPointsOfInterest.get(i).toString();
                    printWriter.println(magicString);
                }
                popupMessage("Saved " + storesPointsOfInterest.size() + " Points of Interest to the local storage!");
            }
            catch (FileNotFoundException e) {
                popupMessage("Error: " + e.getMessage() + " error has occurred.");
                e.printStackTrace();
            }
            finally {
                printWriter.close();
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Set preferences request code event handler
        if (requestCode == 0) { }
        // Set add POI request code event handler
        else if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Bundle extras = intent.getExtras();

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                boolean poiAutoSave = prefs.getBoolean("autosavepoi", false);

                String getPoiName = extras.getString("com.example.poiname");
                String getPoiType = extras.getString("com.example.poitype");
                Double getPoiPrice = extras.getDouble("com.example.poiprice");

                OverlayItem someLocation = new OverlayItem(getPoiName, getPoiName + " " + getPoiType + " " + getPoiPrice, new GeoPoint(latitude, longitude));
                someLocation.setMarker(getResources().getDrawable(R.drawable.marker_default));

                items.addItem(someLocation);
                mv.getOverlays().add(items);

                if(poiAutoSave) {
                    try {
                        PointOfInterestDAO pointOfInterest = new PointOfInterestDAO(getPoiName, getPoiType, getPoiPrice);
                        storesPointsOfInterest.add(pointOfInterest);
                    }
                    catch (Exception e) {
                        popupMessage("Error: " + e.getMessage() + " error has occurred.");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /*
     *   Handling changes in location preferences
     */
    public void onLocationChanged(Location newLoc) {
        //Toast.makeText(this, "Location=" + newLoc.getLatitude() + " " + newLoc.getLongitude(), Toast.LENGTH_LONG).show();
        //mv.getController().setCenter(new GeoPoint(newLoc.getLatitude(), newLoc.getLongitude()));
        //Log.d(TAG, newLoc.getLatitude() + " " + newLoc.getLongitude());
    }

    public void onProviderDisabled(String provider) {
        //Toast.makeText(this, "Provider " + provider + " disabled", Toast.LENGTH_LONG).show();
    }

    public void onProviderEnabled(String provider) {
        //Toast.makeText(this, "Provider " + provider + " enabled", Toast.LENGTH_LONG).show();
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        //Toast.makeText(this, "Status changed: " + status, Toast.LENGTH_LONG).show();
    }

    private void popupMessage(String message) {
        new AlertDialog.Builder(this).setPositiveButton("OK", null).setMessage(message).show();
    }
}

