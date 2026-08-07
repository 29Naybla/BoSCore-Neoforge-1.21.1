package com.x29naybla.bos_core.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.renderer.entity.CatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.animal.Cat;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(CatRenderer.class)
public abstract class CatRendererMixin extends MobRenderer<Cat, CatModel<Cat>> {

    public CatRendererMixin(EntityRendererProvider.Context context, CatModel<Cat> model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    /**
     * @author 29Naybla
     * @reason Pixel consistent cats
     */
    @Overwrite
    protected void scale(@NotNull Cat livingEntity, @NotNull PoseStack poseStack, float partialTickTime) {
        super.scale(livingEntity, poseStack, partialTickTime);
        poseStack.scale(1, 1, 1);
    }
}
