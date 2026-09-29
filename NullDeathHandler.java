package com.typoman.unstablenulls.trial;

import com.typoman.unstablenulls.UnstableNulls;
import com.typoman.unstablenulls.entity.NullEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = UnstableNulls.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NullDeathHandler {

    @SubscribeEvent
    public static void onNullDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof NullEntity nullEntity) {
            FarlandsTrialManager.onNullKilled(nullEntity);
        }
    }
}
