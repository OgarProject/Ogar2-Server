package com.ogarproject.ogar.server.physic;

/**
 * Created by Porama2 on 2/4/2016.
 */
public class MovementData {
    double angle = 0;
    double speed = 0;

    public MovementData(){}

    public MovementData(double angle, double speed){
        setAngle(angle);
        setSpeed(speed);
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getSpeed() {
        return speed;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getAngle() {
        return angle;
    }

    @Override
    public String toString() {
        return "Movement(" + angle + "," + speed + ")";
    }
}
