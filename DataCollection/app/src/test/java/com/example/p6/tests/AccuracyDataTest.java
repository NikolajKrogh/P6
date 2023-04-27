package com.example.p6.tests;
import static org.junit.Assert.*;
import com.example.p6.classes.AccuracyData;
import com.example.p6.classes.Constants;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AccuracyDataTest {


    @Test
    public void AccuracyDataTest() {
        List<Constants.Activity> predictedActivities = new ArrayList<>();
        predictedActivities.add(Constants.Activity.SITTING);
        predictedActivities.add(Constants.Activity.SITTING);
        predictedActivities.add(Constants.Activity.SITTING);
        predictedActivities.add(Constants.Activity.RUNNING);
        predictedActivities.add(Constants.Activity.CYCLING);
        predictedActivities.add(Constants.Activity.CYCLING);


        Constants.Activity sittingToTrack = Constants.Activity.SITTING;

        AccuracyData accuracyDataSitting = new AccuracyData(predictedActivities, sittingToTrack);

        assertEquals(0.5, accuracyDataSitting.accuracy, TestingVariables.DELTA);
        assertEquals(3, accuracyDataSitting.correctPredictions, TestingVariables.DELTA);
        assertEquals(6, accuracyDataSitting.totalPredictions, TestingVariables.DELTA);
        assertEquals(3, accuracyDataSitting.sittingPredictions, TestingVariables.DELTA);
        assertEquals(0, accuracyDataSitting.walkingPredictions, TestingVariables.DELTA);
        assertEquals(1, accuracyDataSitting.runningPredictions, TestingVariables.DELTA);
        assertEquals(2, accuracyDataSitting.cyclingPredictions, TestingVariables.DELTA);
    }
}
