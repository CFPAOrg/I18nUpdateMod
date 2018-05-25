package org.cfpa.i18nupdatemod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

public class NoticeGui extends GuiScreen {
    private final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    private List<String> strings;

    public NoticeGui(List<String> strings) {
        this.strings = strings;
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawGradientRect((int) (this.width * 0.1), (int) (this.height * 0.1), (int) (this.width * 0.9), (int) (this.height * 0.9), -1072689136, -804253680);
        int h = (int) (this.height * 0.14);
        StringBuilder sb = new StringBuilder();
        strings.forEach(v -> sb.append(v).append('\n'));
        fontRenderer.drawSplitString(sb.toString(), (int) (this.width * 0.12), h, (int) (this.width * 0.76), 0xffffff);
    }
}
