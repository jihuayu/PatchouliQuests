package jihuayu.patchoulitask;

import jihuayu.patchoulitask.net.*;
import jihuayu.patchoulitask.net.kiwi.NetworkChannel;
import jihuayu.patchoulitask.task.CollectTaskPage;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vazkii.patchouli.client.book.ClientBookRegistry;

import static jihuayu.patchoulitask.ModMain.MOD_ID;

@Mod(MOD_ID)
public class ModMain {
    public static final String MOD_ID = "patchouliquests";
    public static final Logger LOGGER = LogManager.getLogger();
    static {
        ClientBookRegistry.INSTANCE.pageTypes.put("collect_task", CollectTaskPage.class);
    }

    public ModMain(){
        NetworkChannel.register(C2SCollectTaskCheckPacket.class, new C2SCollectTaskCheckPacket.Handler());
        NetworkChannel.register(C2STaskSyncPacket.class, new C2STaskSyncPacket.Handler());
        NetworkChannel.register(S2CTaskCheckPacket.class, new S2CTaskCheckPacket.Handler());
        NetworkChannel.register(C2SCollectTaskSyncPacket.class, new C2SCollectTaskSyncPacket.Handler());
        NetworkChannel.register(S2CCollectTaskCheckPacket.class, new S2CCollectTaskCheckPacket.Handler());
    }
}
