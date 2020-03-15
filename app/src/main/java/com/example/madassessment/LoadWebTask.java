package com.example.madassessment;

import android.support.v7.app.AlertDialog;
import android.os.AsyncTask;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class LoadWebTask extends AsyncTask<Void, Void, String> {

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
                    magicString += magicLine;
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