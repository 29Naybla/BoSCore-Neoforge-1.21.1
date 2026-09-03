package com.x29naybla.bos_core.mixin;

import com.x29naybla.bos_core.common.registry.BoSBlocks;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ShovelItem.class)
public class ShovelItemMixin {

    @Inject(method = "getShovelPathingState", at = {@At(value = "HEAD")}, cancellable = true)
    private static void bos_getShovelPathingState(BlockState originalState, CallbackInfoReturnable<BlockState> cir) {
        if(originalState.is(Blocks.GRASS_BLOCK) || originalState.is(Blocks.PODZOL) || originalState.is(Blocks.MYCELIUM)) cir.setReturnValue(BoSBlocks.GRASS_PATH.get().defaultBlockState());
        else if(originalState.is(Blocks.MUD)) cir.setReturnValue(BoSBlocks.MUD_PATH.get().defaultBlockState());
        else if(originalState.is(Blocks.GRAVEL)) cir.setReturnValue(BoSBlocks.GRAVEL_PATH.get().defaultBlockState());
        else if(originalState.is(Blocks.SAND)) cir.setReturnValue(BoSBlocks.SAND_PATH.get().defaultBlockState());
        else if(originalState.is(Blocks.RED_SAND)) cir.setReturnValue(BoSBlocks.RED_SAND_PATH.get().defaultBlockState());
        else if(originalState.is(Blocks.SNOW_BLOCK)) cir.setReturnValue(BoSBlocks.SNOW_PATH.get().defaultBlockState());
    }
}
