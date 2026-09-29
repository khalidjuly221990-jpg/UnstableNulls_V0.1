package com.typoman.unstablenulls.sound;

import com.typoman.unstablenulls.UnstableNulls;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, UnstableNulls.MOD_ID);

    public static void register(IEventBus bus) {
        SOUNDS.register(bus);
    }
}
