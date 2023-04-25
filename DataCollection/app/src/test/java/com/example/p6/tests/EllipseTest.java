package com.example.p6.tests;
import static org.junit.Assert.*;

import com.example.p6.classes.Centroid;
import com.example.p6.classes.Constants;
import com.example.p6.classes.DataPointAggregated;
import com.example.p6.handlers.NearestCentroidHandler;

import org.junit.Test;
public class EllipseTest {
    @Test
    public void sittingDataPointsAreWithinSittingCentroid() {
        for (int i = 0; i < TestingConstants.sittingDataPoints.length; i++){
            boolean dataPointWithinEllipse = TestingConstants.centroids[Constants.Activity.SITTING.ordinal()]
                    .ellipse.contains(TestingConstants.sittingDataPoints[i]);
            assertEquals(true, dataPointWithinEllipse);
        }
    }

    @Test
    public void walkingDataPointsAreWithinWalkingCentroid() {
        for (int i = 0; i < TestingConstants.walkingDataPoints.length; i++){
            boolean dataPointWithinEllipse = TestingConstants.centroids[Constants.Activity.WALKING.ordinal()]
                    .ellipse.contains(TestingConstants.walkingDataPoints[i]);
            assertEquals(true, dataPointWithinEllipse);
        }
    }

    @Test
    public void runningDataPointsAreWithinRunningCentroid() {
        for (int i = 0; i < TestingConstants.runningDataPoints.length; i++){
            boolean dataPointWithinEllipse = TestingConstants.centroids[Constants.Activity.RUNNING.ordinal()]
                    .ellipse.contains(TestingConstants.runningDataPoints[i]);
            assertEquals(true, dataPointWithinEllipse);
        }
    }

    @Test
    public void cyclingDataPointsAreWithinCyclingCentroid() {
        for (int i = 0; i < TestingConstants.cyclingDataPoints.length; i++){
            boolean dataPointWithinEllipse = TestingConstants.centroids[Constants.Activity.CYCLING.ordinal()]
                    .ellipse.contains(TestingConstants.cyclingDataPoints[i]);
            assertEquals(true, dataPointWithinEllipse);
        }
    }

    @Test
    public void unlabeledDataPointsAreWithinNoCentroid() {
        for (int i = 0; i < Constants.NUMBER_OF_LABELS; i++){
            for (int j = 0; j < TestingConstants.unlabeledDataPoints.length; j++){
                boolean dataPointWithinEllipse = TestingConstants.centroids[i].
                        ellipse.contains(TestingConstants.unlabeledDataPoints[j]);
                assertEquals("Failed at " + String.valueOf(Constants.Activity.values()[i]),
                        false, dataPointWithinEllipse);
            }
        }
    }

}
