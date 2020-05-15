package jihuayu.patchoulitask;

import com.mojang.brigadier.CommandDispatcher;
import jihuayu.patchoulitask.net.*;
import jihuayu.patchoulitask.net.collect.C2SCollectTaskCheckPacket;
import jihuayu.patchoulitask.net.collect.C2SCollectTaskSyncPacket;
import jihuayu.patchoulitask.net.collect.S2CCollectTaskCheckPacket;
import jihuayu.patchoulitask.net.kiwi.NetworkChannel;
import jihuayu.patchoulitask.task.CollectTaskPage;
import net.minecraft.command.CommandSource;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vazkii.patchouli.client.book.ClientBookRegistry;

import static jihuayu.patchoulitask.ModMain.MOD_ID;

@Mod(MOD_ID)
@Mod.EventBusSubscriber
public class ModMain {
    public static final String MOD_ID = "patchouliquests";
    public static final Logger LOGGER = LogManager.getLogger();
    public static CommandDispatcher<CommandSource> COMMANDS;
    static {
        ClientBookRegistry.INSTANCE.pageTypes.put("collect_task", CollectTaskPage.class);
    }

    public ModMain() {
        NetworkChannel.register(C2SCollectTaskCheckPacket.class, new C2SCollectTaskCheckPacket.Handler());
        NetworkChannel.register(C2STaskSyncPacket.class, new C2STaskSyncPacket.Handler());
        NetworkChannel.register(S2CTaskCheckPacket.class, new S2CTaskCheckPacket.Handler());
        NetworkChannel.register(C2SCollectTaskSyncPacket.class, new C2SCollectTaskSyncPacket.Handler());
        NetworkChannel.register(S2CCollectTaskCheckPacket.class, new S2CCollectTaskCheckPacket.Handler());
    }
    @SubscribeEvent
    protected static void serverInit(FMLServerStartingEvent event) {
        COMMANDS = event.getCommandDispatcher();
    }
}
