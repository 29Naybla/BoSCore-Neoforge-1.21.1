package com.x29naybla.bos_core.common.registry;

import com.x29naybla.bos_core.BoSCore;
import com.x29naybla.bos_core.common.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BoSBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(BoSCore.MODID);

    public static final DeferredBlock<Block> SAND_PATH = registerBlock("sand_path",
            () -> new BoSPathBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SAND), Blocks.SAND.defaultBlockState()));
    public static final DeferredBlock<Block> RED_SAND_PATH = registerBlock("red_sand_path",
            () -> new BoSPathBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RED_SAND), Blocks.RED_SAND.defaultBlockState()));
    public static final DeferredBlock<Block> SNOW_PATH = registerBlock("snow_path",
            () -> new BoSPathBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SNOW_BLOCK), Blocks.SNOW_BLOCK.defaultBlockState()));
    public static final DeferredBlock<Block> MUD_PATH = registerBlock("mud_path",
            () -> new BoSPathBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD), Blocks.MUD.defaultBlockState()));
    public static final DeferredBlock<Block> GRAVEL_PATH = registerBlock("gravel_path",
            () -> new BoSPathBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GRAVEL), Blocks.GRAVEL.defaultBlockState()));

    public static final DeferredBlock<Block> PUMPKIN_PIE = BLOCKS.register("pumpkin_pie",
            () -> new PumpkinPieBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE)));
    public static final DeferredBlock<Block> MEAT_PIE = BLOCKS.register("meat_pie",
            () -> new MeatPieBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE)));
    public static final DeferredBlock<Block> APPLE_PIE = BLOCKS.register("apple_pie",
            () -> new ApplePieBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE)));
    public static final DeferredBlock<Block> BERRY_PIE = BLOCKS.register("berry_pie",
            () -> new BerryPieBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE)));

    public static final DeferredBlock<Block> OVEN = registerBlock("oven",
            () -> new OvenBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_RED)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(2.0F, 6.0F).lightLevel((blockstate) -> {
                        if (blockstate.getValue(OvenBlock.LIT)) {
                            return 15;
                        }
                        return 0;
                    })));

    public static final DeferredBlock<Block> APPLE_LOG = registerBlock("apple_log",
            () -> log(MapColor.COLOR_ORANGE, MapColor.STONE));
    public static final DeferredBlock<Block> APPLE_LEAVES = registerBlock("apple_leaves",
            BoSBlocks::leaves);
    public static final DeferredBlock<Block> APPLE_FLOWER = BLOCKS.register("apple_flower",
            () -> new AppleFlowersBlock(BlockBehaviour.Properties.of()
                    .noCollission()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.CROP)
                    .pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> FLOWERING_APPLE_LEAVES = registerBlock("flowering_apple_leaves",
            () -> fruitingLeaves(BoSBlocks.APPLE_FLOWER.get()));
    public static final DeferredBlock<Block> APPLE_SAPLING = BLOCKS.register("apple_sapling",
            () -> new SaplingBlock(BoSTrees.APPLE_TREE, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<Block> POTTED_APPLE_SAPLING = BLOCKS.register("potted_apple_sapling",
            () -> new FlowerPotBlock(APPLE_SAPLING.get(), BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));

    private static Block log(MapColor topMapColor, MapColor sideMapColor) {
        return new LogBlock(
                BlockBehaviour.Properties.of()
                        .mapColor(blockState -> blockState.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? topMapColor : sideMapColor)
                        .instrument(NoteBlockInstrument.BASS)
                        .strength(2.0F)
                        .sound(SoundType.WOOD)
                        .ignitedByLava()
        );
    }

    private static Block leaves() {
        return new LeavesFlammableBlock(
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.PLANT)
                        .strength(0.2F).randomTicks()
                        .sound(SoundType.GRASS).noOcclusion()
                        .isValidSpawn(Blocks::ocelotOrParrot)
                        .isSuffocating(BoSBlocks::never)
                        .isViewBlocking(BoSBlocks::never)
                        .ignitedByLava()
                        .pushReaction(PushReaction.DESTROY)
                        .isRedstoneConductor(BoSBlocks::never)
        );
    }

    private static Block fruitingLeaves(Block fruitCropBlock) {
        return new FruitingLeavesBlock(
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.PLANT)
                        .strength(0.2F)
                        .randomTicks()
                        .sound(SoundType.GRASS)
                        .noOcclusion()
                        .isValidSpawn(Blocks::ocelotOrParrot)
                        .isSuffocating(BoSBlocks::never)
                        .isViewBlocking(BoSBlocks::never)
                        .ignitedByLava()
                        .pushReaction(PushReaction.DESTROY)
                        .isRedstoneConductor(BoSBlocks::never),
                fruitCropBlock
            );
    }

    private static boolean never(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return false;
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block){
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block){
        BoSItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
