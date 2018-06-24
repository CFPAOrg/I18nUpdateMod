package org.cfpa.i18nupdatemod.notice;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.cfpa.i18nupdatemod.config.MainConfig;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.List;

@SideOnly(Side.CLIENT)
public class NoticeGui extends GuiScreen {
    private List<String> strings;
    private GuiButton noticeGithubButton;
    private GuiButton noticeCloseButton;

    public NoticeGui(List<String> strings) {
        this.strings = strings;
    }

    @Override
    public void initGui() {
        // 添加按钮
        if (MainConfig.notice.showWeblateButton) {
            noticeGithubButton = new GuiButton(0, this.width / 2 - 160, this.height * 75 / 100 + 8, 150, 20, "§l我想要参与模组翻译");
            buttonList.add(noticeGithubButton);
        }

        noticeCloseButton = new GuiButton(1, this.width / 2 + 8, this.height * 75 / 100 + 8, 150, 20, "§l关闭");
        buttonList.add(noticeCloseButton);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawGradientRect((int) (width * 0.1), (int) (height * 0.1), (int) (width * 0.9), (int) (height * 0.75), -0x3fefeff0, -0x2fefeff0);
        int h = (int) (this.height * 0.16);
        int w = (int) (this.width * 0.14);
        StringBuilder sb = new StringBuilder();
        strings.forEach(v -> sb.append(v).append('\n'));
        fontRenderer.drawSplitString(sb.toString(), w, h, (int) (width * 0.72), 0xffffff);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button == noticeCloseButton) {
            mc.displayGuiScreen(null);
            return;
        }

        if (button == noticeGithubButton) {
            String url = "https://cfpa.team";
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
