package com.typoman.unstablenulls.entity.goal;

import com.typoman.unstablenulls.entity.NullEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;

import java.util.EnumSet;

public class RangedSupportGoal extends Goal {

    private final NullEntity nullEntity;
    private int cooldown = 0;
    private int fireTimer = 0;
    private int losRefreshTimer = 0;
    private boolean cachedLOS = false;

    public RangedSupportGoal(NullEntity entity) {
        this.nullEntity = entity;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (cooldown-- > 0) return false;
        cooldown = 60;
        if (nullEntity.getId() % 5 != 0) return false;
        Player target = getTargetPlayer();
        if (target == null) return false;
        if (nullEntity.distanceToSqr(target) <= 36) return false;

        cachedLOS = nullEntity.hasLineOfSight(target);
        if (!cachedLOS) return false;

        fireTimer = 20;
        losRefreshTimer = 0;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        Player target = getTargetPlayer();
        return nullEntity.isAlive() && target != null && cachedLOS;
    }

    @Override
    public void stop() {
        cachedLOS = false;
        fireTimer = 0;
        losRefreshTimer = 0;
    }

    @Override
    public void tick() {
        Player target = getTargetPlayer();
        if (target == null) return;

        nullEntity.getLookControl().setLookAt(target, 30F, 30F);

        if (losRefreshTimer-- <= 0) {
            losRefreshTimer = 10;
            cachedLOS = nullEntity.hasLineOfSight(target);
            if (!cachedLOS) return;
        }

        if (fireTimer-- <= 0) {
            fireTimer = 40;
            Arrow arrow = new Arrow(nullEntity.level(), nullEntity);
            arrow.setPos(nullEntity.getX(), nullEntity.getEyeY(), nullEntity.getZ());

            double dx = target.getX() - nullEntity.getX();
            double dy = target.getEyeY() - arrow.getY();
            double dz = target.getZ() - nullEntity.getZ();
            arrow.shoot(dx, dy, dz, 1.6F, 4.0F);
            nullEntity.level().addFreshEntity(arrow);
        }
    }

    private Player getTargetPlayer() {
        return nullEntity.getTarget() instanceof Player p ? p : null;
    }
}
