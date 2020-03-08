package com.example.madassessment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;

public class AddPlaceToStay extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
    }
}
