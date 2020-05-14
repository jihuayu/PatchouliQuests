package jihuayu.patchoulitask.net;

import jihuayu.patchoulitask.net.kiwi.ClientPacket;
import jihuayu.patchoulitask.task.BaseTaskPage;
import jihuayu.patchoulitask.util.NBTHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Deprecated
public class C2SCollectTaskAddItemPacket extends ClientPacket {
    ResourceLocation book;
    ResourceLocation entry;
    int page;
    ResourceLocation item;
    int num;
    public C2SCollectTaskAddItemPacket(ResourceLocation book, ResourceLocation entry, int page,ResourceLocation item,int num) {
        this.book = book;
        this.entry = entry;
        this.page = page;
        this.item = item;
        this.num = num;
    }

    public static class Handler extends PacketHandler<C2SCollectTaskAddItemPacket> {

        @Override
        public void encode(C2SCollectTaskAddItemPacket msg, PacketBuffer buffer) {
            buffer.writeResourceLocation(msg.book);
            buffer.writeResourceLocation(msg.entry);
            buffer.writeInt(msg.page);
            buffer.writeResourceLocation(msg.item);
            buffer.writeInt(msg.num);
        }

        @Override
        public C2SCollectTaskAddItemPacket decode(PacketBuffer buffer) {
            ResourceLocation book = buffer.readResourceLocation();
            ResourceLocation entry = buffer.readResourceLocation();
            int page = buffer.readInt();
            ResourceLocation item = buffer.readResourceLocation();
            int num = buffer.readInt();
            return new C2SCollectTaskAddItemPacket(book, entry, page,item,num);
        }

        @Override
        public void handle(C2SCollectTaskAddItemPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Book book = ItemModBook.getBook(ItemModBook.forBook(message.book));
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null) return;
                BookPage i = book.contents.entries.get(message.entry).getPages().get(message.page);
                if (i instanceof BaseTaskPage) {


                }
            });
            ctx.get().setPacketHandled(true);
        }

    }

}
