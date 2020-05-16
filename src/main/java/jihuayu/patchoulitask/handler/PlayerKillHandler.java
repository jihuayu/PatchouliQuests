package jihuayu.patchoulitask.handler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PlayerKillHandler {
    @SubscribeEvent
    public static void onLivingDeathEvent(LivingDeathEvent event){
        Entity p1 = event.getSource().getImmediateSource();
        Entity p2 = event.getSource().getTrueSource();

    }
}
