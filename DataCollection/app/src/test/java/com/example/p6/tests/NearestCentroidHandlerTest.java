package com.example.p6.tests;

import static org.junit.Assert.*;

import com.example.p6.classes.Centroid;
import com.example.p6.classes.Constants;
import com.example.p6.classes.DataPointAggregated;
import com.example.p6.handlers.NearestCentroidHandler;

import org.junit.Test;

public class NearestCentroidHandlerTest {

    @Test
    public void sittingDataPointsArePredictedAsSetting() {
        for (int i = 0; i < TestingConstants.sittingDataPoints.length; i++){
            Constants.Activity predictedActivity = NearestCentroidHandler.predict(
                    TestingConstants.sittingDataPoints[i],
                    TestingConstants.centroids);
            assertEquals(Constants.Activity.SITTING, predictedActivity);
        }
    }

    @Test
    public void walkingDataPointsArePredictedAsWalking() {
        for (int i = 0; i < TestingConstants.walkingDataPoints.length; i++){
            Constants.Activity predictedActivity = NearestCentroidHandler.predict(
                    TestingConstants.walkingDataPoints[i],
                    TestingConstants.centroids);
            assertEquals(Constants.Activity.WALKING, predictedActivity);
        }
    }

    @Test
    public void runningDataPointsArePredictedAsRunning() {
        for (int i = 0; i < TestingConstants.runningDataPoints.length; i++){
            Constants.Activity predictedActivity = NearestCentroidHandler.predict(
                    TestingConstants.runningDataPoints[i],
                    TestingConstants.centroids);
            assertEquals(Constants.Activity.RUNNING, predictedActivity);
        }
    }

    @Test
    public void cyclingDataPointsArePredictedAsCycling() {
        for (int i = 0; i < TestingConstants.cyclingDataPoints.length; i++){
            Constants.Activity predictedActivity = NearestCentroidHandler.predict(
                    TestingConstants.cyclingDataPoints[i],
                    TestingConstants.centroids);
            assertEquals(Constants.Activity.CYCLING, predictedActivity);
        }
    }
}
