package com.typoman.unstablenulls.entity.goal;

import com.typoman.unstablenulls.entity.NullEntity;
import com.typoman.unstablenulls.trial.FarlandsTrialManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

/**
 * Target goal that isolates a trial Null to its owning player.
 * Manual Nulls (trialId == null) retain the normal nearest-player behavior.
 */
public class NullTargetGoal extends Goal {
    private final NullEntity nullEntity;
    private int cooldown;
    private Player candidate;

    public NullTargetGoal(NullEntity nullEntity) {
        this.nullEntity = nullEntity;
        this.cooldown = 0;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (cooldown-- > 0) {
            return false;
        }
        cooldown = 10;

        Player current = nullEntity.getTarget();
        if (isValidTarget(current)) {
            return false;
        }

        candidate = findTarget();
        return candidate != null;
    }

    @Override
    public boolean canContinueToUse() {
        return isValidTarget(nullEntity.getTarget());
    }

    @Override
    public void start() {
        if (candidate != null) {
            nullEntity.setTarget(candidate);
        }
    }

    @Override
    public void stop() {
        candidate = null;
        if (!isValidTarget(nullEntity.getTarget())) {
            nullEntity.setTarget(null);
        }
    }

    private Player findTarget() {
        UUID trialId = nullEntity.getTrialId();
        double range = nullEntity.getAttributeValue(Attributes.FOLLOW_RANGE);
        double rangeSq = range * range;

        if (trialId != null) {
            FarlandsTrialManager.TrialSession session = FarlandsTrialManager.getSessionByTrial(trialId);
            if (session == null) {
                return null;
            }

            ServerPlayer owner = session.level.getServer().getPlayerList().getPlayer(session.playerId);
            if (owner == null || owner.isSpectator() || owner.isDeadOrDying()) {
                return null;
            }
            if (owner.serverLevel() != nullEntity.level()) {
                return null;
            }
            if (nullEntity.distanceToSqr(owner) > rangeSq) {
                return null;
            }
            return owner;
        }

        AABB area = nullEntity.getBoundingBox().inflate(range);
        List<Player> players = nullEntity.level().getEntitiesOfClass(Player.class, area,
                p -> !p.isSpectator() && !p.isDeadOrDying());
        return players.stream()
                .filter(p -> nullEntity.distanceToSqr(p) <= rangeSq)
                .min(Comparator.comparingDouble(nullEntity::distanceToSqr))
                .orElse(null);
    }

    private boolean isValidTarget(Player target) {
        if (target == null || !target.isAlive() || target.isSpectator()) {
            return false;
        }
        double range = nullEntity.getAttributeValue(Attributes.FOLLOW_RANGE);
        if (nullEntity.distanceToSqr(target) > range * range) {
            return false;
        }

        UUID trialId = nullEntity.getTrialId();
        if (trialId == null) {
            return true;
        }

        FarlandsTrialManager.TrialSession session = FarlandsTrialManager.getSessionByTrial(trialId);
        return session != null
                && target.getUUID().equals(session.playerId)
                && target.level() == session.level;
    }
}
