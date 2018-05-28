package org.cfpa.i18nupdatemod.notice;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.List;

@SideOnly(Side.CLIENT)
public class NoticeGui extends GuiScreen {
    private final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    private List<String> strings;
    private GuiButton noticeGithubButton;
    private GuiButton noticeCloseButton;

    public NoticeGui(List<String> strings) {
        this.strings = strings;
    }

    @Override
    public void initGui() {
        super.initGui();
        // 绘制按钮
        noticeGithubButton = new GuiButton(0, this.width / 2 - 160, this.height * 75 / 100 + 8, 150, 20, "§l我想要参与模组翻译");
        noticeCloseButton = new GuiButton(1, this.width / 2 + 8, this.height * 75 / 100 + 8, 150, 20, "§l关闭");
        buttonList.add(noticeGithubButton);
        buttonList.add(noticeCloseButton);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawGradientRect((int) (this.width * 0.1), (int) (this.height * 0.1), (int) (this.width * 0.9), (int) (this.height * 0.75), -0x3fefeff0, -0x2fefeff0);
        int h = (int) (this.height * 0.16);
        int w = (int) (this.width * 0.14);
        StringBuilder sb = new StringBuilder();
        strings.forEach(v -> sb.append(v).append('\n'));
        fontRenderer.drawSplitString(sb.toString(), w, h, (int) (this.width * 0.72), 0xffffff);

        // 修正鼠标显示
        Mouse.setGrabbed(false);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == noticeCloseButton) {
            mc.displayGuiScreen(null);
            return;
        }

        if (button == noticeGithubButton) {
            String url = "https://github.com/CFPAOrg/Minecraft-Mod-Language-Package#%E4%BB%93%E5%BA%93%E8%AF%B4%E6%98%8E";
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
