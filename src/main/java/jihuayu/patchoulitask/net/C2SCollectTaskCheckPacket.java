package jihuayu.patchoulitask.net;


import com.google.common.collect.Lists;
import jihuayu.patchoulitask.net.kiwi.ClientPacket;
import jihuayu.patchoulitask.task.CollectTaskPage;
import jihuayu.patchoulitask.util.CheckUtil;
import jihuayu.patchoulitask.util.NBTHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
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

public class C2SCollectTaskCheckPacket extends ClientPacket {
    ResourceLocation book;
    ResourceLocation entry;
    boolean consume;
    int page;

    public C2SCollectTaskCheckPacket(ResourceLocation book, ResourceLocation entry, boolean consume, int page) {
        this.book = book;
        this.entry = entry;
        this.consume = consume;
        this.page = page;
    }

    public static class Handler extends PacketHandler<C2SCollectTaskCheckPacket> {

        @Override
        public void encode(C2SCollectTaskCheckPacket msg, PacketBuffer buffer) {
            buffer.writeResourceLocation(msg.book);
            buffer.writeResourceLocation(msg.entry);
            buffer.writeBoolean(msg.consume);
            buffer.writeInt(msg.page);
        }

        @Override
        public C2SCollectTaskCheckPacket decode(PacketBuffer buffer) {
            ResourceLocation book = buffer.readResourceLocation();
            ResourceLocation entry = buffer.readResourceLocation();
            boolean consume = buffer.readBoolean();
            int page = buffer.readInt();
            return new C2SCollectTaskCheckPacket(book, entry, consume, page);
        }

        @Override
        public void handle(C2SCollectTaskCheckPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Book book = ItemModBook.getBook(ItemModBook.forBook(message.book));
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null) return;
                BookPage i = book.contents.entries.get(message.entry).getPages().get(message.page);
                if (i instanceof CollectTaskPage) {

                    CompoundNBT n = player.getPersistentData();
                    NBTHelper nbt = NBTHelper.of(n);
                    boolean over = nbt.getBoolean(String.format("patchouliquests.%s.%s.%d.over", message.book.toString(), message.entry.toString(), message.page));
                    if (over){
                        new S2CTaskCheckPacket(message.book, message.entry, over, message.page).send(player);
                        return;
                    }
                    ListNBT l = nbt.getTagList(String.format("patchouliquests.%s.%s.%d.num", message.book.toString(), message.entry.toString(), message.page), NBTHelper.NBT.INT);
                    List<Integer> list = new ArrayList<>();
                    if (l == null) {
                        l = new ListNBT();
                        for (int num = 0;num < ((CollectTaskPage) i).items.size();num++) {
                            l.add(num,IntNBT.valueOf(0));
                        }
                    }
                    for (INBT k : l) {
                        if (k instanceof IntNBT) {
                            list.add(((IntNBT) k).getInt());
                        }
                    }
                    boolean t = CheckUtil.checkTask(((CollectTaskPage) i).items, player.container.getInventory(), message.consume, list);
                    nbt.setBoolean(String.format("patchouliquests.%s.%s.%d.over", message.book.toString(), message.entry.toString(), message.page), t);
                    for (ItemStack out : ((CollectTaskPage) i).reward){
                        player.addItemStackToInventory(out.copy());
                    }
                    if (message.consume) {
                        for (int num = 0;num < list.size();num++) {
                            l.set(num,IntNBT.valueOf(list.get(num)));
                        }
                        nbt.setTag(String.format("patchouliquests.%s.%s.%d.num", message.book.toString(), message.entry.toString(), message.page),l);
                        new S2CCollectTaskCheckPacket(message.book, message.entry, t, message.page,list).send(player);
                    }
                    else {
                        new S2CTaskCheckPacket(message.book, message.entry, t, message.page).send(player);
                    }

                }
            });
            ctx.get().setPacketHandled(true);
        }

    }

}
