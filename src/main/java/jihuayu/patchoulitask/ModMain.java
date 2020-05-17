package jihuayu.patchoulitask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import jihuayu.patchoulitask.comand.LockAndHideCommand;
import jihuayu.patchoulitask.old.net.*;
import jihuayu.patchoulitask.old.net.cmd.S2CHideTaskPacket;
import jihuayu.patchoulitask.old.net.cmd.S2CLockTaskPacket;
import jihuayu.patchoulitask.old.net.collect.C2SCollectTaskCheckPacket;
import jihuayu.patchoulitask.old.net.collect.C2SCollectTaskSyncPacket;
import jihuayu.patchoulitask.old.net.collect.S2CCollectTaskCheckPacket;
import jihuayu.patchoulitask.old.net.kill.C2SKillTypeTaskSyncPacket;
import jihuayu.patchoulitask.old.net.kill.S2CKillTypeTaskCheckPacket;
import jihuayu.patchoulitask.old.net.kiwi.NetworkChannel;
import jihuayu.patchoulitask.old.net.near.C2SNearPositionTaskCheckPacket;
import jihuayu.patchoulitask.old.net.near.C2SNearStructTaskCheckPacket;
import jihuayu.patchoulitask.old.task.CollectTaskPage;
import jihuayu.patchoulitask.old.task.kill.KillTypeTaskPage;
import jihuayu.patchoulitask.old.task.near.NearPositionTaskPage;
import jihuayu.patchoulitask.old.task.near.NearStructTaskPage;
import jihuayu.patchoulitask.page.PageBaseQuest;
import jihuayu.patchoulitask.page.reward.BaseReward;
import jihuayu.patchoulitask.page.reward.ItemReward;
import jihuayu.patchoulitask.util.ReflectHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.book.template.TemplateComponent;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.util.SerializationUtil;

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
        ClientBookRegistry.INSTANCE.pageTypes.put("near_block_task", NearStructTaskPage.class);
        ClientBookRegistry.INSTANCE.pageTypes.put("kill_entity_task", KillTypeTaskPage.class);
        ClientBookRegistry.INSTANCE.pageTypes.put("task", PageBaseQuest.class);
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

        ModMain.LOGGER.warn(MOD_ID+" init in "+EffectiveSide.get());

        NetworkChannel.register(S2CTaskCheckPacket.class, new S2CTaskCheckPacket.Handler());
        NetworkChannel.register(S2CCollectTaskCheckPacket.class, new S2CCollectTaskCheckPacket.Handler());
        NetworkChannel.register(S2CLockTaskPacket.class, new S2CLockTaskPacket.Handler());
        NetworkChannel.register(S2CHideTaskPacket.class, new S2CHideTaskPacket.Handler());
        NetworkChannel.register(S2CPaletteSyncPacket.class, new S2CPaletteSyncPacket.Handler());
        NetworkChannel.register(S2CKillTypeTaskCheckPacket.class, new S2CKillTypeTaskCheckPacket.Handler());
        ReflectHelper.setField(BookRegistry.class,"GSON",create(new GsonBuilder()
                .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())),BookRegistry.INSTANCE);
        ReflectHelper.setField(ClientBookRegistry.class,"gson", create(new GsonBuilder()
                .registerTypeHierarchyAdapter(BookPage.class, new ClientBookRegistry.LexiconPageAdapter())
                .registerTypeHierarchyAdapter(TemplateComponent.class, new ClientBookRegistry.TemplateComponentAdapter())),ClientBookRegistry.INSTANCE);
        ReflectHelper.setField(SerializationUtil.class,"RAW_GSON",create(new GsonBuilder()
                .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())),null);
    }

    private Gson create(GsonBuilder builder){
        return builder
                .registerTypeHierarchyAdapter(PageBaseQuest.class, new PageBaseQuest.Deserializer())
                .registerTypeHierarchyAdapter(BaseReward.class, new BaseReward.Deserializer())
                .create();
    }
    @SubscribeEvent
    protected static void serverInit(FMLServerStartingEvent event) {
        COMMANDS = event.getCommandDispatcher();
        LockAndHideCommand.register(COMMANDS);
    }
}
