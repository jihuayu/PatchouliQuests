package jihuayu.patchoulitask.handler;

import jihuayu.patchoulitask.FirstLoginEvent;
import jihuayu.patchoulitask.ModMainConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class LoginHandler {
    @SubscribeEvent
    public static void onClone(PlayerEvent.PlayerLoggedInEvent event){
        ModMainConfig.refresh();
        PlayerEntity player = event.getPlayer();
        if(!player.getPersistentData().getBoolean("ancestralwealth.first_login")){
            for (ItemStack i : ModMainConfig.items){
                player.addItemStackToInventory(i);
            }
            MinecraftForge.EVENT_BUS.post(new FirstLoginEvent(player));
            player.getPersistentData().putBoolean("ancestralwealth.first_login",true);
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event){
        event.getEntityLiving().getPersistentData().putBoolean("ancestralwealth.first_login",true);
    }
}
