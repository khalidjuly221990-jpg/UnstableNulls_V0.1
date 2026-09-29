package com.typoman.unstablenulls.trial;

import com.typoman.unstablenulls.entity.ModEntities;
import com.typoman.unstablenulls.entity.NullEntity;
import com.typoman.unstablenulls.entity.SpokeBotEntity;
import com.typoman.unstablenulls.kit.Kit;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

import java.util.*;

public class FarlandsTrialManager {

    private static final int TRIAL_DURATION_TICKS = 12000;
    private static final int TOTAL_NULL_TARGET = 10000;
    private static final int MAX_ACTIVE_NULLS = 200;
    private static final int WAVE_SIZE = 40;
    private static final int WAVE_INTERVAL = 40;
    private static final int ARENA_RADIUS = 96;

    private static final Map<UUID, TrialSession> ACTIVE = new HashMap<>();
    private static final Map<UUID, UUID> PLAYER_TO_TRIAL = new HashMap<>();

    public static class TrialSession {
        public final UUID trialId;
        public final UUID playerId;
        public final ServerLevel level;
        public final BlockPos center;
        public final AABB arena;
        public final long startTick;
        public UUID spokeBotId;
        public int totalSpawned;
        public int nullsDefeated;
        public final Set<UUID> activeNulls = new HashSet<>();
        public boolean cannonActivated;
        public boolean finished;

        public TrialSession(UUID trialId, UUID playerId, ServerLevel level, BlockPos center, long startTick) {
            this.trialId = trialId;
            this.playerId = playerId;
            this.level = level;
            this.center = center;
            this.arena = new AABB(center).inflate(ARENA_RADIUS);
            this.startTick = startTick;
        }
    }

    public static boolean isPlayerInTrial(UUID playerId) {
        return PLAYER_TO_TRIAL.containsKey(playerId);
    }

    public static TrialSession getSessionByTrial(UUID trialId) {
        return ACTIVE.get(trialId);
    }

    public static TrialSession getSessionByPlayer(UUID playerId) {
        UUID trialId = PLAYER_TO_TRIAL.get(playerId);
        if (trialId == null) return null;
        return ACTIVE.get(trialId);
    }

    public static void startTrial(ServerPlayer player, BlockPos center) {
        if (PLAYER_TO_TRIAL.containsKey(player.getUUID())) {
            player.sendSystemMessage(Component.literal("§cYou already have a trial running."));
            return;
        }

        ServerLevel level = player.serverLevel();
        FarlandArena.generate(level, center);

        UUID trialId = UUID.randomUUID();
        TrialSession session = new TrialSession(trialId, player.getUUID(), level, center, level.getGameTime());

        // Register the session before the first server tick can process the entity.
        ACTIVE.put(trialId, session);
        PLAYER_TO_TRIAL.put(player.getUUID(), trialId);

        SpokeBotEntity bot = ModEntities.SPOKE_BOT.get().create(level);
        if (bot != null) {
            bot.moveTo(center.getX() + 5, center.getY() + 1, center.getZ() + 5, 0F, 0F);
            bot.setTrialId(trialId);
            if (level.addFreshEntity(bot)) {
                session.spokeBotId = bot.getUUID();
            } else {
                bot.discard();
            }
        }

        player.sendSystemMessage(Component.literal("§5§l🌌 Farlands Trial Started!"));
        player.sendSystemMessage(Component.literal("§e⏱️ Survive 10 minutes. Up to 10,000 Nulls will enter the trial."));
    }

    public static void onNullKilled(NullEntity nullEntity) {
        UUID trialId = nullEntity.getTrialId();
        if (trialId == null) return;

        TrialSession session = ACTIVE.get(trialId);
        if (session == null || session.finished) return;

        if (session.activeNulls.remove(nullEntity.getUUID())) {
            session.nullsDefeated++;
        }
    }

    private static BlockPos findSafeSpawn(ServerLevel level, BlockPos origin) {
        for (int yOffset = 2; yOffset >= -2; yOffset--) {
            BlockPos pos = origin.offset(0, yOffset, 0);
            if (level.hasChunkAt(pos)
                    && level.isEmptyBlock(pos)
                    && level.isEmptyBlock(pos.above())
                    && !level.isEmptyBlock(pos.below())) {
                return pos;
            }
        }
        return null;
    }

    private static void cleanupMissingNulls(TrialSession s) {
        Iterator<UUID> iterator = s.activeNulls.iterator();
        while (iterator.hasNext()) {
            UUID id = iterator.next();
            Entity entity = s.level.getEntity(id);
            if (!(entity instanceof NullEntity nullEntity) || !nullEntity.isAlive()) {
                iterator.remove();
            }
        }
    }

    public static void tick() {
        Iterator<Map.Entry<UUID, TrialSession>> it = ACTIVE.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, TrialSession> entry = it.next();
            TrialSession s = entry.getValue();
            if (s.finished) {
                it.remove();
                PLAYER_TO_TRIAL.remove(s.playerId);
                continue;
            }

            cleanupMissingNulls(s);

            ServerPlayer player = s.level.getServer().getPlayerList().getPlayer(s.playerId);
            if (player == null || player.isDeadOrDying()) {
                endTrial(s, false, "§c[Player died or left]");
                it.remove();
                PLAYER_TO_TRIAL.remove(s.playerId);
                continue;
            }

            // A trial belongs to the exact dimension it started in.
            if (player.serverLevel() != s.level) {
                endTrial(s, false, "§c[You changed dimensions]");
                it.remove();
                PLAYER_TO_TRIAL.remove(s.playerId);
                continue;
            }

            if (!s.arena.contains(player.position())) {
                endTrial(s, false, "§c[You left the arena]");
                it.remove();
                PLAYER_TO_TRIAL.remove(s.playerId);
                continue;
            }

            long elapsed = s.level.getGameTime() - s.startTick;

            if (elapsed < TRIAL_DURATION_TICKS) {
                if (elapsed % WAVE_INTERVAL == 0
                        && s.totalSpawned < TOTAL_NULL_TARGET
                        && s.activeNulls.size() < MAX_ACTIVE_NULLS) {
                    int want = Math.min(WAVE_SIZE,
                            Math.min(TOTAL_NULL_TARGET - s.totalSpawned,
                                    MAX_ACTIVE_NULLS - s.activeNulls.size()));
                    spawnWave(s, want);
                }
            } else {
                s.cannonActivated = fireCannons(s, player);
                endTrial(s, true, null);
                it.remove();
                PLAYER_TO_TRIAL.remove(s.playerId);
            }
        }
    }

    private static void spawnWave(TrialSession s, int count) {
        if (count <= 0) return;
        ServerLevel level = s.level;

        for (int i = 0; i < count; i++) {
            NullEntity n = ModEntities.NULL.get().create(level);
            if (n == null) continue;

            double dx = (level.random.nextDouble() - 0.5) * 60;
            double dz = (level.random.nextDouble() - 0.5) * 60;

            BlockPos origin = BlockPos.containing(
                    s.center.getX() + dx,
                    s.center.getY() + 1,
                    s.center.getZ() + dz
            );

            BlockPos spawnPos = findSafeSpawn(level, origin);
            if (spawnPos == null) {
                n.discard();
                continue;
            }

            n.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                    level.random.nextFloat() * 360F, 0F);
            n.setTrialId(s.trialId);

            if (level.addFreshEntity(n)) {
                s.activeNulls.add(n.getUUID());
                s.totalSpawned++;
            } else {
                n.discard();
            }
        }
    }

    private static boolean fireCannons(TrialSession s, ServerPlayer player) {
        if (s.spokeBotId == null) return false;
        Entity e = s.level.getEntity(s.spokeBotId);
        if (e instanceof SpokeBotEntity bot && bot.isAlive()) {
            bot.fireOrbitalStrike(player);
            player.sendSystemMessage(Component.literal("§4§l🛰️ ORBITAL STRIKE CANNONS: FIRED"));
            return true;
        } else {
            player.sendSystemMessage(Component.literal("§7[No Spoke Bot detected. Trial ends quietly.]"));
        }
        return false;
    }

    private static void discardTrialNulls(TrialSession s) {
        for (UUID id : new HashSet<>(s.activeNulls)) {
            Entity entity = s.level.getEntity(id);
            if (entity instanceof NullEntity n) n.discard();
        }
        s.activeNulls.clear();
    }

    private static String formatTime(long ticks) {
        long totalSeconds = ticks / 20;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    private static void endTrial(TrialSession s, boolean passed, String overrideMsg) {
        s.finished = true;
        discardTrialNulls(s);

        if (s.spokeBotId != null) {
            Entity e = s.level.getEntity(s.spokeBotId);
            if (e != null) e.discard();
        }

        ServerPlayer player = s.level.getServer().getPlayerList().getPlayer(s.playerId);
        if (player == null) return;

        if (player.serverLevel() == s.level) {
            Kit.reset(player);
        }

        long elapsed = s.level.getGameTime() - s.startTick;
        String timeSurvived = formatTime(Math.min(elapsed, TRIAL_DURATION_TICKS));

        if (overrideMsg != null) {
            player.sendSystemMessage(Component.literal(overrideMsg));
        } else {
            String name = player.getName().getString();
            if (passed) {
                player.sendSystemMessage(Component.literal("§a§l" + name + " PASSED"));
            } else {
                player.sendSystemMessage(Component.literal("§c§l" + name + " FAILED"));
            }
            player.sendSystemMessage(Component.literal(
                    "§7Nulls defeated: §f" + s.nullsDefeated +
                    " §7| Time survived: §f" + timeSurvived));
        }
    }

    public static void forceStopTrial(ServerPlayer player) {
        UUID trialId = PLAYER_TO_TRIAL.get(player.getUUID());
        if (trialId != null) {
            TrialSession session = ACTIVE.get(trialId);
            if (session != null && !session.finished) {
                endTrial(session, false, "§c[Trial force-stopped]");
            }
            ACTIVE.remove(trialId);
            PLAYER_TO_TRIAL.remove(player.getUUID());
        }
    }

    public static void clearAll() {
        for (TrialSession s : ACTIVE.values()) {
            discardTrialNulls(s);
            if (s.spokeBotId != null) {
                Entity e = s.level.getEntity(s.spokeBotId);
                if (e != null) e.discard();
            }
        }
        ACTIVE.clear();
        PLAYER_TO_TRIAL.clear();
    }
}
