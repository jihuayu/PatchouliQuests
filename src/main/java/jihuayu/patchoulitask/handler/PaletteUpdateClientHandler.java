package jihuayu.patchoulitask.handler;

import jihuayu.patchoulitask.old.net.C2SPaletteSyncPacket;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vazkii.patchouli.api.BookContentsReloadEvent;

import static net.minecraftforge.api.distmarker.Dist.CLIENT;

@Mod.EventBusSubscriber(CLIENT)
public class PaletteUpdateClientHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onTagUpdateEventClient(BookContentsReloadEvent event) {
        new C2SPaletteSyncPacket(event.book).send();
    }
}
