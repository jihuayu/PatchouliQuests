package jihuayu.patchoulitask.net;

import jihuayu.patchoulitask.net.kiwi.Packet;
import jihuayu.patchoulitask.task.BaseTaskPage;
import jihuayu.patchoulitask.util.BookNBTHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import java.util.function.Supplier;

public class S2CLockTaskPacket extends Packet {
    ResourceLocation book;
    ResourceLocation entry;
    int id;
    boolean lock;

    public S2CLockTaskPacket(ResourceLocation book, ResourceLocation entry, int id, boolean lock) {
        this.book = book;
        this.entry = entry;
        this.id = id;
        this.lock = lock;
    }

    public static class Handler extends PacketHandler<S2CLockTaskPacket> {

        @Override
        public void encode(S2CLockTaskPacket msg, PacketBuffer buffer) {
            buffer.writeResourceLocation(msg.book);
            buffer.writeResourceLocation(msg.entry);
            buffer.writeInt(msg.id);
            buffer.writeBoolean(msg.lock);
        }

        @Override
        public S2CLockTaskPacket decode(PacketBuffer buffer) {
            ResourceLocation book = buffer.readResourceLocation();
            ResourceLocation entry = buffer.readResourceLocation();
            int page = buffer.readInt();
            boolean lock = buffer.readBoolean();
            return new S2CLockTaskPacket(book, entry, page,lock);
        }

        @Override
        public void handle(S2CLockTaskPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Book book = ItemModBook.getBook(ItemModBook.forBook(message.book));
                BookPage i = BookNBTHelper.getPage(book.contents.entries.get(message.entry).getPages(),message.id);
                if (i instanceof BaseTaskPage) {
                    ((BaseTaskPage) i).lock = message.lock;
                }
            });
            ctx.get().setPacketHandled(true);
        }

    }
}
