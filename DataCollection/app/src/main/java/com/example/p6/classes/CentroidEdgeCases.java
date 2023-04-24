package com.example.p6.classes;

public class CentroidEdgeCases {
    public DataPointBasic northernMostPoint;
    public DataPointBasic easternMostPoint;
    public DataPointBasic southernMostPoint;
    public DataPointBasic westernMostPoint;

    public CentroidEdgeCases(DataPointBasic northernMostPoint,
                             DataPointBasic easternMostPoint,
                             DataPointBasic southernMostPoint,
                             DataPointBasic westernMostPoint){
        this.northernMostPoint = northernMostPoint;
        this.easternMostPoint = easternMostPoint;
        this.southernMostPoint = southernMostPoint;
        this.westernMostPoint = westernMostPoint;
    }
}
