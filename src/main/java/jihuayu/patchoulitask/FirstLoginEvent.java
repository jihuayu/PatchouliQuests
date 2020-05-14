package jihuayu.patchoulitask;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class FirstLoginEvent extends PlayerEvent {

    public FirstLoginEvent(PlayerEntity player) {
        super(player);
    }
}
