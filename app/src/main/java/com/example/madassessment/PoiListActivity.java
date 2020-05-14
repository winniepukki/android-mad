package com.example.madassessment;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.madassessment.dataEntities.PointOfInterestEntity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class PoiListActivity extends ListActivity {

    private final String TAG = this.getClass().getSimpleName();
    ArrayList<String> magicEntities;
    ArrayList<String> magicDescriptions;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<PointOfInterestEntity> storesEntities = new ArrayList<>();
        magicEntities = new ArrayList<>();
        magicDescriptions = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory().getAbsolutePath() + "/records.csv"));
            String magicLine = "";

            while ((magicLine = reader.readLine()) != null) {
                String components[] = magicLine.split(",");
                PointOfInterestEntity pointOfInterestEntity = new PointOfInterestEntity(components[0], components[1], Double.parseDouble(components[2]), Double.parseDouble(components[3]), Double.parseDouble(components[4]));
                storesEntities.add(pointOfInterestEntity);
            }

            for (int i = 0; i < storesEntities.size(); i++) {
                String getPoiName = storesEntities.get(i).getName();
                String getPoiType = storesEntities.get(i).getType();

                Double getPoiPrice = storesEntities.get(i).getPrice();

                magicEntities.add(getPoiName);
                magicDescriptions.add(getPoiType + " " + getPoiPrice);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        MyAdapter adapter = new MyAdapter();
        setListAdapter(adapter);
    }

    public void onListItemClick(ListView lv, View view, int index, long id) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        try {
            bundle.putInt("com.example.poiindex", index);
            intent.putExtras(bundle);

            setResult(RESULT_OK, intent);
            finish();
        }
        catch (Exception e) {
            Log.v(TAG, e.getMessage());
        }
    }

    public class MyAdapter extends ArrayAdapter<String> {
        public MyAdapter() {
            super(PoiListActivity.this, android.R.layout.simple_list_item_1, magicEntities);
        }

        public View getView(int index, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.poientry, parent, false);

            TextView title = (TextView) view.findViewById(R.id.poi_name),
                     detail = (TextView) view.findViewById(R.id.poi_desc);

            title.setText(magicEntities.get(index));
            detail.setText(magicDescriptions.get(index));

            return view;
        }
    }
}
