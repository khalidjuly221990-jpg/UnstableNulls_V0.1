package com.typoman.unstablenulls.entity;

import com.typoman.unstablenulls.entity.goal.NullTargetGoal;
import com.typoman.unstablenulls.entity.goal.RegroupGoal;
import com.typoman.unstablenulls.entity.goal.RangedSupportGoal;
import com.typoman.unstablenulls.entity.goal.ShieldWallGoal;
import com.typoman.unstablenulls.entity.goal.SwarmGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Random;
import java.util.UUID;

public class NullEntity extends Monster {

    private UUID trialId = null;

    public NullEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setCustomName(Component.literal(genRandomNullName()));
        this.setCustomNameVisible(false);
    }

    public void setTrialId(UUID id) {
        this.trialId = id;
    }

    public UUID getTrialId() {
        return trialId;
    }

    private static String genRandomNullName() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_#@$!^&";
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(r.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(2, new SwarmGoal(this, 1.1D));
        this.goalSelector.addGoal(3, new ShieldWallGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new RangedSupportGoal(this));
        this.goalSelector.addGoal(5, new RegroupGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 24.0F));

        // Trial Nulls are isolated to their exact trial owner.
        // Manual-spawned Nulls still target the nearest player normally.
        this.targetSelector.addGoal(1, new NullTargetGoal(this));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (trialId != null) {
            tag.putUUID("TrialId", trialId);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("TrialId")) {
            trialId = tag.getUUID("TrialId");
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 2.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }
}
