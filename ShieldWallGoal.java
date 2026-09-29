package com.typoman.unstablenulls.entity.goal;

import com.typoman.unstablenulls.entity.NullEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class ShieldWallGoal extends Goal {

    private final NullEntity nullEntity;
    private final double speed;
    private int cooldown = 0;
    private int repathCooldown = 0;
    private double wallX, wallY, wallZ;
    private boolean hasPosition = false;

    public ShieldWallGoal(NullEntity entity, double speed) {
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
        if (nullEntity.distanceToSqr(target) >= 100) return false;

        double dx = nullEntity.getX() - target.getX();
        double dz = nullEntity.getZ() - target.getZ();
        double len = Math.sqrt(dx * dx + dz * dz);
        if (len < 0.001) return false;

        double perpX = -dz / len;
        double perpZ = dx / len;
        int slot = (nullEntity.getId() % 5) - 2;

        wallX = target.getX() + perpX * (4.0 + slot);
        wallY = target.getY();
        wallZ = target.getZ() + perpZ * (4.0 + slot);
        hasPosition = true;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return nullEntity.isAlive()
                && hasPosition
                && getTargetPlayer() != null
                && !nullEntity.getNavigation().isDone();
    }

    @Override
    public void stop() {
        hasPosition = false;
        nullEntity.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (repathCooldown-- <= 0) {
            repathCooldown = 10;
            if (hasPosition) nullEntity.getNavigation().moveTo(wallX, wallY, wallZ, speed);
        }
    }

    private Player getTargetPlayer() {
        return nullEntity.getTarget() instanceof Player p ? p : null;
    }
}
