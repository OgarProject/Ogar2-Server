package com.ogarproject.ogar.server.physic;

import com.ogarproject.ogar.api.entity.Entity;

/**
 * Created by Porama2 on 1/4/2016.
 */
public class PhysicsRecord {
    final Entity entity;
    public MovementRecord movement = new MovementRecord();
    double resistance = 0;

    public PhysicsRecord(Entity entityowner) {
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
        return Calc.getVectorFromMovement(movement.getDegree(), movement.getSpeed());
    }

    public void setVector(Vector vector) {
        movement = Calc.getMovementFromVector(vector);
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
        //if(!isNeedUpdate()) return;
        UpdatePosition();
        UpdateData();
    }
}

