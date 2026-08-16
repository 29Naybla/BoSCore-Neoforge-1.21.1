package com.x29naybla.bos_core;

import com.x29naybla.bos_core.client.gui.OvenScreen;
import com.x29naybla.bos_core.common.registry.BoSMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = BoSCore.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = BoSCore.MODID, value = Dist.CLIENT)
public class BoSCoreClient {
    public BoSCoreClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(BoSMenuTypes.OVEN_MENU.get(), OvenScreen::new);
    }
}
