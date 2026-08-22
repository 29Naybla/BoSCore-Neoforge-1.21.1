package com.x29naybla.bos_core.mixin;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.RabbitModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.animal.Rabbit;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

@Mixin(RabbitModel.class)
public abstract class RabbitModelMixin<T extends Rabbit> extends EntityModel<T> {
    @Mutable
    @Final
    @Shadow
    private final ModelPart leftRearFoot;
    @Mutable
    @Final
    @Shadow
    private final ModelPart rightRearFoot;
    @Mutable
    @Final
    @Shadow
    private final ModelPart leftHaunch;
    @Mutable
    @Final
    @Shadow
    private final ModelPart rightHaunch;
    @Mutable
    @Final
    @Shadow
    private final ModelPart body;
    @Mutable
    @Final
    @Shadow
    private final ModelPart leftFrontLeg;
    @Mutable
    @Final
    @Shadow
    private final ModelPart rightFrontLeg;
    @Mutable
    @Final
    @Shadow
    private final ModelPart head;
    @Mutable
    @Final
    @Shadow
    private final ModelPart rightEar;
    @Mutable
    @Final
    @Shadow
    private final ModelPart leftEar;
    @Mutable
    @Final
    @Shadow
    private final ModelPart tail;
    @Mutable
    @Final
    @Shadow
    private final ModelPart nose;

    protected RabbitModelMixin(ModelPart leftRearFoot, ModelPart rightRearFoot, ModelPart leftHaunch, ModelPart rightHaunch, ModelPart body, ModelPart leftFrontLeg, ModelPart rightFrontLeg, ModelPart head, ModelPart rightEar, ModelPart leftEar, ModelPart tail, ModelPart nose) {
        this.leftRearFoot = leftRearFoot;
        this.rightRearFoot = rightRearFoot;
        this.leftHaunch = leftHaunch;
        this.rightHaunch = rightHaunch;
        this.body = body;
        this.leftFrontLeg = leftFrontLeg;
        this.rightFrontLeg = rightFrontLeg;
        this.head = head;
        this.rightEar = rightEar;
        this.leftEar = leftEar;
        this.tail = tail;
        this.nose = nose;
    }

    /**
     * @author 29Naybla
     * @reason Scaling up rabbits to be pixel consistent (hopefully)
     */
    @Overwrite
    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        if (this.young) {
            float f = 1.5F;
            poseStack.pushPose();
            poseStack.translate(0.0F, 0.27F, 0.0F);
            ImmutableList.of(this.head, this.leftEar, this.rightEar, this.nose)
                    .forEach(p_349855_ -> p_349855_.render(poseStack, buffer, packedLight, packedOverlay, color));
            poseStack.popPose();
            poseStack.pushPose();
            poseStack.scale(0.6F, 0.6F, 0.6F);
            poseStack.translate(0.0F, 1F, 0.0F);
            ImmutableList.of(
                            this.leftRearFoot, this.rightRearFoot, this.leftHaunch, this.rightHaunch, this.body, this.leftFrontLeg, this.rightFrontLeg, this.tail
                    )
                    .forEach(p_349849_ -> p_349849_.render(poseStack, buffer, packedLight, packedOverlay, color));
            poseStack.popPose();
        } else {
            poseStack.pushPose();
            ImmutableList.of(
                            this.leftRearFoot,
                            this.rightRearFoot,
                            this.leftHaunch,
                            this.rightHaunch,
                            this.body,
                            this.leftFrontLeg,
                            this.rightFrontLeg,
                            this.head,
                            this.rightEar,
                            this.leftEar,
                            this.tail,
                            this.nose
                    )
                    .forEach(p_349861_ -> p_349861_.render(poseStack, buffer, packedLight, packedOverlay, color));
            poseStack.popPose();
        }
    }
}
