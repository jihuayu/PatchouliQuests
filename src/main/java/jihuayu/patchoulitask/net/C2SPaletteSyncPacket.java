package jihuayu.patchoulitask.net;

//import jihuayu.patchoulitask.handler.PaletteUpdateHandler;

import com.google.common.collect.Lists;
import jihuayu.patchoulitask.net.kiwi.ClientPacket;
import jihuayu.patchoulitask.util.PaletteHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import java.util.ArrayList;
import java.util.function.Supplier;

public class C2SPaletteSyncPacket extends ClientPacket {
    public ResourceLocation book;

    public C2SPaletteSyncPacket(ResourceLocation book) {
        this.book = book;
    }

    public static class Handler extends PacketHandler<C2SPaletteSyncPacket> {

        @Override
        public void encode(C2SPaletteSyncPacket msg, PacketBuffer buffer) {
            buffer.writeResourceLocation(msg.book);
        }

        @Override
        public C2SPaletteSyncPacket decode(PacketBuffer buffer) {
            ResourceLocation book = buffer.readResourceLocation();
            return new C2SPaletteSyncPacket(book);
        }

        @Override
        public void handle(C2SPaletteSyncPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Book book = ItemModBook.getBook(ItemModBook.forBook(message.book));
                PaletteHelper.PALETTE_LIST.put(message.book, Lists.newArrayList(book.contents.entries.keySet()));
                new S2CPaletteSyncPacket(message.book, PaletteHelper.PALETTE_LIST.getOrDefault(message.book, new ArrayList<>())).send(ctx.get().getSender());
            });
            ctx.get().setPacketHandled(true);
        }

    }
}
