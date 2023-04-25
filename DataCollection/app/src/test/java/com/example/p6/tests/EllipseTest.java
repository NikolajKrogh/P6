package com.example.p6.tests;

import static org.junit.Assert.*;

import com.example.p6.classes.Centroid;
import com.example.p6.classes.Constants;
import com.example.p6.classes.DataPointAggregated;
import com.example.p6.handlers.NearestCentroidHandler;

import org.junit.Test;
public class EllipseTest {

    Centroid[] centroids = NearestCentroidHandler.generalModelCentroids;
    DataPointAggregated[] sittingDataPoints = {
            new DataPointAggregated(60, 0, 0, 0, 0, 0),
            new DataPointAggregated(100, 0, 0, 0, 0, 0),
            new DataPointAggregated(60, 0, 0, 10, 0, 0),
            new DataPointAggregated(100, 0, 0, 10, 0, 0)};

    DataPointAggregated[] walkingDataPoints = {
            new DataPointAggregated(90, 0, 0, 80, 0, 0),
            new DataPointAggregated(110, 0, 0, 80, 0, 0),
            new DataPointAggregated(90, 0, 0, 200, 0, 0),
            new DataPointAggregated(110, 0, 0, 200, 0, 0)};

    DataPointAggregated[] runningDataPoints = {
            new DataPointAggregated(150, 0, 0, 140, 0, 0),
            new DataPointAggregated(170, 0, 0, 140, 0, 0),
            new DataPointAggregated(150, 0, 0, 160, 0, 0),
            new DataPointAggregated(170, 0, 0, 160, 0, 0)};

    DataPointAggregated[] cyclingDataPoints = {
            new DataPointAggregated(80, 0, 0, 0, 0, 0),
            new DataPointAggregated(160, 0, 0, 0, 0, 0),
            new DataPointAggregated(80, 0, 0, 10, 0, 0),
            new DataPointAggregated(160, 0, 0, 10, 0, 0)};

    DataPointAggregated[] unlabeledDataPoints = {
            new DataPointAggregated(0, 0, 0, 0, 0, 0),
            new DataPointAggregated(300, 0, 0, 0, 0, 0),
            new DataPointAggregated(0, 0, 0, 300, 0, 0),
            new DataPointAggregated(300, 0, 0, 300, 0, 0)};


    @Test
    public void sittingDataPointsAreWithinSittingCentroid() {
        for (int i = 0; i < sittingDataPoints.length; i++){
            boolean actual = centroids[Constants.Activity.SITTING.ordinal()].ellipse.contains(sittingDataPoints[i]);
            assertEquals(true, actual);
        }
    }

    @Test
    public void walkingDataPointsAreWithinWalkingCentroid() {
        for (int i = 0; i < walkingDataPoints.length; i++){
            boolean actual = centroids[Constants.Activity.WALKING.ordinal()].ellipse.contains(walkingDataPoints[i]);
            assertEquals(true, actual);
        }
    }

    @Test
    public void runningDataPointsAreWithinRunningCentroid() {
        for (int i = 0; i < runningDataPoints.length; i++){
            boolean actual = centroids[Constants.Activity.RUNNING.ordinal()].ellipse.contains(runningDataPoints[i]);
            assertEquals(true, actual);
        }
    }

    @Test
    public void cyclingDataPointsAreWithinCyclingCentroid() {
        for (int i = 0; i < cyclingDataPoints.length; i++){
            boolean actual = centroids[Constants.Activity.CYCLING.ordinal()].ellipse.contains(cyclingDataPoints[i]);
            assertEquals(true, actual);
        }
    }

    @Test
    public void unlabeledDataPointsAreWithinNoCentroid() {
        for (int i = 0; i < Constants.NUMBER_OF_LABELS; i++){
            for (int j = 0; j < unlabeledDataPoints.length; j++){
                boolean actual = centroids[i].ellipse.contains(unlabeledDataPoints[j]);
                assertEquals("Failed at " + String.valueOf(Constants.Activity.values()[i]),
                        false, actual);
            }
        }
    }

}
