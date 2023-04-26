package com.example.p6.tests;
import static org.junit.Assert.*;

import com.example.p6.classes.Centroid;
import com.example.p6.classes.Constants;

import org.junit.Test;
public class EllipseTest {
    @Test
    public void sittingDataPointsAreWithinSittingCentroid() {
        for (int i = 0; i < TestingVariables.sittingDataPoints.length; i++){
            boolean dataPointWithinEllipse = TestingVariables.centroids[Constants.Activity.SITTING.ordinal()]
                    .ellipse.contains(TestingVariables.sittingDataPoints[i]);
            assertEquals(true, dataPointWithinEllipse);
        }
    }

    @Test
    public void walkingDataPointsAreWithinWalkingCentroid() {
        for (int i = 0; i < TestingVariables.walkingDataPoints.length; i++){
            boolean dataPointWithinEllipse = TestingVariables.centroids[Constants.Activity.WALKING.ordinal()]
                    .ellipse.contains(TestingVariables.walkingDataPoints[i]);
            assertEquals(true, dataPointWithinEllipse);
        }
    }

    @Test
    public void runningDataPointsAreWithinRunningCentroid() {
        for (int i = 0; i < TestingVariables.runningDataPoints.length; i++){
            boolean dataPointWithinEllipse = TestingVariables.centroids[Constants.Activity.RUNNING.ordinal()]
                    .ellipse.contains(TestingVariables.runningDataPoints[i]);
            assertEquals(true, dataPointWithinEllipse);
        }
    }

    @Test
    public void cyclingDataPointsAreWithinCyclingCentroid() {
        for (int i = 0; i < TestingVariables.cyclingDataPoints.length; i++){
            boolean dataPointWithinEllipse = TestingVariables.centroids[Constants.Activity.CYCLING.ordinal()]
                    .ellipse.contains(TestingVariables.cyclingDataPoints[i]);
            assertEquals(true, dataPointWithinEllipse);
        }
    }

    @Test
    public void unlabeledDataPointsAreWithinNoCentroid() {
        for (int i = 0; i < Constants.NUMBER_OF_LABELS; i++){
            for (int j = 0; j < TestingVariables.unlabeledDataPoints.length; j++){
                boolean dataPointWithinEllipse = TestingVariables.centroids[i].
                        ellipse.contains(TestingVariables.unlabeledDataPoints[j]);
                assertEquals("Failed at " + String.valueOf(Constants.Activity.values()[i]),
                        false, dataPointWithinEllipse);
            }
        }
    }

    //region Input for semi-major axis and semi-minor axis tests
    private static double[] expectedResultsForSemiMajorAxes = {45, 170, 0};
    private static double[] expectedResultsForSittingAndCyclingSemiMinorAxes = {10, 0, 100};
    private static double[] expectedResultsForWalkingAndRunningSemiMinorAxes = {5, 0, 25};
    private static double[][] expectedSemiMinorAxesForCentroids = {
            expectedResultsForSittingAndCyclingSemiMinorAxes, expectedResultsForWalkingAndRunningSemiMinorAxes,
            expectedResultsForWalkingAndRunningSemiMinorAxes, expectedResultsForSittingAndCyclingSemiMinorAxes};
    //endregion

    @Test
    public void semiMajorAxesAreCalculatedCorrectly() {
        for (int i = 0; i < Constants.NUMBER_OF_LABELS; i++) {
            Centroid[] customCentroids = {
                new Centroid(60, 30, 120, 0, 0, 0, (byte)i, 0),
                new Centroid(60, 60, 400, 0, 0, 0, (byte)i, 0),
                new Centroid(60, 60, 60, 0, 0, 0, (byte)i, 0)};

            for (int j = 0; j < customCentroids.length; j++) {
                double expectedSemiMajorAxis = expectedResultsForSemiMajorAxes[j];
                double actualSemiMajorAxis = customCentroids[j].ellipse.getSemiMajorAxis();
                assertEquals("Failed at " + Constants.Activity.values()[i],
                        expectedSemiMajorAxis, actualSemiMajorAxis, TestingVariables.DELTA);
            }
        }
    }

    @Test
    public void semiMinorAxesAreCalculatedCorrectly() {
        for (int i = 0; i < Constants.NUMBER_OF_LABELS; i++) {
            Centroid[] customCentroids = {
                    new Centroid(60, 0, 0, 0, 0, 10, (byte)i, 0),
                    new Centroid(60, 0, 0, 0, 0, 0, (byte)i, 0),
                    new Centroid(60, 0, 0, 0, 50, 100, (byte)i, 0)};

            for (int j = 0; j < customCentroids.length; j++) {
                double expectedSemiMinorAxis = expectedSemiMinorAxesForCentroids[i][j];
                double actualSemiMinorAxis = customCentroids[j].ellipse.getSemiMinorAxis();
                assertEquals("Failed at " + Constants.Activity.values()[i],
                        expectedSemiMinorAxis, actualSemiMinorAxis, TestingVariables.DELTA);
            }
        }
    }
}
