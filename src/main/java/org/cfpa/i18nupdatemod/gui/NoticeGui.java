package org.cfpa.i18nupdatemod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

public class NoticeGui extends GuiScreen {
    private final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    private final String text = "琪露诺是是湖附近妖精们的领袖，力量也比其他妖精强。她是属于好战型的，有操控冷气的能力，能瞬间冻结小东西，比普通的妖精更危险。\n\n而她最常说的一句话就是“あたいってば最強ね！”（本小姐最强），尤其是在对战中赢了之后。所以总是四处找人挑战，但与幻想乡的一众怪物级人物相比，再强的妖精也远远不够格进入幻想乡强者之列……（所以也会用“幻想乡最强”来指代琪露诺）\n\n";

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawGradientRect((int) (this.width * 0.1), (int) (this.height * 0.1), (int) (this.width * 0.9), (int) (this.height * 0.9), -1072689136, -804253680);
        fontRenderer.drawSplitString(text, (int) (this.width * 0.12), (int) (this.height * 0.14), (int) (this.width * 0.76), 0xffffff);
    }
}
