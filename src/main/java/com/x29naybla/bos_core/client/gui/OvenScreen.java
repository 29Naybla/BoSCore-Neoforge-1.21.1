package com.x29naybla.bos_core.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.x29naybla.bos_core.BoSCore;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class OvenScreen extends AbstractContainerScreen<OvenMenu> {
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(BoSCore.MODID, "textures/gui/oven_gui.png");

    public OvenScreen(OvenMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - imageHeight) /2;
        int y = (height - imageHeight) /2;

        guiGraphics.blit(GUI_TEXTURE, x-5, y, 0, 0, imageWidth, imageHeight);

        renderBurnProgress(guiGraphics, x-5, y);
    }

    private void renderBurnProgress(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isLit()) {
            guiGraphics.blit(GUI_TEXTURE, x + 90, y + 35, 176, 14, menu.getBurnProgress(), 17);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
