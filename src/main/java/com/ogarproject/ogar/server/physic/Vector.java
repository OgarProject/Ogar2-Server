package com.ogarproject.ogar.server.physic;

import com.ogarproject.ogar.api.world.Position;

/**
 * Created by Porama2 on 1/4/2016.
 */
public class Vector {
    double X = 0.0;
    double Y = 0.0;

    public Vector() {
    }

    public Vector(double X, double Y) {
        this.X = X;
        this.Y = Y;
    }

    public Vector(Position pos){
        X = pos.getX();
        Y = pos.getY();
    }

    public double getX() {
        return X;
    }

    public void setX(double x) {
        X = x;
    }

    public double getY() {
        return Y;
    }

    public void setY(double y) {
        Y = y;
    }

    public Position toPosition() {
        return new Position(X, Y);
    }

    public double distanceCenter(){
        return distance(new Vector());
    }

    public void ReverseY(){
        Y = 0 - Y;
    }

    public double distance(Vector second) {
        double X = second.getX() - getX();
        double Y = second.getY() - getY();
        double X2 = Math.pow(X,2);
        double Y2 = Math.pow(Y,2);
        return Math.sqrt(X2 + Y2);
    }

    @Override
    public String toString() {
        return "Vector(" + getX() + "," + getY() + ")";
    }
}
