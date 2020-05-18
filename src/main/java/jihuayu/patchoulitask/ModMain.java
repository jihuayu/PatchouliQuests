package jihuayu.patchoulitask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import jihuayu.patchoulitask.comand.LockAndHideCommand;
import jihuayu.patchoulitask.comand.TestCommand;
import jihuayu.patchoulitask.net.*;
import jihuayu.patchoulitask.net.reward.item.C2SItemRewardPacket;
import jihuayu.patchoulitask.net.reward.item.S2CItemRewardPacket;
import jihuayu.patchoulitask.net.kiwi.NetworkChannel;
import jihuayu.patchoulitask.net.palette.C2SPaletteSyncPacket;
import jihuayu.patchoulitask.net.palette.S2CPaletteSyncPacket;
import jihuayu.patchoulitask.net.task.item.C2SItemTaskCheckPacket;
import jihuayu.patchoulitask.net.task.item.S2CItemTaskPacket;
import jihuayu.patchoulitask.page.PageBaseQuest;
import jihuayu.patchoulitask.page.reward.BaseReward;
import jihuayu.patchoulitask.page.task.BaseTask;
import jihuayu.patchoulitask.util.ReflectHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.util.SerializationUtil;

import static jihuayu.patchoulitask.ModMain.MOD_ID;

@Mod(MOD_ID)
@Mod.EventBusSubscriber
public class ModMain {
    public static final String MOD_ID = "patchouliquests";
    public static final Logger LOGGER = LogManager.getLogger();
    public static CommandDispatcher<CommandSource> COMMANDS;

    static {
        ClientBookRegistry.INSTANCE.pageTypes.put("task", PageBaseQuest.class);
    }

    public ModMain() {

        NetworkChannel.register(C2SItemRewardPacket.class, new C2SItemRewardPacket.Handler());
        NetworkChannel.register(C2SPaletteSyncPacket.class, new C2SPaletteSyncPacket.Handler());
        NetworkChannel.register(C2SAllSyncPacket.class, new C2SAllSyncPacket.Handler());
        NetworkChannel.register(S2CAllSyncPacket.class, new S2CAllSyncPacket.Handler());
        NetworkChannel.register(C2SBasePagePacket.class, new C2SBasePagePacket.Handler());
        NetworkChannel.register(S2CItemRewardPacket.class, new S2CItemRewardPacket.Handler());
        NetworkChannel.register(S2CPaletteSyncPacket.class, new S2CPaletteSyncPacket.Handler());
        NetworkChannel.register(S2CBasePagePacket.class, new S2CBasePagePacket.Handler());
        NetworkChannel.register(C2SItemTaskCheckPacket.class, new C2SItemTaskCheckPacket.Handler());
        NetworkChannel.register(S2CItemTaskPacket.class, new S2CItemTaskPacket.Handler());

        ModMain.LOGGER.info(MOD_ID + " init in " + EffectiveSide.get());



        {
//            ReflectHelper.setField(BookRegistry.class, "GSON", create(new GsonBuilder()
//                    .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())), BookRegistry.INSTANCE);
//            ReflectHelper.setField(ClientBookRegistry.class, "gson", create(new GsonBuilder()
//                    .registerTypeHierarchyAdapter(BookPage.class, new ClientBookRegistry.LexiconPageAdapter())
//                    .registerTypeHierarchyAdapter(TemplateComponent.class, new ClientBookRegistry.TemplateComponentAdapter())), ClientBookRegistry.INSTANCE);
            ReflectHelper.setField(SerializationUtil.class, "RAW_GSON", create(new GsonBuilder()
                    .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())), null);
        }

    }

    private Gson create(GsonBuilder builder) {
        return builder
                .registerTypeHierarchyAdapter(PageBaseQuest.class, new PageBaseQuest.Deserializer())
                .registerTypeHierarchyAdapter(BaseReward.class, new BaseReward.Deserializer())
                .registerTypeHierarchyAdapter(BaseTask.class, new BaseTask.Deserializer())
                .create();
    }

    @SubscribeEvent
    protected static void serverInit(FMLServerStartingEvent event) {
        COMMANDS = event.getCommandDispatcher();
        LockAndHideCommand.register(COMMANDS);
        TestCommand.register(COMMANDS);
    }
}
