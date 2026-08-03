package com.x29naybla.bos_core.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.x29naybla.bos_core.common.entity.LeatherCollarMixinAccess;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CatCollarLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cat;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

@Mixin(CatCollarLayer.class)
public abstract class CatRendererMixin extends RenderLayer<Cat, CatModel<Cat>> {
    @Shadow
    private static final ResourceLocation CAT_COLLAR_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/cat/cat_collar.png");
    @Mutable
    @Final
    @Shadow
    private final CatModel<Cat> catModel;

    public CatRendererMixin(RenderLayerParent<Cat, CatModel<Cat>> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.catModel = new CatModel<>(modelSet.bakeLayer(ModelLayers.CAT_COLLAR));
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
            Cat livingEntity,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        if (livingEntity.isTame()) {
            int i = ((LeatherCollarMixinAccess)livingEntity).bos_getLeatherCollarColor();
            coloredCutoutModelCopyLayerRender(
                    this.getParentModel(),
                    this.catModel,
                    CAT_COLLAR_LOCATION,
                    poseStack,
                    buffer,
                    packedLight,
                    livingEntity,
                    limbSwing,
                    limbSwingAmount,
                    ageInTicks,
                    netHeadYaw,
                    headPitch,
                    partialTicks,
                    i
            );
        }
    }
}
