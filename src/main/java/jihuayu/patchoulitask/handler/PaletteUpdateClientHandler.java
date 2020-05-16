package jihuayu.patchoulitask.handler;

import jihuayu.patchoulitask.net.C2SPaletteSyncPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vazkii.patchouli.api.BookContentsReloadEvent;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.ItemModBook;

import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.api.distmarker.Dist.CLIENT;
import static net.minecraftforge.api.distmarker.Dist.DEDICATED_SERVER;

@Mod.EventBusSubscriber(CLIENT)
public class PaletteUpdateClientHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onTagUpdateEventClient(BookContentsReloadEvent event){
        new C2SPaletteSyncPacket(event.book).send();
    }
}
