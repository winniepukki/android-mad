package com.example.madassessment;

import android.os.AsyncTask;
import android.os.Environment;

import com.example.madassessment.dataEntities.PointOfInterestEntity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class LoadWebTask extends AsyncTask<Void, Void, String> {
    ArrayList<PointOfInterestEntity> storesPointsOfInterest = new ArrayList<PointOfInterestEntity>();

    @Override
    protected String doInBackground(Void... voids) {
        HttpURLConnection connection = null;

        try {
            URL connectionUrl = new URL("https://www.hikar.org/course/ws/get.php?year=20&username=user002&format=csv");
            connection = (HttpURLConnection) connectionUrl.openConnection();

            InputStream inputStream = connection.getInputStream();

            if (connection.getResponseCode() == 200) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String magicString = "", magicLine;

                while ((magicLine = bufferedReader.readLine()) != null) {
                    String values[] = magicLine.split(",");

                    if (values.length != 5) {
                        throw new IllegalArgumentException("Invalid string cannot be split into title, snippet, detail ,lat, lon");
                    }

                    PointOfInterestEntity pointOfInterestEntity = new PointOfInterestEntity(values[0], values[1], Double.parseDouble(values[2]), Double.parseDouble(values[3]), Double.parseDouble(values[4]));
                    storesPointsOfInterest.add(pointOfInterestEntity);
                }

                PrintWriter printWriter = null;

                try {
                    boolean recordFileExists = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/records.csv").isFile();

                    if (recordFileExists) {
                        new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/records.csv").delete();
                    }

                    File recordsFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/records.csv");
                    printWriter = new PrintWriter(recordsFile);

                    for (int i = 0; i < storesPointsOfInterest.size(); i++) {
                        String magicalString = storesPointsOfInterest.get(i).toString();
                        printWriter.println(magicalString);
                    }
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                finally {
                    printWriter.close();
                }

                return magicString;
            } else {
                return "HTTP ERROR: " + connection.getResponseCode();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return "HTTP ERROR:";
    }

    public void onPostExecute(String message) {

    }
}