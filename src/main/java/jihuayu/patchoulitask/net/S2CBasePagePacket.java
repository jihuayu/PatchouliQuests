package jihuayu.patchoulitask.net;

import jihuayu.patchoulitask.net.kiwi.Packet;
import jihuayu.patchoulitask.page.PageBaseQuest;
import jihuayu.patchoulitask.util.BookNBTHelper;
import jihuayu.patchoulitask.util.BufferHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import java.util.function.Supplier;

public class S2CBasePagePacket extends Packet {
    ResourceLocation book;
    ResourceLocation entry;
    int id;
    int stats;
    boolean hide;
    boolean lock;

    public S2CBasePagePacket(ResourceLocation book, ResourceLocation entry, int id, int stats, boolean hide, boolean lock) {
        this.book = book;
        this.entry = entry;
        this.id = id;
        this.stats = stats;
        this.hide = hide;
        this.lock = lock;
    }

    public static class Handler extends PacketHandler<S2CBasePagePacket> {

        @Override
        public void encode(S2CBasePagePacket msg, PacketBuffer buffer) {
            BufferHelper.writeTaskId(buffer, msg.book, msg.entry, msg.id);
            buffer.writeVarInt(msg.stats);
            buffer.writeBoolean(msg.hide);
            buffer.writeBoolean(msg.lock);
        }

        @Override
        public S2CBasePagePacket decode(PacketBuffer buffer) {
            BufferHelper.TaskRead i = BufferHelper.readTaskId(buffer);
            ResourceLocation book = i.book;
            ResourceLocation entry = i.entry;
            int page = i.id;
            int ok = buffer.readVarInt();
            boolean hide = buffer.readBoolean();
            boolean lock = buffer.readBoolean();
            return new S2CBasePagePacket(book, entry, page,ok,hide,lock);
        }

        @Override
        public void handle(S2CBasePagePacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Book book = ItemModBook.getBook(ItemModBook.forBook(message.book));
                PageBaseQuest page = BookNBTHelper.getPage(book.contents.entries.get(message.entry).getPages(), message.id);
                if (page!=null){
                    ( page).stats = message.stats;
                    ( page).hide = message.hide;
                    ( page).lock = message.lock;
                }
            });
            ctx.get().setPacketHandled(true);
        }

    }

}
