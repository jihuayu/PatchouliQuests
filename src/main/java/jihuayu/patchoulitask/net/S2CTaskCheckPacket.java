package jihuayu.patchoulitask.net;

import jihuayu.patchoulitask.net.kiwi.Packet;
import jihuayu.patchoulitask.task.BaseTaskPage;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import java.util.function.Supplier;

public class S2CTaskCheckPacket extends Packet {
    public boolean ok;
    public boolean hide;
    public boolean lock;
    public ResourceLocation book;
    public ResourceLocation entry;
    public int page;

    public S2CTaskCheckPacket(ResourceLocation book, ResourceLocation entry, boolean ok, int page,boolean hide,boolean lock) {
        this.book = book;
        this.entry = entry;
        this.ok = ok;
        this.page = page;
        this.hide = hide;
        this.lock = lock;
    }

    public static class Handler extends PacketHandler<S2CTaskCheckPacket> {

        @Override
        public void encode(S2CTaskCheckPacket msg, PacketBuffer buffer) {
            buffer.writeResourceLocation(msg.book);
            buffer.writeResourceLocation(msg.entry);
            buffer.writeBoolean(msg.ok);
            buffer.writeInt(msg.page);
            buffer.writeBoolean(msg.hide);
            buffer.writeBoolean(msg.lock);
        }

        @Override
        public S2CTaskCheckPacket decode(PacketBuffer buffer) {
            ResourceLocation book = buffer.readResourceLocation();
            ResourceLocation entry = buffer.readResourceLocation();
            boolean ok = buffer.readBoolean();
            int page = buffer.readInt();
            boolean hide = buffer.readBoolean();
            boolean lock = buffer.readBoolean();
            return new S2CTaskCheckPacket(book, entry, ok, page,hide,lock);
        }

        @Override
        public void handle(S2CTaskCheckPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Book book = ItemModBook.getBook(ItemModBook.forBook(message.book));
                BookPage i = book.contents.entries.get(message.entry).getPages().get(message.page);
                if (i instanceof BaseTaskPage) {
                    ((BaseTaskPage) i).stats = message.ok ? 1 : -1;
                    ((BaseTaskPage) i).lock = message.lock;
                    ((BaseTaskPage) i).hide = message.hide;
                }
            });
            ctx.get().setPacketHandled(true);
        }

    }
}
