package com.example.p6.tests;

import static org.junit.Assert.assertEquals;

import com.example.p6.classes.Centroid;

import org.junit.Test;

public class CentroidTest {

    @Test
    public void UIStringIsFormattedCorrectly(){
        Centroid centroid = new Centroid(76.96141393842203,54.495219885277244,105.07047619047619,0.0,0.0,18.0,(byte) 0,180);

        //Activity, heartRate, stepCount, ellipse.heartRate, ellipse.stepCount, ellipse.getSemiMajorAxis(), ellipse.getSemiMinorAxis())
        String expectedOutput = "SITTING:\n" + "Centroid: 76.96, 0.00\nEllipse-center: 79.78, 0.00\nEllipse-axes: 25.29, 18.00\n";
        assertEquals(expectedOutput, centroid.formatUIString());
    }
    @Test
    public void stringIsFormattedCorrectly(){
        Centroid centroid = new Centroid(76.96141393842203,54.495219885277244,105.07047619047619,0.0,0.0,18.0,(byte) 0,180);

        //If the precision for String.format which toString uses is not specified, then the default value is 6
        //heartRate, ellipse.minHeartRate, ellipse.maxHeartRate, stepCount, ellipse.minStepCount, ellipse.maxStepCount, label, size
        String expectedOutput = "76.961414,54.495220,105.070476,0.000000,0.000000,18.000000,0,180";
        assertEquals(expectedOutput, centroid.toString());
    }
}
