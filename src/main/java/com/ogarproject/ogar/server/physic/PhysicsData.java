package com.ogarproject.ogar.server.physic;

import com.ogarproject.ogar.api.entity.Entity;

/**
 * Created by Porama2 on 1/4/2016.
 */
public class PhysicsData {
    final Entity entity;
    public MovementData movement = new MovementData();
    double resistance = 0;

    public PhysicsData(Entity entityowner) {
        entity = entityowner;
    }

    public double getResistance() {
        return resistance;
    }

    public void setResistance(double resistance) {
        this.resistance = resistance;
    }

    public Entity getEntity() {
        return entity;
    }

    public boolean isUpdateNeeded() {
        return !(movement.getSpeed() == 0);
    }

    public Vector getVector() {
        return Calc.getVectorFromMovement(movement.getAngle(), movement.getSpeed());
    }

    public void setVector(Vector vector) {
        movement = Calc.getMovementFromVector(vector);
    }

    public void setMovement(MovementData record){
        movement = record;
    }

    public void setSpeed(double speed){
        movement.setSpeed(speed);
    }

    public double getSpeed(){
        return movement.getSpeed();
    }

    public void setAngle(double angle){
        movement.setAngle(angle);
    }

    public double getAngle(){
        return movement.getAngle();
    }

    public void UpdatePosition() {
        Vector vector = getVector();
        entity.setPosition(entity.getPosition().add(vector.getX(), vector.getY()));
    }

    public void UpdateData() {
        double speed = movement.getSpeed();
        speed -= resistance;
        if (speed < 0) speed = 0;
        movement.setSpeed(speed);
    }

    public void Update() {
        UpdatePosition();
        UpdateData();
    }
}

