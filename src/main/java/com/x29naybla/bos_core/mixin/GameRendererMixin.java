package com.x29naybla.bos_core.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.server.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.file.Path;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements AutoCloseable {

    @Shadow
    public abstract Minecraft getMinecraft();

    @Shadow
    private void takeAutoScreenshot(Path path) {}

    /**
     * @author 29Naybla
     * @reason Always making a new icon when the player leaves the world
     */
    @Overwrite
    private void tryTakeScreenshotIfNeeded(){
        IntegratedServer integratedserver = this.getMinecraft().getSingleplayerServer();

        if (this.getMinecraft().isLocalServer() && integratedserver != null && integratedserver.isCurrentlySaving()) {
            integratedserver.getWorldScreenshotFile().ifPresent(this::takeAutoScreenshot);
        }
    }
}
