package com.x29naybla.bos_core;

import com.x29naybla.bos_core.common.registry.BoSBlocks;
import com.x29naybla.bos_core.common.registry.BoSItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = BoSCore.MODID)
public class BoSEvents {
    public static final int DEFAULT_NAME_TAG_ITEM_COLOR = 14927242;
    public static final int DEFAULT_NAME_COLOR = FastColor.ARGB32.color(255, 255, 255);

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
    public static void pieStacksToOne(ModifyDefaultComponentsEvent event) {
        event.modify((Items.PUMPKIN_PIE), (builder) -> builder.set(DataComponents.MAX_STACK_SIZE, 1));
    }

    @SubscribeEvent
    public static void onEntitySize(EntityEvent.Size e) {
        Entity entity = e.getEntity();

        if ((entity instanceof Rabbit)) {
            e.setNewSize(e.getOldSize().scale(1.67F));
            if(((Rabbit) entity).isBaby()) e.setNewSize(e.getOldSize().scale(0.6F));
        }
    }

    @SubscribeEvent
    public static void entityAttributeModification(EntityAttributeModificationEvent event) {
        event.add(EntityType.RABBIT, Attributes.JUMP_STRENGTH, 0.5);
        event.add(EntityType.RABBIT, Attributes.SAFE_FALL_DISTANCE, 6);
    }

    @SubscribeEvent
    public static void registerItemColorHandlers(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> tintIndex > 0 ? -1 : FastColor.ARGB32.opaque(DyedItemColor.getOrDefault(stack, DEFAULT_NAME_TAG_ITEM_COLOR)),
                Items.NAME_TAG);
        /*
        event.register((stack, tintIndex) -> {
            BlockState blockstate = ((BlockItem)stack.getItem()).getBlock().defaultBlockState();
            return event.getBlockColors().getColor(blockstate, null, null, tintIndex);
        },
                BoSBlocks.APPLE_LEAVES,
                BoSBlocks.FLOWERING_APPLE_LEAVES);
         */
    }

    /*
    @SubscribeEvent
    public static void registerBlockColorHandlers(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> level != null && pos != null ? BiomeColors.getAverageFoliageColor(level, pos) : FoliageColor.getDefaultColor(),
                BoSBlocks.APPLE_LEAVES.get(),
                BoSBlocks.FLOWERING_APPLE_LEAVES.get());
    }
     */

    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.insertAfter(Blocks.CHERRY_LOG.asItem().getDefaultInstance(), BoSBlocks.APPLE_LOG.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        } else if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS){
            event.insertAfter(Blocks.SAND.asItem().getDefaultInstance(), BoSBlocks.SAND_PATH.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(Blocks.RED_SAND.asItem().getDefaultInstance(), BoSBlocks.RED_SAND_PATH.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(Blocks.SNOW_BLOCK.asItem().getDefaultInstance(), BoSBlocks.SNOW_PATH.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(Blocks.MUD.asItem().getDefaultInstance(), BoSBlocks.MUD_PATH.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(Blocks.GRAVEL.asItem().getDefaultInstance(), BoSBlocks.GRAVEL_PATH.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(Blocks.CHERRY_LOG.asItem().getDefaultInstance(), BoSBlocks.APPLE_LOG.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(Blocks.CHERRY_LEAVES.asItem().getDefaultInstance(), BoSBlocks.APPLE_LEAVES.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(BoSBlocks.APPLE_LEAVES.toStack(), BoSBlocks.FLOWERING_APPLE_LEAVES.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(Blocks.CHERRY_SAPLING.asItem().getDefaultInstance(), BoSItems.APPLE_SEEDS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        } else if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.insertAfter(Blocks.FURNACE.asItem().getDefaultInstance(), BoSBlocks.OVEN.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        } else if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS){
            event.insertAfter(Items.PUMPKIN_PIE.getDefaultInstance(), BoSBlocks.APPLE_PIE.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(BoSBlocks.APPLE_PIE.toStack(), BoSBlocks.BERRY_PIE.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(BoSBlocks.BERRY_PIE.toStack(), BoSBlocks.MEAT_PIE.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }
}
