package com.typoman.unstablenulls.world;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;

import java.util.Random;

public class FarlandArena {

    public static void generate(ServerLevel level, BlockPos center) {
        Random rand = new Random();
        for (int i = 0; i < 10; i++) {
            BlockPos crater = center.offset(rand.nextInt(60) - 30, 0, rand.nextInt(60) - 30);
            spawnCrater(level, crater, 3);
        }

        if (level.hasChunkAt(center.above())) {
            level.setBlock(center.above(), Blocks.LODESTONE.defaultBlockState(), 3);
        }

        level.getServer().getPlayerList().broadcastSystemMessage(
                Component.literal("§5§l🌌 Farland Echoes: §r§7The past replays."), false);
    }

    private static void spawnCrater(ServerLevel level, BlockPos center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int distSq = x * x + z * z;
                if (distSq > radius * radius) continue;
                int depth = (int) Math.max(0, radius - Math.sqrt(distSq));
                for (int y = 0; y <= depth; y++) {
                    BlockPos pos = center.offset(x, -y, z);
                    if (level.hasChunkAt(pos) && !level.isEmptyBlock(pos)) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
        }
    }
}
