package com.x29naybla.bos_core.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.x29naybla.bos_core.common.entity.LeatherCollarMixinAccess;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.WolfCollarLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WolfCollarLayer.class)
public abstract class WolfCollarLayerMixin extends RenderLayer<Wolf, WolfModel<Wolf>>{
    @Shadow
    private static final ResourceLocation WOLF_COLLAR_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_collar.png");

    public WolfCollarLayerMixin(RenderLayerParent<Wolf, WolfModel<Wolf>> renderer) {
        super(renderer);
    }

    /**
     * @author 29Naybla
     * @reason Make the renderer use leather collar color instead of vanilla's collar color
     */
    @Overwrite
    public void render(
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource buffer,
            int packedLight,
            Wolf livingEntity,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        if (livingEntity.isTame() && !livingEntity.isInvisible()) {
            int i = ((LeatherCollarMixinAccess)livingEntity).bos_getLeatherCollarColor();
            VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(WOLF_COLLAR_LOCATION));
            this.getParentModel().renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, i);
        }
    }
}
