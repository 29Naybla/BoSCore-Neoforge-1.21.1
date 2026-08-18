package com.x29naybla.bos_core.common.registry;

import com.x29naybla.bos_core.BoSCore;
import com.x29naybla.bos_core.common.block.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
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

    public static final DeferredBlock<Block> PUMPKIN_PIE = registerBlock("pumpkin_pie",
            () -> new PumpkinPieBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE)));
    public static final DeferredBlock<Block> MEAT_PIE = registerBlock("meat_pie",
            () -> new MeatPieBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE)));
    public static final DeferredBlock<Block> APPLE_PIE = registerBlock("apple_pie",
            () -> new ApplePieBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE)));
    public static final DeferredBlock<Block> BERRY_PIE = registerBlock("berry_pie",
            () -> new BerryPieBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE)));

    public static final DeferredBlock<Block> OVEN = registerBlock("oven",
            () -> new OvenBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(2.0F, 6.0F).lightLevel((blockstate) -> {
                        if (blockstate.getValue(OvenBlock.LIT)) {
                            return 15;
                        }
                        return 0;
                    })));

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
