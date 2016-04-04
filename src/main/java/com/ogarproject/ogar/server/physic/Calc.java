package com.ogarproject.ogar.server.physic;

/**
 * Created by Porama2 on 2/4/2016.
 */
public class Calc {

    public static Vector getVectorFromMovement(MovementData movement) {
        return getVectorFromMovement(movement.getAngle(), movement.getSpeed());
    }

    public static Vector getVectorFromMovement(double angle, double speed) {

        double VecX = Math.cos((angle / 360) * (Math.PI * 2)) * speed;
        double VecY = Math.sin((angle / 360) * (Math.PI * 2)) * speed;
        return new Vector(VecX, VecY);
    }

    public static MovementData getMovementFromVector(Vector vector) {
        double diff = vector.distanceCenter();
        double angle = getAngle(vector.getX(), vector.getY());
        double speed = diff;
        return new MovementData(angle, speed);
    }

    public static double getAngle(double X, double Y) {
        return Math.atan2(X, Y);
    }

    public static double getAngle(Vector vector) {
        return getAngle(vector.getX(), vector.getY());
    }

}
