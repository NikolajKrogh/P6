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
}
