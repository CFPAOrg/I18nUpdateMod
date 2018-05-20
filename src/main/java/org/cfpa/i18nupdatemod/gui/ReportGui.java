package org.cfpa.i18nupdatemod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

public class ReportGui extends GuiScreen {

    private final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawGradientRect((int) (this.width * 0.1), (int) (this.height * 0.1), (int) (this.width * 0.9), (int) (this.height * 0.9), -1072689136, -804253680);
    }

}
