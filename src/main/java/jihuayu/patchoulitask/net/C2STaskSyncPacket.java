package jihuayu.patchoulitask.net;

import jihuayu.patchoulitask.net.kiwi.ClientPacket;
import jihuayu.patchoulitask.task.BaseTaskPage;
import jihuayu.patchoulitask.util.BookNBTHelper;
import jihuayu.patchoulitask.util.NBTHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import java.util.function.Supplier;

public class C2STaskSyncPacket extends ClientPacket {
    ResourceLocation book;
    ResourceLocation entry;
    int page;

    public C2STaskSyncPacket(ResourceLocation book, ResourceLocation entry, int page) {
        this.book = book;
        this.entry = entry;
        this.page = page;
    }

    public static class Handler extends PacketHandler<C2STaskSyncPacket> {

        @Override
        public void encode(C2STaskSyncPacket msg, PacketBuffer buffer) {
            buffer.writeResourceLocation(msg.book);
            buffer.writeResourceLocation(msg.entry);
            buffer.writeInt(msg.page);
        }

        @Override
        public C2STaskSyncPacket decode(PacketBuffer buffer) {
            ResourceLocation book = buffer.readResourceLocation();
            ResourceLocation entry = buffer.readResourceLocation();
            int page = buffer.readInt();
            return new C2STaskSyncPacket(book, entry, page);
        }

        @Override
        public void handle(C2STaskSyncPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Book book = ItemModBook.getBook(ItemModBook.forBook(message.book));
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null) return;
                BookPage i = book.contents.entries.get(message.entry).getPages().get(message.page);
                if (i instanceof BaseTaskPage) {
                    boolean over = BookNBTHelper.isOver(player,message.book.toString(),message.entry.toString(),message.page);
                    boolean hide = BookNBTHelper.isHide(player,message.book.toString(),message.entry.toString(),message.page);
                    boolean lock = BookNBTHelper.isLock(player,message.book.toString(),message.entry.toString(),message.page);
                    new S2CTaskCheckPacket(message.book,message.entry,over,message.page,hide,lock).send(player);
                }
            });
            ctx.get().setPacketHandled(true);
        }

    }

}
