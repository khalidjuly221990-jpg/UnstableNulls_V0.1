package com.typoman.unstablenulls.item;

import com.typoman.unstablenulls.UnstableNulls;
import com.typoman.unstablenulls.entity.ModEntities;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = UnstableNulls.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, UnstableNulls.MOD_ID);

    public static final RegistryObject<Item> NULL_SPAWN_EGG = ITEMS.register("null_spawn_egg",
            () -> new ForgeSpawnEggItem(() -> ModEntities.NULL.get(), 0x000000, 0xFFFFFF, new Item.Properties()));

    public static final RegistryObject<Item> SPOKE_BOT_SPAWN_EGG = ITEMS.register("spoke_bot_spawn_egg",
            () -> new ForgeSpawnEggItem(() -> ModEntities.SPOKE_BOT.get(), 0xFF0000, 0x000000, new Item.Properties()));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }

    @SubscribeEvent
    public static void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(NULL_SPAWN_EGG);
            event.accept(SPOKE_BOT_SPAWN_EGG);
        }
    }
}
