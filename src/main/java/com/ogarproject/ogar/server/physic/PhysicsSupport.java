package com.ogarproject.ogar.server.physic;

import com.ogarproject.ogar.api.entity.Entity;

/**
 * Created by Porama2 on 2/4/2016.
 */
public class PhysicsSupport {
    public PhysicsData record;
    boolean inited = false;
    boolean enable = true;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void AutoUpdatePhysics(Entity entity) {
        if(!isEnable()) return;
        if (!isInitialized()) InitPhysics(entity);
        UpdateMovement();
    }

    public boolean isInitialized() {
        return inited;
    }

    public void InitPhysics(Entity entity) {
        record = new PhysicsData(entity);
        inited = true;
    }

    public PhysicsData getPhysics() {
        return record;
    }

    public void UpdateMovement() {
        record.Update();
    }
}
