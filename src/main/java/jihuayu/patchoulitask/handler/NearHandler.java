package jihuayu.patchoulitask.handler;

import jihuayu.patchoulitask.net.nearpostition.C2SNearPositionTaskCheckPacket;
import jihuayu.patchoulitask.task.NearPositionTaskPage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.ALL;

@Mod.EventBusSubscriber
public class NearHandler {
    public static double time = 0;
    @SubscribeEvent
    public static void onRenderGameOverlayEvent(RenderGameOverlayEvent.Pre event){
        try {
            if (event.getType() != ALL)return;
            time += event.getPartialTicks();
            if (time <1)return;
            time = 0;
            PlayerEntity player = Minecraft.getInstance().player;
            for(NearPositionTaskPage i : NearPositionTaskPage.POSES){
                if (i.stats>0 || i.lock)continue;
                if(player.getPosition().distanceSq(i.x,i.y,i.z,true)<=i.range){
                    new C2SNearPositionTaskCheckPacket(i.book.id,i.parent.getEntry().getId(),i.id).send();
                }
            }
        }catch (Exception ignore){
        }

    }
}
