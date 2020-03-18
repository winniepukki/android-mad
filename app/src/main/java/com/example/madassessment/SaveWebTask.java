package com.example.madassessment;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SaveWebTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = "MainActivity";

    @Override
    protected String doInBackground(Void... voids) {
        HttpURLConnection httpURLConnection = null;

        try {
            URL connectionUrl = new URL("https://www.hikar.org/course/ws/add.php");
            httpURLConnection = (HttpURLConnection) connectionUrl.openConnection();

            // Replace all test values with actual values
            String postData = "username=user002&name=test&type=test&price=test&lat=test&lon=test&year=20";
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setFixedLengthStreamingMode(postData.length());

            OutputStream outputStream = null;
            outputStream = httpURLConnection.getOutputStream();
            outputStream.write(postData.getBytes());

            if (httpURLConnection.getResponseCode() == 200) {
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String allRecords = "", magicLine;

                while ((magicLine = bufferedReader.readLine()) != null) {
                    allRecords += magicLine;
                    return allRecords;
                }
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
        return "HTTP ERROR:";
    }

    @Override
    public void onPostExecute(String result) {
        Log.i(TAG, " " + result);
    }
}
