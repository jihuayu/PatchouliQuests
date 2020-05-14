package jihuayu.patchoulitask.net;


import jihuayu.patchoulitask.net.kiwi.ClientPacket;
import jihuayu.patchoulitask.task.CollectTaskPage;
import jihuayu.patchoulitask.util.CheckUtil;
import jihuayu.patchoulitask.util.NBTHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import java.util.function.Supplier;

public class C2STaskCheckPacket extends ClientPacket {
    ResourceLocation book;
    ResourceLocation entry;
    boolean consume;
    int page;

    public C2STaskCheckPacket(ResourceLocation book, ResourceLocation entry, boolean consume, int page) {
        this.book = book;
        this.entry = entry;
        this.consume = consume;
        this.page = page;
    }

    public static class Handler extends PacketHandler<C2STaskCheckPacket> {

        @Override
        public void encode(C2STaskCheckPacket msg, PacketBuffer buffer) {
            buffer.writeResourceLocation(msg.book);
            buffer.writeResourceLocation(msg.entry);
            buffer.writeBoolean(msg.consume);
            buffer.writeInt(msg.page);
        }

        @Override
        public C2STaskCheckPacket decode(PacketBuffer buffer) {
            ResourceLocation book = buffer.readResourceLocation();
            ResourceLocation entry = buffer.readResourceLocation();
            boolean consume = buffer.readBoolean();
            int page = buffer.readInt();
            return new C2STaskCheckPacket(book, entry, consume, page);
        }

        @Override
        public void handle(C2STaskCheckPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Book book = ItemModBook.getBook(ItemModBook.forBook(message.book));
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null) return;
                BookPage i = book.contents.entries.get(message.entry).getPages().get(message.page);
                if (i instanceof CollectTaskPage) {
                    boolean t = CheckUtil.checkTask(((CollectTaskPage) i).items,player.container.getInventory(),message.consume);
                    CompoundNBT n = player.getPersistentData();
                        NBTHelper nbt = NBTHelper.of(n);
                        nbt.setBoolean(String.format("patchouliquests.%s.%s.%d",message.book.toString(),message.entry.toString(),message.page),t);
                        new S2CTaskCheckPacket(message.book,message.entry,t,message.page).send(player);
                }
            });
            ctx.get().setPacketHandled(true);
        }

    }

}
