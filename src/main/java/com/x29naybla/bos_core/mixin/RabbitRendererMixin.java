package com.x29naybla.bos_core.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.RabbitRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Rabbit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RabbitRenderer.class)
public class RabbitRendererMixin {

    @Inject(at= {@At("TAIL")}, method = {"getTextureLocation(Lnet/minecraft/world/entity/animal/Rabbit;)Lnet/minecraft/resources/ResourceLocation;"}, cancellable = true)
    private void bos_getMoccaTexture(Rabbit entity, CallbackInfoReturnable<ResourceLocation> cir){
        if ("Mocca".equals(ChatFormatting.stripFormatting(entity.getName().getString()))) {
            cir.setReturnValue(ResourceLocation.withDefaultNamespace("textures/entity/rabbit/mocca.png"));
        }
    }
}
