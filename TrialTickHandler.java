package com.typoman.unstablenulls.trial;

import com.typoman.unstablenulls.UnstableNulls;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = UnstableNulls.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TrialTickHandler {

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            FarlandsTrialManager.tick();
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        FarlandsTrialManager.clearAll();
    }
}
