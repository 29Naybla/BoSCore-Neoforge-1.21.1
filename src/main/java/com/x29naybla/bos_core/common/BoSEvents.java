package com.x29naybla.bos_core.common;

import com.x29naybla.bos_core.BoSCore;
import com.x29naybla.bos_core.common.registry.BoSBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = BoSCore.MODID)
public class BoSEvents {

    @SubscribeEvent
    public static void pathMaking(PlayerInteractEvent.RightClickBlock event){
        if(event.getItemStack().is(ItemTags.SHOVELS)){
            BlockState state = event.getLevel().getBlockState(event.getPos());
            if(state.is(Blocks.SAND)){
                turnIntoPath(event.getEntity(), BoSBlocks.SAND_PATH.get().defaultBlockState(), event.getLevel(), event.getPos());
            } else if (state.is(Blocks.RED_SAND)) {
                turnIntoPath(event.getEntity(), BoSBlocks.RED_SAND_PATH.get().defaultBlockState(), event.getLevel(), event.getPos());
            }else if (state.is(Blocks.SNOW_BLOCK)) {
                turnIntoPath(event.getEntity(), BoSBlocks.SNOW_PATH.get().defaultBlockState(), event.getLevel(), event.getPos());
            }else if(state.is(Blocks.MUD)){
                turnIntoPath(event.getEntity(), BoSBlocks.MUD_PATH.get().defaultBlockState(), event.getLevel(), event.getPos());
            } else if (state.is(Blocks.GRAVEL)) {
                turnIntoPath(event.getEntity(),  BoSBlocks.GRAVEL_PATH.get().defaultBlockState(), event.getLevel(), event.getPos());
            }
        }
    }

    public static void turnIntoPath(Player player, BlockState state, Level level, BlockPos pos){
        level.setBlockAndUpdate(pos, state);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state));
        level.playSound(player, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS);
        player.swing(player.getUsedItemHand());
        player.getItemInHand(player.getUsedItemHand()).hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        player.awardStat(Stats.ITEM_USED.get(player.getItemInHand(player.getUsedItemHand()).getItem()));
    }

    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS){
            event.insertAfter(Blocks.SAND.asItem().getDefaultInstance(), BoSBlocks.SAND_PATH.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(Blocks.RED_SAND.asItem().getDefaultInstance(), BoSBlocks.RED_SAND_PATH.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(Blocks.SNOW_BLOCK.asItem().getDefaultInstance(), BoSBlocks.SNOW_PATH.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(Blocks.MUD.asItem().getDefaultInstance(), BoSBlocks.MUD_PATH.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(Blocks.GRAVEL.asItem().getDefaultInstance(), BoSBlocks.GRAVEL_PATH.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }
}
