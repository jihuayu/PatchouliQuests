package jihuayu.patchoulitask;

import jihuayu.patchoulitask.net.C2STaskSyncPacket;
import jihuayu.patchoulitask.net.S2CTaskCheckPacket;
import jihuayu.patchoulitask.net.kiwi.NetworkChannel;
import jihuayu.patchoulitask.net.C2STaskCheckPacket;
import jihuayu.patchoulitask.task.CollectTaskPage;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
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
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModMainConfig.spec, MOD_ID+".toml");
        NetworkChannel.register(C2STaskCheckPacket.class, new C2STaskCheckPacket.Handler());
        NetworkChannel.register(C2STaskSyncPacket.class, new C2STaskSyncPacket.Handler());
        NetworkChannel.register(S2CTaskCheckPacket.class, new S2CTaskCheckPacket.Handler());

    }
}
