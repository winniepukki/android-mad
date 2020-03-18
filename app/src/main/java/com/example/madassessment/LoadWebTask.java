package com.example.madassessment;

import android.os.AsyncTask;

import com.example.madassessment.dataEntities.PointOfInterestEntity;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class LoadWebTask extends AsyncTask<Void, Void, String> {
    ArrayList<PointOfInterestEntity> storesPointsOfInterest = new ArrayList<PointOfInterestEntity>();
    MapView mv;
    ItemizedIconOverlay<OverlayItem> items;
    ItemizedIconOverlay.OnItemGestureListener<OverlayItem> markerGestureListener;

    @Override
    protected String doInBackground(Void... voids) {
        HttpURLConnection connection = null;

        try {
            URL connectionUrl = new URL("https://www.hikar.org/course/ws/get.php?year=20&username=user002&format=csv");
            connection = (HttpURLConnection) connectionUrl.openConnection();

            InputStream inputStream = connection.getInputStream();

            if(connection.getResponseCode() == 200) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String magicString = "", magicLine;

                while((magicLine = bufferedReader.readLine()) != null) {
                    String values[] = magicLine.split(",");

                    if (values.length != 5) {
                        throw new IllegalArgumentException("invalid string cannot be split into title, snippet, detail ,lat, lon");
                    }

                    PointOfInterestEntity pointOfInterestEntity = new PointOfInterestEntity(values[0], values[1], Double.parseDouble(values[2]), Double.parseDouble(values[3]), Double.parseDouble(values[4]));
                    storesPointsOfInterest.add(pointOfInterestEntity);
                }
                return magicString;
            }
            else {
                return "HTTP ERROR: " + connection.getResponseCode();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
        return "HTTP ERROR:";
    }

    public void onPostExecute(String message) {
        String[] values = message.split(",");
    }
}