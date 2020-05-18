package jihuayu.patchoulitask.net;

import io.netty.buffer.Unpooled;
import jihuayu.patchoulitask.net.kiwi.ClientPacket;
import jihuayu.patchoulitask.page.PageBaseQuest;
import jihuayu.patchoulitask.page.reward.BaseReward;
import jihuayu.patchoulitask.page.task.BaseTask;
import jihuayu.patchoulitask.util.PaletteHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import java.util.function.Supplier;

public class C2SAllSyncPacket extends ClientPacket {

    public C2SAllSyncPacket() {
    }

    public static class Handler extends PacketHandler<C2SAllSyncPacket> {

        @Override
        public void encode(C2SAllSyncPacket msg, PacketBuffer buffer) {
        }

        @Override
        public C2SAllSyncPacket decode(PacketBuffer buffer) {
            return new C2SAllSyncPacket();
        }

        @Override
        public void handle(C2SAllSyncPacket message, Supplier<NetworkEvent.Context> ctx) {
            if (ctx.get().getSender() == null) return;
            ServerPlayerEntity player = ctx.get().getSender();
            for (ResourceLocation i : PaletteHelper.BOOK_LIST) {
                Book book = ItemModBook.getBook(ItemModBook.forBook(i));
                for (ResourceLocation j : book.contents.entries.keySet()) {
                    BookEntry entry = book.contents.entries.get(j);
                    for (BookPage page : entry.getPages()) {
                        if (page instanceof PageBaseQuest) {
                            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
                            ((PageBaseQuest) page).writeBuffer(buffer,player);
                            for (BaseTask k : ((PageBaseQuest) page).tasks) {
                                k.writeBuffer(buffer,player);
                            }
                            for (BaseReward k : ((PageBaseQuest) page).rewards) {
                                k.writeBuffer(buffer,player);
                            }
                            new S2CAllSyncPacket(i, j,buffer).send(ctx.get().getSender());
                        }
                    }
                }
            }
            ctx.get().setPacketHandled(true);
        }

    }

}
