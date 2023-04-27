package com.example.p6.tests;

import com.example.p6.classes.Centroid;
import com.example.p6.classes.DataPointAggregated;
import com.example.p6.handlers.NearestCentroidHandler;

public class TestingVariables {

    public static final double DELTA = 0.01;

    public static Centroid[] centroids = NearestCentroidHandler.generalModelCentroids;
    public static DataPointAggregated[] sittingDataPoints = {
            new DataPointAggregated(60, 0, 0, 0),
            new DataPointAggregated(90, 0, 0, 0),
            new DataPointAggregated(60, 0, 0, 10),
            new DataPointAggregated(90, 0, 0, 10)};

    public static DataPointAggregated[] walkingDataPoints = {
            new DataPointAggregated(90, 0, 0, 80),
            new DataPointAggregated(110, 0, 0, 80),
            new DataPointAggregated(90, 0, 0, 200),
            new DataPointAggregated(110, 0, 0, 200)};

    public static DataPointAggregated[] runningDataPoints = {
            new DataPointAggregated(150, 0, 0, 140),
            new DataPointAggregated(170, 0, 0, 140),
            new DataPointAggregated(150, 0, 0, 160),
            new DataPointAggregated(170, 0, 0, 160)};

    public static DataPointAggregated[] cyclingDataPoints = {
            new DataPointAggregated(110, 0, 0, 0),
            new DataPointAggregated(160, 0, 0, 0),
            new DataPointAggregated(110, 0, 0, 10),
            new DataPointAggregated(160, 0, 0, 10)};

    public static DataPointAggregated[] unlabeledDataPoints = {
            new DataPointAggregated(0, 0, 0, 0),
            new DataPointAggregated(300, 0, 0, 0),
            new DataPointAggregated(0, 0, 0, 300),
            new DataPointAggregated(300, 0, 0, 300)};

    public static DataPointAggregated[] betweenWalkingAndCyclingDataPoints = {
            new DataPointAggregated(100, 0, 0, 2),
            new DataPointAggregated(105, 0, 0, 10),
            new DataPointAggregated(90, 0, 0, 29)};

    public static DataPointAggregated[] betweenSittingAndCyclingDataPoints = {
            new DataPointAggregated(80, 0, 0, 0),
            new DataPointAggregated(70, 0, 0, 10),
            new DataPointAggregated(90, 0, 0, 5)};

    // Special case
    public static DataPointAggregated[] betweenSittingAndCyclingDataPointsSpecial = {
            new DataPointAggregated(100, 0, 0, 0),
            new DataPointAggregated(101, 0, 0, 0),
            new DataPointAggregated(102, 0, 0, 0)};

    public static DataPointAggregated[] betweenSittingAndCyclingAndWalkingDataPoints = {
            new DataPointAggregated(97, 0, 0, 10),
            new DataPointAggregated(95, 0, 0, 12)};
}
