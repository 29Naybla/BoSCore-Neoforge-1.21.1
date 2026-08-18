package com.x29naybla.bos_core;

import com.x29naybla.bos_core.common.registry.*;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(BoSCore.MODID)
public class BoSCore {
    public static final String MODID = "bos_core";

    public BoSCore(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);

        BoSItems.register(modEventBus);
        BoSBlocks.register(modEventBus);
        BoSBlockEntities.register(modEventBus);
        BoSMenuTypes.register(modEventBus);
        BoSRecipes.register(modEventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }
}
