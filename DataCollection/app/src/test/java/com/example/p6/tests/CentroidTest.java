package com.example.p6.tests;

import static org.junit.Assert.assertEquals;

import com.example.p6.classes.Centroid;

import org.junit.Test;

public class CentroidTest {

    @Test
    public void formatUIStringTest(){
        Centroid centroid = new Centroid(76.96141393842203,54.495219885277244,105.07047619047619,0.0,0.0,18.0,(byte) 0,180);

        //Activity, heartRate, stepCount, ellipse.heartRate, ellipse.stepCount, ellipse.getSemiMajorAxis(), ellipse.getSemiMinorAxis())
        String expectedOutput = "SITTING:\n" + "76.96, 0.00\n79.78, 0.00\n25.29, 18.00\n";
        assertEquals(expectedOutput, centroid.formatUIString());
    }
    @Test
    public void toStringTest(){
        Centroid centroid = new Centroid(76.96141393842203,54.495219885277244,105.07047619047619,0.0,0.0,18.0,(byte) 0,180);

        //heartRate, ellipse.minHeartRate, ellipse.maxHeartRate, stepCount, ellipse.minStepCount, ellipse.maxStepCount, label, size
        String expectedOutput = "76.961414,54.495220,105.070476,0.000000,0.000000,18.000000,0,180";
        assertEquals(expectedOutput, centroid.toString());
    }
}
