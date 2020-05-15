package jihuayu.patchoulitask.net.collect;


import com.mojang.brigadier.exceptions.CommandSyntaxException;
import jihuayu.patchoulitask.ModMain;
import jihuayu.patchoulitask.net.S2CTaskCheckPacket;
import jihuayu.patchoulitask.net.kiwi.ClientPacket;
import jihuayu.patchoulitask.task.BaseTaskPage;
import jihuayu.patchoulitask.task.CollectTaskPage;
import jihuayu.patchoulitask.util.BookHelper;
import jihuayu.patchoulitask.util.BookNBTHelper;
import jihuayu.patchoulitask.util.CheckUtil;
import jihuayu.patchoulitask.util.NBTHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
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
    int id;

    public C2SCollectTaskCheckPacket(ResourceLocation book, ResourceLocation entry, int id) {
        this.book = book;
        this.entry = entry;
        this.id = id;
    }

    public static class Handler extends PacketHandler<C2SCollectTaskCheckPacket> {

        @Override
        public void encode(C2SCollectTaskCheckPacket msg, PacketBuffer buffer) {
            buffer.writeResourceLocation(msg.book);
            buffer.writeResourceLocation(msg.entry);
            buffer.writeInt(msg.id);
        }

        @Override
        public C2SCollectTaskCheckPacket decode(PacketBuffer buffer) {
            ResourceLocation book = buffer.readResourceLocation();
            ResourceLocation entry = buffer.readResourceLocation();
            int page = buffer.readInt();
            return new C2SCollectTaskCheckPacket(book, entry, page);
        }

        @Override
        public void handle(C2SCollectTaskCheckPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Book book = ItemModBook.getBook(ItemModBook.forBook(message.book));
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null) return;
                BookPage i = BookNBTHelper.getPage(book.contents.entries.get(message.entry).getPages(),message.id);
                if (i instanceof CollectTaskPage) {
                    if (!BookNBTHelper.hasHide(player,message.book.toString(),message.entry.toString(),message.id)){
                        BookNBTHelper.setHide(player,message.book.toString(),message.entry.toString(),message.id,((CollectTaskPage) i).hide);
                    }
                    if (!BookNBTHelper.hasLock(player,message.book.toString(),message.entry.toString(),message.id)){
                        BookNBTHelper.setLock(player,message.book.toString(),message.entry.toString(),message.id,((CollectTaskPage) i).lock);
                    }
                    boolean over = BookNBTHelper.isOver(player,message.book.toString(),message.entry.toString(),message.id);
                    if (over) {
                        boolean hide = BookNBTHelper.isHide(player,message.book.toString(),message.entry.toString(),message.id);
                        boolean lock = BookNBTHelper.isLock(player,message.book.toString(),message.entry.toString(),message.id);
                        new S2CTaskCheckPacket(message.book,message.entry,over,message.id,hide,lock).send(player);
                        return;
                    }
                    ListNBT l = BookNBTHelper.getTaskNum(player,message.book.toString(),message.entry.toString(),message.id);
                    List<Integer> list = new ArrayList<>();
                    if (l == null) {
                        l = new ListNBT();
                        for (int num = 0; num < ((CollectTaskPage) i).items.size(); num++) {
                            l.add(num, IntNBT.valueOf(0));
                        }
                    }
                    for (INBT k : l) {
                        if (k instanceof IntNBT) {
                            list.add(((IntNBT) k).getInt());
                        }
                    }
                    boolean t = CheckUtil.checkTask(((CollectTaskPage) i).items, player.container.getInventory(), ((CollectTaskPage) i).consume, list);
                    BookNBTHelper.setOver(player,message.book.toString(),message.entry.toString(),message.id,t);
                    if (t) {
                        BookHelper.complete(player, (BaseTaskPage) i);
                    }
                    boolean hide = BookNBTHelper.isHide(player,message.book.toString(),message.entry.toString(),message.id);
                    boolean lock = BookNBTHelper.isLock(player,message.book.toString(),message.entry.toString(),message.id);
                    if (((CollectTaskPage) i).consume) {
                        for (int num = 0; num < list.size(); num++) {
                            l.set(num, IntNBT.valueOf(list.get(num)));
                        }
                        BookNBTHelper.setTaskNum(player,message.book.toString(),message.entry.toString(),message.id,l);
                        new S2CCollectTaskCheckPacket(message.book, message.entry, t, message.id, list,hide,lock).send(player);
                    } else {

                        new S2CTaskCheckPacket(message.book,message.entry,over,message.id,hide,lock).send(player);
                    }

                }
            });
            ctx.get().setPacketHandled(true);
        }

    }

}
