package org.cfpa.i18nupdatemod.notice;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.cfpa.i18nupdatemod.I18nUpdateMod;
import org.cfpa.i18nupdatemod.I18nConfig;

@Mod.EventBusSubscriber(modid = I18nUpdateMod.MODID)
public class ShowNoticeFirst {
    public static boolean shouldShowNotice = false;

    @SubscribeEvent
    public static void onPlayerFirstJoin(RenderGameOverlayEvent.Post event) {
        if (shouldShowNotice && event.getType() != RenderGameOverlayEvent.ElementType.HELMET && I18nConfig.notice.showNoticeConfig) {
            shouldShowNotice = false;
            new NoticeShower();
        }
    }
}
