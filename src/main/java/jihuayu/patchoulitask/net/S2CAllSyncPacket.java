package jihuayu.patchoulitask.net;

import jihuayu.patchoulitask.net.kiwi.Packet;
import jihuayu.patchoulitask.page.PageBaseQuest;
import jihuayu.patchoulitask.page.reward.BaseReward;
import jihuayu.patchoulitask.page.task.BaseTask;
import jihuayu.patchoulitask.util.BufferHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import java.util.function.Supplier;

public class S2CAllSyncPacket extends Packet {
    ResourceLocation book;
    ResourceLocation entry;
    PageBaseQuest page;
    PacketBuffer buffer;

    public S2CAllSyncPacket(ResourceLocation book, ResourceLocation entry, PageBaseQuest page) {
        this.book = book;
        this.entry = entry;
        this.page = page;
    }

    public S2CAllSyncPacket(ResourceLocation book, ResourceLocation entry, PacketBuffer buffer) {
        this.book = book;
        this.entry = entry;
        this.buffer = buffer;
    }

    public static class Handler extends PacketHandler<S2CAllSyncPacket> {

        @Override
        public void encode(S2CAllSyncPacket msg, PacketBuffer buffer) {
            BufferHelper.writeTask(buffer, msg.book, msg.entry);
            (msg.page).writeBuffer(buffer);
            for (BaseTask k : (msg.page).tasks) {
                k.writeBuffer(buffer);
            }
            for (BaseReward k : (msg.page).rewards) {
                k.writeBuffer(buffer);
            }
            System.out.println(2222);
        }

        @Override
        public S2CAllSyncPacket decode(PacketBuffer buffer) {
            BufferHelper.TaskRead i = BufferHelper.readTask(buffer);
            System.out.println(1111);
            return new S2CAllSyncPacket(i.book, i.entry, new PacketBuffer(buffer));
        }

        @Override
        public void handle(S2CAllSyncPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(
                    () -> {
                        Book book = ItemModBook.getBook(ItemModBook.forBook(message.book));
                        BookEntry entry = book.contents.entries.get(message.entry);
                        System.out.println(entry);
                        for (BookPage page : entry.getPages()) {
                            if (page instanceof PageBaseQuest) {
                                System.out.println(page);
                                ((PageBaseQuest) page).readBuffer(message.buffer);
                                for (BaseTask i : ((PageBaseQuest) page).tasks) {
                                    i.readBuffer(message.buffer);
                                }
                                for (BaseReward i : ((PageBaseQuest) page).rewards) {
                                    i.readBuffer(message.buffer);
                                }
                            }
                        }
                    }
            );
            ctx.get().setPacketHandled(true);
        }

    }

}
