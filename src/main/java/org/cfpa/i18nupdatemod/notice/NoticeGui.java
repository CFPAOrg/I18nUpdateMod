package org.cfpa.i18nupdatemod.notice;

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
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawGradientRect((int) (this.width * 0.1), (int) (this.height * 0.1), (int) (this.width * 0.9), (int) (this.height * 0.8), -0x3fefeff0, -0x2fefeff0);
        int h = (int) (this.height * 0.16);
        int w = (int) (this.width * 0.14);
        StringBuilder sb = new StringBuilder();
        strings.forEach(v -> sb.append(v).append('\n'));
        fontRenderer.drawSplitString(sb.toString(), w, h, (int) (this.width * 0.72), 0xffffff);

        // 绘制按钮
        NoticeButton noticeButton = new NoticeButton(0, (this.width - 200) / 2, this.height * 8 / 10 + 3, "§l我想要参与模组翻译");
        this.addButton(noticeButton);
    }
}
