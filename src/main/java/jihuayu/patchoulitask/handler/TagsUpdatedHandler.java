package jihuayu.patchoulitask.handler;

import jihuayu.patchoulitask.old.task.kill.KillTypeTaskPage;
import jihuayu.patchoulitask.old.task.near.NearPositionTaskPage;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class TagsUpdatedHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onTagsUpdatedEvent(TagsUpdatedEvent event) {
//        NearPositionTaskPage.LISTS.clear();
//        KillTypeTaskPage.LISTS.clear();
    }
}
