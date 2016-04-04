package com.ogarproject.ogar.server.physic;

/**
 * Created by Porama2 on 2/4/2016.
 */
public class Calc {
    public static double getAngle(double radians) {
        return radians * (180.0 / Math.PI);
    }

    public static Vector getVectorFromMovement(MovementRecord movement) {
        return getVectorFromMovement(movement.getDegree(), movement.getSpeed());
    }

    public static Vector getVectorFromMovement(double angle, double speed) {

        double VecX = Math.cos((angle / 360) * (Math.PI * 2)) * speed;
        double VecY = Math.sin((angle / 360) * (Math.PI * 2)) * speed;
        return new Vector(VecX, VecY);
    }

    public static MovementRecord getMovementFromVector(Vector vector) {
        double diff = vector.distanceCenter();
        //double X = vector.getX() / diff;
        //int Y = (int) (vector.getY() / diff);
        //double angle = getAngleFromRadians(Math.acos(X));
        double angle = getAngle(vector.getX(),vector.getY());
        double speed = diff;
        return new MovementRecord(angle, speed);
    }

    public static double getAngle(double X, double Y) {
        return Math.atan2(X, Y);
    }
}
