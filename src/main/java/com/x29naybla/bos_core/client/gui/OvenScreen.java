package com.x29naybla.bos_core.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.x29naybla.bos_core.BoSCore;
import com.x29naybla.bos_core.common.block.entity.container.OvenMenu;
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
    protected void init() {
        super.init();
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - imageHeight) /2;
        int y = (height - imageHeight) /2;

        guiGraphics.blit(GUI_TEXTURE, x-5, y, 0, 0, imageWidth, imageHeight);

        renderBakingProgress(guiGraphics, x-5, y);
        renderBurnProgress(guiGraphics, x-5, y);
    }

    private void renderBakingProgress(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isLit()) {
            guiGraphics.blit(GUI_TEXTURE, x + 90, y + 35, 176, 14, menu.getBurnProgress(), 17);
        }
    }

    private void renderBurnProgress(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isFueled()){
            float currentHeight = menu.getLitTime();
            int offset = (int) (15 - currentHeight);
            guiGraphics.blit(GUI_TEXTURE, x + 124, y + 39 + offset, 176, offset, 14, (int) currentHeight);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, (this.imageHeight - 96 + 2), 4210752, false);
    }
}
