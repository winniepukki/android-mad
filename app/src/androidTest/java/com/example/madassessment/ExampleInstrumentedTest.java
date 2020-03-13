package com.example.madassessment;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import com.example.madassessment.dao.PointOfInterestDAO;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
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
        PointOfInterestDAO pointOfInterestRecord = new PointOfInterestDAO("Test name", "Test type", 250.0);
        ArrayList<PointOfInterestDAO> storesPointsOfInterest = new ArrayList<>();

        assertTrue(storesPointsOfInterest.add(pointOfInterestRecord));

        Log.d(TAG, "end of addPointOfInterestTest()");
    }
}
