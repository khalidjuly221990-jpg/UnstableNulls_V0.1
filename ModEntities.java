package com.typoman.unstablenulls.entity;

import com.typoman.unstablenulls.UnstableNulls;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = UnstableNulls.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, UnstableNulls.MOD_ID);

    public static final RegistryObject<EntityType<NullEntity>> NULL =
            ENTITIES.register("null", () -> EntityType.Builder
                    .of(NullEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.8F)
                    .clientTrackingRange(6)
                    .updateInterval(3)
                    .build("null"));

    public static final RegistryObject<EntityType<SpokeBotEntity>> SPOKE_BOT =
            ENTITIES.register("spoke_bot", () -> EntityType.Builder
                    .of(SpokeBotEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 2.2F)
                    .clientTrackingRange(10)
                    .build("spoke_bot"));

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(NULL.get(), NullEntity.createAttributes().build());
        event.put(SPOKE_BOT.get(), SpokeBotEntity.createAttributes().build());
    }
}
