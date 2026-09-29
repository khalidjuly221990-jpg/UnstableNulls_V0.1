package com.typoman.unstablenulls.entity.goal;

import com.typoman.unstablenulls.entity.NullEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class RegroupGoal extends Goal {

    private final NullEntity nullEntity;
    private final double speed;
    private int cooldown = 0;
    private int repathCooldown = 0;
    private double anchorX, anchorY, anchorZ;
    private boolean hasAnchor = false;

    public RegroupGoal(NullEntity entity, double speed) {
        this.nullEntity = entity;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (cooldown-- > 0) return false;
        cooldown = 80;

        AABB area = nullEntity.getBoundingBox().inflate(8.0D);
        List<NullEntity> nearby = nullEntity.level().getEntitiesOfClass(NullEntity.class, area);
        UUID myTrial = nullEntity.getTrialId();

        int others = 0;
        NullEntity nearest = null;
        double nearestDist = Double.MAX_VALUE;

        for (NullEntity n : nearby) {
            if (n == nullEntity) continue;
            if (myTrial == null || !myTrial.equals(n.getTrialId())) continue;
            others++;
            double d = nullEntity.distanceToSqr(n);
            if (d < nearestDist) {
                nearestDist = d;
                nearest = n;
            }
        }

        if (others >= 2) return false;
        if (nearest == null) return false;

        anchorX = nearest.getX();
        anchorY = nearest.getY();
        anchorZ = nearest.getZ();
        hasAnchor = true;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return nullEntity.isAlive()
                && hasAnchor
                && !nullEntity.getNavigation().isDone();
    }

    @Override
    public void stop() {
        hasAnchor = false;
        nullEntity.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (repathCooldown-- <= 0) {
            repathCooldown = 15;
            if (hasAnchor) nullEntity.getNavigation().moveTo(anchorX, anchorY, anchorZ, speed);
        }
    }
}
