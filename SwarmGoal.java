package com.typoman.unstablenulls.entity.goal;

import com.typoman.unstablenulls.entity.NullEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class SwarmGoal extends Goal {

    private final NullEntity nullEntity;
    private final double speed;
    private int cooldown = 0;
    private int repathCooldown = 0;
    private double targetX, targetY, targetZ;
    private boolean hasDestination = false;

    public SwarmGoal(NullEntity entity, double speed) {
        this.nullEntity = entity;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (cooldown-- > 0) return false;
        cooldown = 40;
        Player target = getTargetPlayer();
        if (target == null) return false;

        targetX = target.getX() + (nullEntity.getRandom().nextDouble() - 0.5) * 4.0;
        targetY = target.getY();
        targetZ = target.getZ() + (nullEntity.getRandom().nextDouble() - 0.5) * 4.0;
        hasDestination = true;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return nullEntity.isAlive()
                && hasDestination
                && getTargetPlayer() != null
                && !nullEntity.getNavigation().isDone();
    }

    @Override
    public void stop() {
        hasDestination = false;
        nullEntity.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (repathCooldown-- <= 0) {
            repathCooldown = 10;
            if (hasDestination) nullEntity.getNavigation().moveTo(targetX, targetY, targetZ, speed);
        }
    }

    private Player getTargetPlayer() {
        return nullEntity.getTarget() instanceof Player p ? p : null;
    }
}
