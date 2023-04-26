package com.example.p6.tests;

import static org.junit.Assert.*;

import com.example.p6.classes.Centroid;
import com.example.p6.classes.Constants;
import com.example.p6.handlers.NearestCentroidHandler;

import org.junit.Test;

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
}
