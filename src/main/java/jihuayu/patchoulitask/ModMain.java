package jihuayu.patchoulitask;

import com.mojang.brigadier.CommandDispatcher;
import jihuayu.patchoulitask.comand.LockAndHideCommand;
import jihuayu.patchoulitask.net.*;
import jihuayu.patchoulitask.net.collect.C2SCollectTaskCheckPacket;
import jihuayu.patchoulitask.net.collect.C2SCollectTaskSyncPacket;
import jihuayu.patchoulitask.net.collect.S2CCollectTaskCheckPacket;
import jihuayu.patchoulitask.net.kill.C2SKillTypeTaskSyncPacket;
import jihuayu.patchoulitask.net.kill.S2CKillTypeTaskCheckPacket;
import jihuayu.patchoulitask.net.kiwi.NetworkChannel;
import jihuayu.patchoulitask.net.near.C2SNearPositionTaskCheckPacket;
import jihuayu.patchoulitask.net.near.C2SNearStructTaskCheckPacket;
import jihuayu.patchoulitask.task.CollectTaskPage;
import jihuayu.patchoulitask.task.kill.KillTypeTask;
import jihuayu.patchoulitask.task.near.NearPositionTaskPage;
import jihuayu.patchoulitask.task.near.NearStructTaskPage;
import net.minecraft.command.CommandSource;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
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
        ClientBookRegistry.INSTANCE.pageTypes.put("near_position_task", NearPositionTaskPage.class);
        ClientBookRegistry.INSTANCE.pageTypes.put("near_struct_task", NearStructTaskPage.class);
        ClientBookRegistry.INSTANCE.pageTypes.put("kill_entity_task", KillTypeTask.class);
    }

    public ModMain() {
        NetworkChannel.register(C2SCollectTaskSyncPacket.class, new C2SCollectTaskSyncPacket.Handler());
        NetworkChannel.register(C2SNearPositionTaskCheckPacket.class, new C2SNearPositionTaskCheckPacket.Handler());
        NetworkChannel.register(C2SRewardGetPacket.class, new C2SRewardGetPacket.Handler());
        NetworkChannel.register(C2SNearStructTaskCheckPacket.class, new C2SNearStructTaskCheckPacket.Handler());
        NetworkChannel.register(C2SPaletteSyncPacket.class, new C2SPaletteSyncPacket.Handler());
        NetworkChannel.register(C2SCollectTaskCheckPacket.class, new C2SCollectTaskCheckPacket.Handler());
        NetworkChannel.register(C2STaskSyncPacket.class, new C2STaskSyncPacket.Handler());
        NetworkChannel.register(C2SKillTypeTaskSyncPacket.class, new C2SKillTypeTaskSyncPacket.Handler());


        NetworkChannel.register(S2CTaskCheckPacket.class, new S2CTaskCheckPacket.Handler());
        NetworkChannel.register(S2CCollectTaskCheckPacket.class, new S2CCollectTaskCheckPacket.Handler());
        NetworkChannel.register(S2CLockTaskPacket.class, new S2CLockTaskPacket.Handler());
        NetworkChannel.register(S2CHideTaskPacket.class, new S2CHideTaskPacket.Handler());
        NetworkChannel.register(S2CPaletteSyncPacket.class, new S2CPaletteSyncPacket.Handler());
        NetworkChannel.register(S2CKillTypeTaskCheckPacket.class, new S2CKillTypeTaskCheckPacket.Handler());
    }

    @SubscribeEvent
    protected static void serverInit(FMLServerStartingEvent event) {
        COMMANDS = event.getCommandDispatcher();
        LockAndHideCommand.register(COMMANDS);
    }
}
