package jihuayu.patchoulitask.handler;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CloneHandler {

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (event.getOriginal().getPersistentData().get("patchouliquests") != null) {
            event.getEntityLiving().getPersistentData().put("patchouliquests", event.getOriginal().getPersistentData().get("patchouliquests"));
        }
    }
}
