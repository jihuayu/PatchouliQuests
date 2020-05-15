package jihuayu.patchoulitask.net.collect;

import jihuayu.patchoulitask.net.kiwi.ClientPacket;
import jihuayu.patchoulitask.task.BaseTaskPage;
import jihuayu.patchoulitask.task.CollectTaskPage;
import jihuayu.patchoulitask.util.BookNBTHelper;
import jihuayu.patchoulitask.util.NBTHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class C2SCollectTaskSyncPacket extends ClientPacket {
    ResourceLocation book;
    ResourceLocation entry;
    int page;

    public C2SCollectTaskSyncPacket(ResourceLocation book, ResourceLocation entry, int page) {
        this.book = book;
        this.entry = entry;
        this.page = page;
    }

    public static class Handler extends PacketHandler<C2SCollectTaskSyncPacket> {

        @Override
        public void encode(C2SCollectTaskSyncPacket msg, PacketBuffer buffer) {
            buffer.writeResourceLocation(msg.book);
            buffer.writeResourceLocation(msg.entry);
            buffer.writeInt(msg.page);
        }

        @Override
        public C2SCollectTaskSyncPacket decode(PacketBuffer buffer) {
            ResourceLocation book = buffer.readResourceLocation();
            ResourceLocation entry = buffer.readResourceLocation();
            int page = buffer.readInt();
            return new C2SCollectTaskSyncPacket(book, entry, page);
        }

        @Override
        public void handle(C2SCollectTaskSyncPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Book book = ItemModBook.getBook(ItemModBook.forBook(message.book));
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null) return;
                BookPage i = book.contents.entries.get(message.entry).getPages().get(message.page);
                if (i instanceof BaseTaskPage) {
                    if (!BookNBTHelper.hasHide(player,message.book.toString(),message.entry.toString(),message.page)){
                        BookNBTHelper.setHide(player,message.book.toString(),message.entry.toString(),message.page,((CollectTaskPage) i).hide);
                    }
                    if (!BookNBTHelper.hasLock(player,message.book.toString(),message.entry.toString(),message.page)){
                        BookNBTHelper.setLock(player,message.book.toString(),message.entry.toString(),message.page,((CollectTaskPage) i).lock);
                    }
                    CompoundNBT n = player.getPersistentData();
                    NBTHelper nbt = NBTHelper.of(n);
                    boolean over = nbt.getBoolean(String.format("patchouliquests.%s.%s.%d.over",message.book.toString(),message.entry.toString(),message.page));
                    ListNBT l = nbt.getTagList(String.format("patchouliquests.%s.%s.%d.num",message.book.toString(),message.entry.toString(),message.page), NBTHelper.NBT.INT);
                    List<Integer> list = new ArrayList<>();
                    if (l!=null)
                    for (INBT k : l){
                        if (k instanceof IntNBT){
                            list.add(((IntNBT) k).getInt());
                        }
                    }
                    boolean hide = BookNBTHelper.isHide(player,message.book.toString(),message.entry.toString(),message.page);
                    boolean lock = BookNBTHelper.isLock(player,message.book.toString(),message.entry.toString(),message.page);
                    new S2CCollectTaskCheckPacket(message.book,message.entry,over,message.page,list,hide,lock).send(player);
                }
            });
            ctx.get().setPacketHandled(true);
        }

    }

}
