package org.cfpa.i18nupdatemod.notice;

import net.minecraft.client.gui.GuiButton;

import java.awt.*;
import java.net.URI;

public class NoticeButton extends GuiButton {
    public NoticeButton(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, 200, 20, buttonText);
    }

    public void mouseReleased(int mouseX, int mouseY) {
        String url = "https://github.com/CFPAOrg/Minecraft-Mod-Language-Package#%E4%BB%93%E5%BA%93%E8%AF%B4%E6%98%8E";
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
