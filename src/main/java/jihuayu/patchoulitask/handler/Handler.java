package jihuayu.patchoulitask.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vazkii.patchouli.client.book.gui.GuiBook;

@Mod.EventBusSubscriber
public class Handler {
    @SubscribeEvent
    public static void onDrawScreenEvent(GuiScreenEvent.DrawScreenEvent.Pre event){

    }
    @SubscribeEvent
    public static void onDrawScreenEvent(GuiScreenEvent.DrawScreenEvent.Post event){

    }
}
