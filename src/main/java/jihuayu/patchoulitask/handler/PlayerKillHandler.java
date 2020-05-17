package jihuayu.patchoulitask.handler;

import jihuayu.patchoulitask.old.net.kill.S2CKillTypeTaskCheckPacket;
import jihuayu.patchoulitask.old.task.kill.KillTypeTaskPage;
import jihuayu.patchoulitask.util.BookNBTHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;

@Mod.EventBusSubscriber
public class PlayerKillHandler {
    @SubscribeEvent
    public static void onLivingDeathEvent(LivingDeathEvent event) {
        Entity p2 = event.getSource().getTrueSource();
        Entity d = event.getEntity();
        for (KillTypeTaskPage i : KillTypeTaskPage.LISTS) {
            if (i.name.equals(d.getType().getRegistryName().toString())) {
                if (p2 != null) {
                    test(p2, i);
                }
            }
        }
    }

    private static void test(Entity p2, KillTypeTaskPage i) {
        if (p2 instanceof PlayerEntity) {
            System.out.println(p2);
            System.out.println(i.book.id);
            if (!BookNBTHelper.isOver((PlayerEntity) p2, i.book.id.toString(), i.getEntry().getId().toString(), i.id)) {
                int pp = BookNBTHelper.getKillNum((PlayerEntity) p2, i.book.id.toString(), i.getEntry().getId().toString(), i.id);
                BookNBTHelper.setKillNum((PlayerEntity) p2, i.book.id.toString(), i.getEntry().getId().toString(), i.id, pp + 1);
                boolean hide = BookNBTHelper.isHide((PlayerEntity) p2, i.book.id.toString(), i.getEntry().getId().toString(), i.id);
                boolean lock = BookNBTHelper.isLock((PlayerEntity) p2, i.book.id.toString(), i.getEntry().getId().toString(), i.id);
                ArrayList<Boolean> list1 = new ArrayList<>();
                for (int j = 0; j < (i).reward.size(); j++) {
                    list1.add(BookNBTHelper.getRewardStats((PlayerEntity) p2, i.book.id.toString(), i.getEntry().getId().toString(), i.id, j));
                }
                if (pp + 1 >= i.num) {
                    BookNBTHelper.setOver((PlayerEntity) p2, i.book.id.toString(), i.getEntry().getId().toString(), i.id, true);
                    new S2CKillTypeTaskCheckPacket(i.book.id, i.getEntry().getId(), i.id, true, hide, lock, list1, pp + 1);
                } else {
                    BookNBTHelper.setOver((PlayerEntity) p2, i.book.id.toString(), i.getEntry().getId().toString(), i.id, false);
                    new S2CKillTypeTaskCheckPacket(i.book.id, i.getEntry().getId(), i.id, false, hide, lock, list1, pp + 1);

                }
            }

        }
    }
}
