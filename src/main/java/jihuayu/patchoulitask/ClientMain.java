package jihuayu.patchoulitask;

import jihuayu.patchoulitask.net.C2SPaletteSyncPacket;
import jihuayu.patchoulitask.net.C2SRewardGetPacket;
import jihuayu.patchoulitask.net.C2STaskSyncPacket;
import jihuayu.patchoulitask.net.S2CPaletteSyncPacket;
import jihuayu.patchoulitask.net.collect.C2SCollectTaskCheckPacket;
import jihuayu.patchoulitask.net.collect.C2SCollectTaskSyncPacket;
import jihuayu.patchoulitask.net.kiwi.NetworkChannel;
import jihuayu.patchoulitask.net.near.C2SNearPositionTaskCheckPacket;
import jihuayu.patchoulitask.net.near.C2SNearStructTaskCheckPacket;
import jihuayu.patchoulitask.task.CollectTaskPage;
import jihuayu.patchoulitask.task.kill.KillTypeTask;
import jihuayu.patchoulitask.task.near.NearPositionTaskPage;
import jihuayu.patchoulitask.task.near.NearStructTaskPage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import vazkii.patchouli.client.book.ClientBookRegistry;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientMain {

}
