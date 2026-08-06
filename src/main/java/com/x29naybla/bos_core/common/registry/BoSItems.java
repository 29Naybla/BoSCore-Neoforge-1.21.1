package com.x29naybla.bos_core.common.registry;

import com.x29naybla.bos_core.BoSCore;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class BoSItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BoSCore.MODID);



    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }

    public static final Supplier<Item> SHADOW_PUMPKIN_PIE = ITEMS.register("shadow_pumpkin_pie",
            () -> new BlockItem(BoSBlocks.PUMPKIN_PIE.get(), new Item.Properties()) {
                @Override
                public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag isAdvanced) {
                    tooltip.add(Component.literal("You naughty player ;)").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                }
            });
}
