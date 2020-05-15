package jihuayu.patchoulitask.handler;

import jihuayu.patchoulitask.task.NearPositionTaskPage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.ALL;

@Mod.EventBusSubscriber
public class NearHandler {
    public static double time = 0;
    public static void onRenderGameOverlayEvent(RenderGameOverlayEvent.Pre event){
        if (event.getType() != ALL)return;
        time += event.getPartialTicks();
        if (time <0)return;
        time = 0;
        PlayerEntity player = Minecraft.getInstance().player;
        for(NearPositionTaskPage i : NearPositionTaskPage.POSES){
            if(player.getPosition().distanceSq(i.x,i.y,i.z,true)<=i.range){

            }
        }
    }


}
