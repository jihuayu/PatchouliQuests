package jihuayu.patchoulitask.old.net.cmd;

import jihuayu.patchoulitask.net.kiwi.Packet;
import jihuayu.patchoulitask.old.task.BaseTaskPage;
import jihuayu.patchoulitask.util.BookNBTHelper;
import jihuayu.patchoulitask.util.BufferHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import java.util.function.Supplier;

public class S2CCompleteTaskPacket extends Packet {
    ResourceLocation book;
    ResourceLocation entry;
    int id;
    boolean complete;

    public S2CCompleteTaskPacket(ResourceLocation book, ResourceLocation entry, int id, boolean complete) {
        this.book = book;
        this.entry = entry;
        this.id = id;
        this.complete = complete;
    }

    public static class Handler extends PacketHandler<S2CCompleteTaskPacket> {

        @Override
        public void encode(S2CCompleteTaskPacket msg, PacketBuffer buffer) {
            BufferHelper.writeTaskId(buffer, msg.book, msg.entry, msg.id);

            buffer.writeBoolean(msg.complete);
        }

        @Override
        public S2CCompleteTaskPacket decode(PacketBuffer buffer) {
            BufferHelper.TaskRead i = BufferHelper.readTaskId(buffer);
            ResourceLocation book = i.book;
            ResourceLocation entry = i.entry;
            int page = i.id;
            boolean lock = buffer.readBoolean();
            return new S2CCompleteTaskPacket(book, entry, page, lock);
        }

        @Override
        public void handle(S2CCompleteTaskPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Book book = ItemModBook.getBook(ItemModBook.forBook(message.book));
                BookPage i = BookNBTHelper.getPage(book.contents.entries.get(message.entry).getPages(), message.id);
                if (i instanceof BaseTaskPage) {
                    ((BaseTaskPage) i).stats = message.complete?1:-1;
                }
            });
            ctx.get().setPacketHandled(true);
        }

    }
}
