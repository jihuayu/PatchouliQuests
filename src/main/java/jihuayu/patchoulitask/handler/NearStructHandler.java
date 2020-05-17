package jihuayu.patchoulitask.handler;

import jihuayu.patchoulitask.old.net.near.C2SNearStructTaskCheckPacket;
import jihuayu.patchoulitask.old.task.near.NearPositionTaskPage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.ALL;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class NearStructHandler {
    public static double time = 0;

    @SubscribeEvent
    public static void onRenderGameOverlayEvent(RenderGameOverlayEvent.Pre event) {
        try {
            if (event.getType() != ALL) return;
            time += event.getPartialTicks();
            if (time < 10) return;
            time = 0;
            PlayerEntity player = Minecraft.getInstance().player;
            for (NearPositionTaskPage i : NearPositionTaskPage.LISTS) {
                if (i.stats > 0 || i.lock) continue;
                new C2SNearStructTaskCheckPacket(i.book.id, i.parent.getEntry().getId(), i.id).send();
            }
        } catch (Exception ignore) {
        }

    }
}
