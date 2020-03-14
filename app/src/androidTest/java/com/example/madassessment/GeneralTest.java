package com.example.madassessment;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import com.example.madassessment.dataEntities.PointOfInterestEntity;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class GeneralTest {
    private static final String TAG = "ExampleTestClass";

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.madassessment", appContext.getPackageName());
    }

    @Test
    public void addPointOfInterestTest() {
        Log.d(TAG, "start of addPointOfInterestTest()");
        PointOfInterestEntity pointOfInterestRecord = new PointOfInterestEntity("Test name", "Test type", 250.0, Constants.DEFAULT_LAT, Constants.DEFAULT_LON);
        ArrayList<PointOfInterestEntity> storesPointsOfInterest = new ArrayList<>();

        assertTrue(storesPointsOfInterest.add(pointOfInterestRecord));
        assertEquals(1, storesPointsOfInterest.size());
        Log.d(TAG, "added " + storesPointsOfInterest.size() + " record to the POI array");

        assertEquals("Test name", storesPointsOfInterest.get(0).getName());
        assertEquals("Test type", storesPointsOfInterest.get(0).getType());

        Log.d(TAG, "end of addPointOfInterestTest()");
    }
}
