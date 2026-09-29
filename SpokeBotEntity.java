package com.typoman.unstablenulls.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class SpokeBotEntity extends Monster {

    private int dialogueCooldown = 200;
    private UUID trialId = null;
    private boolean announced = false;

    public SpokeBotEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setCustomName(Component.literal("§c§lSpoke Bot"));
        this.setCustomNameVisible(true);
    }

    public void setTrialId(UUID id) {
        this.trialId = id;
    }

    public UUID getTrialId() {
        return trialId;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 500.0D)
                .add(Attributes.ARMOR, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 128.0D);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!this.level().isClientSide() && !announced) {
            announced = true;
            this.level().players().forEach(p -> {
                if (p.distanceToSqr(this) < 4096) {
                    p.sendSystemMessage(Component.literal("§c§lSpoke Bot: §r§fWHY ARE YOU GUYS DYING TO ONE PLAYER"));
                }
            });
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide() || trialId == null) {
            return;
        }

        if (dialogueCooldown-- <= 0) {
            dialogueCooldown = 400;
            var session = com.typoman.unstablenulls.trial.FarlandsTrialManager.getSessionByTrial(trialId);
            if (session != null) {
                Player p = this.level().getServer().getPlayerList().getPlayer(session.playerId);
                if (p != null && p.level() == this.level()) {
                    p.sendSystemMessage(Component.literal("§c§lSpoke Bot: §r§fWHY ARE YOU GUYS DYING TO ONE PLAYER"));
                }
            }
        }
    }

    public void fireOrbitalStrike(Player target) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Vec3 pos = target.position();
        serverLevel.explode(this, pos.x, pos.y, pos.z, 3.0F, Level.ExplosionInteraction.NONE);
        serverLevel.playSound(null, pos.x, pos.y, pos.z, SoundEvents.GENERIC_EXPLODE,
                SoundSource.HOSTILE, 3.0F, 1.2F);
        target.sendSystemMessage(Component.literal("§4☄️ Orbital strike!"));
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(net.minecraft.world.entity.Entity e) {
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (trialId != null) {
            tag.putUUID("TrialId", trialId);
        }
        tag.putBoolean("Announced", announced);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("TrialId")) {
            trialId = tag.getUUID("TrialId");
        }
        announced = tag.getBoolean("Announced");
    }
}
