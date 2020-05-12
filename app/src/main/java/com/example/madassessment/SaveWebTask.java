package com.example.madassessment;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.example.madassessment.dataEntities.PointOfInterestEntity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SaveWebTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = "MainActivity";

    @Override
    protected String doInBackground(Void... voids) {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory().getAbsolutePath() + "/records.csv"));
            String magicLine = "";

            ArrayList<PointOfInterestEntity> storesEntities = new ArrayList<>();

            while ((magicLine = reader.readLine()) != null) {
                String components[] = magicLine.split(",");
                PointOfInterestEntity pointOfInterestEntity = new PointOfInterestEntity(components[0], components[1], Double.parseDouble(components[2]), Double.parseDouble(components[3]), Double.parseDouble(components[4]));
                storesEntities.add(pointOfInterestEntity);
            }

            for (int i = 0; i < storesEntities.size(); i++) {
                HttpURLConnection httpURLConnection = null;
                try {
                    URL connectionUrl = new URL("https://www.hikar.org/course/ws/add.php");
                    httpURLConnection = (HttpURLConnection) connectionUrl.openConnection();

                    String getPoiName = storesEntities.get(i).getName();
                    String getPoiType = storesEntities.get(i).getType().trim();

                    Double getPoiPrice = storesEntities.get(i).getPrice();
                    Double getPoiLatitude = storesEntities.get(i).getLatitude();
                    Double getPoiLongitude = storesEntities.get(i).getLongitude();

                    String postData = "username=user002&name=" + getPoiName + "&type=" + getPoiType + "&price=" + getPoiPrice + "&lat=" + getPoiLatitude + "&lon=" + getPoiLongitude + "&year=20";

                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setFixedLengthStreamingMode(postData.length());

                    OutputStream outputStream = null;
                    outputStream = httpURLConnection.getOutputStream();
                    outputStream.write(postData.getBytes());

                    if (httpURLConnection.getResponseCode() == 200) {
                        InputStream inputStream = httpURLConnection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                        String allRecords = "", magicLine2;

                        while ((magicLine2 = bufferedReader.readLine()) != null) {
                            allRecords += magicLine2;
                        }
                        return allRecords;
                    }
                    else {
                        return "HTTP ERROR: " + httpURLConnection.getResponseCode();
                    }
                }
                catch (IOException e) {
                    return e.toString();
                }
                finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return "HTTP error has occurred!";
    }

    @Override
    public void onPostExecute(String result) {
        Log.i(TAG, " " + result);
    }
}
