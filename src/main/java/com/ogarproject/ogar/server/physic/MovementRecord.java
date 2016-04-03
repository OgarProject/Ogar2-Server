package com.ogarproject.ogar.server.physic;

/**
 * Created by Porama2 on 2/4/2016.
 */
public class MovementRecord {
    double degree = 0;
    double speed = 0;

    public MovementRecord(){}

    public MovementRecord(double degree, double speed){
        setDegree(degree);
        setSpeed(speed);
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getSpeed() {
        return speed;
    }

    public void setDegree(double degree) {
        this.degree = degree;
    }

    public double getDegree() {
        return degree;
    }

    @Override
    public String toString() {
        return "Movement(" + degree + "," + speed + ")";
    }
}
