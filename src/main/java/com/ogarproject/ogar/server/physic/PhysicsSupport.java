package com.ogarproject.ogar.server.physic;

import com.ogarproject.ogar.api.entity.Entity;

/**
 * Created by Porama2 on 2/4/2016.
 */
public class PhysicsSupport {
    public PhysicsRecord record;

    public void InitPhysics(Entity entity) {
        record = new PhysicsRecord(entity);
    }

    public PhysicsRecord getPhysics(){
        return record;
    }

    public void UpdateMovement(){
        record.Update();
    }
}
