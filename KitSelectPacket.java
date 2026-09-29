package com.typoman.unstablenulls.network;

import com.typoman.unstablenulls.kit.Kit;
import com.typoman.unstablenulls.trial.FarlandsTrialManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class KitSelectPacket {

    private final int kitOrdinal;

    public KitSelectPacket(int kitOrdinal) {
        this.kitOrdinal = kitOrdinal;
    }

    public static void encode(KitSelectPacket p, FriendlyByteBuf buf) {
        buf.writeInt(p.kitOrdinal);
    }

    public static KitSelectPacket decode(FriendlyByteBuf buf) {
        return new KitSelectPacket(buf.readInt());
    }

    public static void handle(KitSelectPacket p, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            if (!FarlandsTrialManager.isPlayerInTrial(player.getUUID())) {
                player.sendSystemMessage(Component.literal("§cYou are not in a Farlands trial."));
                return;
            }

            Kit[] kits = Kit.values();
            if (p.kitOrdinal < 0 || p.kitOrdinal >= kits.length) return;
            Kit kit = kits[p.kitOrdinal];

            Kit.reset(player);
            kit.apply(player);
            player.sendSystemMessage(Component.literal("§a🌀 " + kit.title + " §aequipped."));
        });
        context.setPacketHandled(true);
    }
}
