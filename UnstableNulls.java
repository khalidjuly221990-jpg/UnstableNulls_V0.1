package com.typoman.unstablenulls;

import com.mojang.logging.LogUtils;
import com.typoman.unstablenulls.entity.ModEntities;
import com.typoman.unstablenulls.item.ModItems;
import com.typoman.unstablenulls.network.ModNetwork;
import com.typoman.unstablenulls.sound.ModSounds;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(UnstableNulls.MOD_ID)
public class UnstableNulls {
    public static final String MOD_ID = "unstablenulls";
    public static final Logger LOGGER = LogUtils.getLogger();

    public UnstableNulls() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEntities.register(bus);
        ModItems.register(bus);
        ModSounds.register(bus);
        ModNetwork.register();

        LOGGER.info("UnstableNulls loaded. 10,000 Nulls incoming. Respect the lore.");
    }
}
