package com.example.madassessment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.location.LocationManager;
import android.location.LocationListener;
import android.location.Location;
import android.content.Context;
import android.widget.Toast;

import com.example.madassessment.dataEntities.PointOfInterestEntity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements LocationListener {

    private Double latitude = Constants.DEFAULT_LAT;
    private Double longitude = Constants.DEFAULT_LON;
    private Double zoom = Constants.DEFAULT_ZOOM;
    private static final String TAG = "MainActivity";

    ItemizedIconOverlay.OnItemGestureListener<OverlayItem> markerGestureListener;
    ArrayList<PointOfInterestEntity> storesPointsOfInterest;
    ItemizedIconOverlay<OverlayItem> items;
    MapView mv;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        storesPointsOfInterest = new ArrayList<PointOfInterestEntity>();

        /*
        *   Performing all initial checks and
        *   setting the appropriate values
        */

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "Location permissions were granted", Toast.LENGTH_LONG).show();
        }
        else {
            reqestStoragePermission();
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
        mv.getOverlays().add(items);
    }

    /*
     *   Handling changes on menu selection
     */
    @Override
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
            savePoiToLocalStorage();
        }
        else if (item.getItemId() == R.id.loadplaces) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory().getAbsolutePath() + "/records.csv"));
                String magicLine = "";

                ArrayList<PointOfInterestEntity> storesEntities = new ArrayList<>();

                while((magicLine = reader.readLine()) != null) {
                    String components[] = magicLine.split(",");
                    PointOfInterestEntity pointOfInterestEntity = new PointOfInterestEntity(components[0], components[1], Double.parseDouble(components[2]), Double.parseDouble(components[3]), Double.parseDouble(components[4]));
                    storesEntities.add(pointOfInterestEntity);
                }

                for(int i = 0; i < storesEntities.size(); i++) {
                    String getPoiName = storesEntities.get(i).getName();
                    String getPoiType = storesEntities.get(i).getType();

                    Double getPoiPrice = storesEntities.get(i).getPrice();
                    Double getPoiLatitude = storesEntities.get(i).getLatitude();
                    Double getPoiLongitude = storesEntities.get(i).getLongitude();

                    OverlayItem someLocation = new OverlayItem(getPoiName, getPoiName + " " + getPoiType + " " + getPoiPrice, new GeoPoint(getPoiLatitude, getPoiLongitude));
                    someLocation.setMarker(getResources().getDrawable(R.drawable.marker_default));

                    items.addItem(someLocation);
                    mv.getOverlays().add(items);
                }
                Log.v(TAG, String.format("%d", storesEntities.size()));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (item.getItemId() == R.id.loadweb) {
            LoadWebTask loadWebTask = new LoadWebTask();
            loadWebTask.execute();
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

                try {
                    PointOfInterestEntity pointOfInterest = new PointOfInterestEntity(getPoiName, getPoiType, getPoiPrice, latitude, longitude);
                    storesPointsOfInterest.add(pointOfInterest);

                    if(poiAutoSave) {
                        savePoiToLocalStorage();
                    }
                }
                catch (Exception e) {
                    popupMessage("Error: " + e.getMessage() + " error has occurred.");
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     *   Handling changes in location preferences
     */
    @Override
    public void onLocationChanged(Location newLoc) {
        //Toast.makeText(this, "Location=" + newLoc.getLatitude() + " " + newLoc.getLongitude(), Toast.LENGTH_LONG).show();
        //mv.getController().setCenter(new GeoPoint(newLoc.getLatitude(), newLoc.getLongitude()));
        //Log.d(TAG, newLoc.getLatitude() + " " + newLoc.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        //Toast.makeText(this, "Provider " + provider + " disabled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        //Toast.makeText(this, "Provider " + provider + " enabled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //Toast.makeText(this, "Status changed: " + status, Toast.LENGTH_LONG).show();
    }

    private void popupMessage(String message) {
        new AlertDialog.Builder(this).setPositiveButton("OK", null).setMessage(message).show();
    }

    private boolean savePoiToLocalStorage() {
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
            return true;
        }
    }

    private void reqestStoragePermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission is required")
                    .setMessage("This permission is required for the location accuracy!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, Constants.STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        }
        else {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, Constants.STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == Constants.STORAGE_PERMISSION_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions have been granted!", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Permissions have been denied!", Toast.LENGTH_LONG).show();
            }
        }
    }
}