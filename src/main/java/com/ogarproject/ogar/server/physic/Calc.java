package com.ogarproject.ogar.server.physic;

import com.ogarproject.ogar.api.Ogar;
import com.ogarproject.ogar.api.world.Position;
import com.ogarproject.ogar.server.world.WorldImpl;

/**
 * Created by Porama2 on 2/4/2016.
 */
public class Calc {

    public static Vector getVectorFromMovement(MovementData movement) {
        return getVectorFromMovement(movement.getAngle(), movement.getSpeed());
    }

    public static Vector getVectorFromMovement(double angle, double speed) {

        double VecX = Math.cos(getRadians(angle)) * speed;
        double VecY = Math.sin(getRadians(angle)) * speed;
        return new Vector(VecX, VecY);
    }

    public static MovementData getMovementFromVector(Vector vector) {
        double diff = vector.distanceCenter();
        double angle = getAngle(vector.getX(), vector.getY());
        double speed = diff;
        return new MovementData(angle, speed);
    }

    public static double getAngle(double X, double Y) {
        return getAngle(Math.atan2(X, Y));
    }

    public static double getAngle(double radians) {
        return radians * (180.0 / Math.PI);
    }

    public static double getRadians(double angle) {
        return (angle / 360) * (Math.PI * 2);
    }

    public static double getAngle(Vector vector) {
        return getAngle(vector.getX(), vector.getY());
    }

    public static Position KeepInWorld(Position position) {
        WorldImpl.Border border = ((WorldImpl) Ogar.getWorld()).getBorder();
        double x = position.getX();
        double y = position.getY();
        if (x < border.getLeft()) {
            x = border.getLeft();
        }
        if (x > border.getRight()) {
            x = border.getRight();
        }
        if (y < border.getTop()) {
            y = border.getTop();
        }
        if (y > border.getBottom()) {
            y = border.getBottom();
        }
        position.setX(x);
        position.setY(y);
        return position;
    }



}
