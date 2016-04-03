package com.ogarproject.ogar.server.physic;

import com.ogarproject.ogar.api.entity.Entity;

/**
 * Created by Porama2 on 2/4/2016.
 */
public class PhysicsSupport {
    public PhysicsRecord record;
    boolean inited = false;

    public boolean isInitialized() {
        return inited;
    }

    public void InitPhysics(Entity entity) {
        record = new PhysicsRecord(entity);
        inited = true;
    }


    public PhysicsRecord getPhysics(){
        return record;
    }

    public void UpdateMovement(){
        record.Update();
    }
}
