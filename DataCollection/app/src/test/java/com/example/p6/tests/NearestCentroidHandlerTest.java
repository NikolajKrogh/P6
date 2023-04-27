package com.example.p6.tests;

import static org.junit.Assert.*;

import com.example.p6.classes.Centroid;
import com.example.p6.classes.Constants;
import com.example.p6.classes.DataPointAggregated;
import com.example.p6.handlers.NearestCentroidHandler;

import org.junit.Test;

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
    public void betweenWalkingAndCyclingDataPointsArePredictedAsCycling() {
        for (int i = 0; i < TestingVariables.betweenWalkingAndCyclingDataPoints.length; i++){
            Constants.Activity predictedActivity = NearestCentroidHandler.predict(
                    TestingVariables.betweenWalkingAndCyclingDataPoints[i],
                    TestingVariables.centroids);
            assertEquals(Constants.Activity.CYCLING, predictedActivity);
        }
    }

    @Test
    public void betweenSittingAndCyclingDataPointsArePredictedAsSitting() {
        for (int i = 0; i < TestingVariables.betweenSittingAndCyclingDataPoints.length; i++){
            Constants.Activity predictedActivity = NearestCentroidHandler.predict(
                    TestingVariables.betweenSittingAndCyclingDataPoints[i],
                    TestingVariables.centroids);
            assertEquals(Constants.Activity.SITTING, predictedActivity);
        }
    }

    @Test
    public void betweenSittingAndCyclingAndWalkingDataPointsArePredictedAsSitting() {
        for (int i = 0; i < TestingVariables.betweenSittingAndCyclingAndWalkingDataPoints.length; i++){
            Constants.Activity predictedActivity = NearestCentroidHandler.predict(
                    TestingVariables.betweenSittingAndCyclingAndWalkingDataPoints[i],
                    TestingVariables.centroids);
            assertEquals(Constants.Activity.SITTING, predictedActivity);
        }
    }

    @Test
    public void betweenSittingAndCyclingDataPointsArePredictedAsCycling() {
        for (int i = 0; i < TestingVariables.betweenSittingAndCyclingDataPointsSpecial.length; i++){
            Constants.Activity predictedActivity = NearestCentroidHandler.predict(
                    TestingVariables.betweenSittingAndCyclingDataPointsSpecial[i],
                    TestingVariables.centroids);
            assertEquals(Constants.Activity.CYCLING, predictedActivity);
        }
    }

    @Test
    public void updateCentroidWithNewDataPoint_noError() {
        int length = NearestCentroidHandler.centroids.length;
        DataPointAggregated input = new DataPointAggregated(1, 1, 1, 1);
        NearestCentroidHandler.centroids[0] = new Centroid(10,10, 10,10,10,10,(byte) 0,1);
        NearestCentroidHandler.centroids[1] = new Centroid(5,5, 5,5,5,5,(byte) 0,1);
        NearestCentroidHandler.centroids[2] = new Centroid(2,2, 2,2,2,2,(byte) 0,1);
        NearestCentroidHandler.centroids[3] = new Centroid(0,0, 0,0,0,0,(byte) 0,0);

        double[] expected = {5.5,4,3.25,2.8};

        for(int i = 0; i < length; i++){
            Centroid actual = NearestCentroidHandler.updateModel(Constants.Activity.SITTING, input);
            assertEquals(expected[i], actual.heartRate, TestingVariables.DELTA);
        }
    }
}
