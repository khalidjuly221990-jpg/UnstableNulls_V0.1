package com.typoman.unstablenulls.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.typoman.unstablenulls.UnstableNulls;
import com.typoman.unstablenulls.entity.ModEntities;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = UnstableNulls.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    public static final KeyMapping OPEN_KIT = new KeyMapping(
            "key.unstablenulls.open_kit",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "key.categories.unstablenulls"
    );

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.NULL.get(), NullRenderer::new);
        event.registerEntityRenderer(ModEntities.SPOKE_BOT.get(), SpokeBotRenderer::new);
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(OPEN_KIT);
    }
}
