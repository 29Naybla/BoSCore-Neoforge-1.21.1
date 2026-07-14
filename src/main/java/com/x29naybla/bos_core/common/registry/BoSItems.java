package com.x29naybla.bos_core.common.registry;

import com.x29naybla.bos_core.BoSCore;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BoSItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BoSCore.MODID);



    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
