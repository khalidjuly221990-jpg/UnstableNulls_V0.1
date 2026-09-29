package com.typoman.unstablenulls.network;

import com.typoman.unstablenulls.UnstableNulls;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(UnstableNulls.MOD_ID, "main"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, KitSelectPacket.class,
                KitSelectPacket::encode, KitSelectPacket::decode, KitSelectPacket::handle);
    }
}
