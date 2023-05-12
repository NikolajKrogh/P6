package com.example.p6.tests;

import static org.junit.Assert.*;

import com.example.p6.classes.Centroid;
import com.example.p6.classes.Constants;
import com.example.p6.classes.DataPointAggregated;
import com.example.p6.handlers.NearestCentroidHandler;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class NearestCentroidHandlerTest {

    @Test
    public void sittingDataPointsArePredictedAsSetting() {
        for (int i = 0; i < TestingVariables.sittingDataPoints.length; i++){
            Constants.Activity predictedActivity = NearestCentroidHandler.predict(
                    TestingVariables.sittingDataPoints[i],
                    TestingVariables.centroids);
            assertEquals(Constants.Activity.SITTING, predictedActivity);
        }
    }

    @Test
    public void walkingDataPointsArePredictedAsWalking() {
        for (int i = 0; i < TestingVariables.walkingDataPoints.length; i++){
            Constants.Activity predictedActivity = NearestCentroidHandler.predict(
                    TestingVariables.walkingDataPoints[i],
                    TestingVariables.centroids);
            assertEquals(Constants.Activity.WALKING, predictedActivity);
        }
    }

    @Test
    public void runningDataPointsArePredictedAsRunning() {
        for (int i = 0; i < TestingVariables.runningDataPoints.length; i++){
            Constants.Activity predictedActivity = NearestCentroidHandler.predict(
                    TestingVariables.runningDataPoints[i],
                    TestingVariables.centroids);
            assertEquals(Constants.Activity.RUNNING, predictedActivity);
        }
    }

    @Test
    public void unlabeledDataPointsArePredictedAsUnlabeled() {
        for (int i = 0; i < TestingVariables.unlabeledDataPoints.length; i++){
            Constants.Activity predictedActivity = NearestCentroidHandler.predict(
                    TestingVariables.unlabeledDataPoints[i],
                    TestingVariables.centroids);
            assertEquals(Constants.Activity.UNLABELED, predictedActivity);
        }
    }

    @Test
    public void cyclingDataPointsArePredictedAsCycling() {
        for (int i = 0; i < TestingVariables.cyclingDataPoints.length; i++){
            Constants.Activity predictedActivity = NearestCentroidHandler.predict(
                    TestingVariables.cyclingDataPoints[i],
                    TestingVariables.centroids);
            assertEquals(Constants.Activity.CYCLING, predictedActivity);
        }
    }

    @Test
    public void activitiesWithinTwoEllipsesArePredictedAsTheNearestActivity() {
        for (int i = 0; i < TestingVariables.betweenWalkingAndRunningDataPoints.length; i++){
            Constants.Activity predictedActivity = NearestCentroidHandler.predict(
                    TestingVariables.betweenWalkingAndRunningDataPoints[i],
                    TestingVariables.centroids);
            assertEquals(Constants.Activity.WALKING, predictedActivity);
        }
    }

    @Test
    public void centroidsAreUpdatedCorrectly() {
        NearestCentroidHandler.centroids[0] = new Centroid(10,10, 10,10,10,10,(byte) 0,10);
        NearestCentroidHandler.centroids[1] = new Centroid(10,10, 10,10,10,10,(byte) 1,10);
        NearestCentroidHandler.centroids[2] = new Centroid(10,10, 10,10,10,10,(byte) 2,10);

        List<DataPointAggregated> aggregatedDataPoints = new ArrayList<>();
        aggregatedDataPoints.add(new DataPointAggregated(10,10,10,10));
        aggregatedDataPoints.add(new DataPointAggregated(0,0,0,0));
        aggregatedDataPoints.add(new DataPointAggregated(100,100,100,100));

        List<Centroid> expectedCentroids = new ArrayList<>();
        expectedCentroids.add(new Centroid(10, 10, 10, 10, 10, 10, (byte) 0, 11));
        expectedCentroids.add(new Centroid(9.090909, 0, 9.090909, 9.090909, 0, 9.090909, (byte) 1, 11));
        expectedCentroids.add(new Centroid(18.181818, 18.181818, 100, 18.181818, 18.181818, 100, (byte) 2, 11));

        byte i = 0;
        byte[] labels = {0, 1, 2};

        for (DataPointAggregated dataPoint : aggregatedDataPoints){
            Centroid actualCentroid = NearestCentroidHandler.updateModel(
                    Constants.Activity.values()[labels[i]],
                    dataPoint.heartRate, dataPoint.minHeartRate, dataPoint.maxHeartRate,
                    dataPoint.stepCount, dataPoint.stepCount, dataPoint.stepCount, 1
                    );
            assertEquals("i: " + i + "\n" +
                    "Expected: " + expectedCentroids.get(i) + "\n" +
                    "Actual:   " + actualCentroid, expectedCentroids.get(i), actualCentroid);
            i++;
        }
    }
}
