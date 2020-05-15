package jihuayu.patchoulitask.net;

import jihuayu.patchoulitask.net.kiwi.Packet;
import jihuayu.patchoulitask.task.BaseTaskPage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import java.util.function.Supplier;

public class S2CHideTaskPacket extends Packet {
    ResourceLocation book;
    ResourceLocation entry;
    int page;
    boolean hide;

    public S2CHideTaskPacket(ResourceLocation book, ResourceLocation entry, int page, boolean hide) {
        this.book = book;
        this.entry = entry;
        this.page = page;
        this.hide = hide;
    }

    public static class Handler extends PacketHandler<S2CHideTaskPacket> {

        @Override
        public void encode(S2CHideTaskPacket msg, PacketBuffer buffer) {
            buffer.writeResourceLocation(msg.book);
            buffer.writeResourceLocation(msg.entry);
            buffer.writeInt(msg.page);
            buffer.writeBoolean(msg.hide);
        }

        @Override
        public S2CHideTaskPacket decode(PacketBuffer buffer) {
            ResourceLocation book = buffer.readResourceLocation();
            ResourceLocation entry = buffer.readResourceLocation();
            int page = buffer.readInt();
            boolean lock = buffer.readBoolean();
            return new S2CHideTaskPacket(book, entry, page,lock);
        }

        @Override
        public void handle(S2CHideTaskPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Book book = ItemModBook.getBook(ItemModBook.forBook(message.book));
                BookPage i = book.contents.entries.get(message.entry).getPages().get(message.page);
                if (i instanceof BaseTaskPage) {
                    ((BaseTaskPage) i).hide = message.hide;
                }
            });
            ctx.get().setPacketHandled(true);
        }

    }
}
