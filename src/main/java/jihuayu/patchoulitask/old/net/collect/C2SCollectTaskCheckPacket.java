package jihuayu.patchoulitask.old.net.collect;


import jihuayu.patchoulitask.old.net.S2CTaskCheckPacket;
import jihuayu.patchoulitask.old.net.kiwi.ClientPacket;
import jihuayu.patchoulitask.old.task.BaseTaskPage;
import jihuayu.patchoulitask.old.task.CollectTaskPage;
import jihuayu.patchoulitask.util.BookHelper;
import jihuayu.patchoulitask.util.BookNBTHelper;
import jihuayu.patchoulitask.util.BufferHelper;
import jihuayu.patchoulitask.util.CheckUtil;
import net.minecraft.entity.player.ServerPlayerEntity;
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
    int id;

    public C2SCollectTaskCheckPacket(ResourceLocation book, ResourceLocation entry, int id) {
        this.book = book;
        this.entry = entry;
        this.id = id;
    }

    public static class Handler extends PacketHandler<C2SCollectTaskCheckPacket> {

        @Override
        public void encode(C2SCollectTaskCheckPacket msg, PacketBuffer buffer) {
            BufferHelper.writeTaskId(buffer, msg.book, msg.entry, msg.id);
        }

        @Override
        public C2SCollectTaskCheckPacket decode(PacketBuffer buffer) {
            BufferHelper.TaskRead p = BufferHelper.readTaskId(buffer);
            ResourceLocation book = p.book;
            ResourceLocation entry = p.entry;
            int page = p.id;
            return new C2SCollectTaskCheckPacket(book, entry, page);
        }

        @Override
        public void handle(C2SCollectTaskCheckPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Book book = ItemModBook.getBook(ItemModBook.forBook(message.book));
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null) return;
                BookPage i = BookNBTHelper.getPage(book.contents.entries.get(message.entry).getPages(), message.id);
                if (i instanceof CollectTaskPage) {
                    if (!BookNBTHelper.hasHide(player, message.book.toString(), message.entry.toString(), message.id)) {
                        BookNBTHelper.setHide(player, message.book.toString(), message.entry.toString(), message.id, ((CollectTaskPage) i).hide);
                    }
                    if (!BookNBTHelper.hasLock(player, message.book.toString(), message.entry.toString(), message.id)) {
                        BookNBTHelper.setLock(player, message.book.toString(), message.entry.toString(), message.id, ((CollectTaskPage) i).lock);
                    }
                    boolean over = BookNBTHelper.isOver(player, message.book.toString(), message.entry.toString(), message.id);
                    ArrayList<Boolean> list1 = new ArrayList<>();
                    for (int j = 0; j < ((BaseTaskPage) i).reward.size(); j++) {
                        list1.add(BookNBTHelper.getRewardStats(player, message.book.toString(), message.entry.toString(), message.id, j));
                    }
                    if (over) {
                        boolean hide = BookNBTHelper.isHide(player, message.book.toString(), message.entry.toString(), message.id);
                        boolean lock = BookNBTHelper.isLock(player, message.book.toString(), message.entry.toString(), message.id);
                        new S2CTaskCheckPacket(message.book, message.entry, over, message.id, hide, lock, list1).send(player);
                        return;
                    }
                    List<Integer> list = new ArrayList<>();
                    for (int k = 0; k < ((CollectTaskPage) i).items.size(); k++) {
                        list.add(BookNBTHelper.getTaskNum(player, message.book.toString(), message.entry.toString(), message.id, k));
                    }
                    boolean t = CheckUtil.checkTask(((CollectTaskPage) i).items, player.container.getInventory(), ((CollectTaskPage) i).consume, list);
                    BookNBTHelper.setOver(player, message.book.toString(), message.entry.toString(), message.id, t);
                    if (t) {
                        BookHelper.complete(player, (BaseTaskPage) i);
                    }
                    boolean hide = BookNBTHelper.isHide(player, message.book.toString(), message.entry.toString(), message.id);
                    boolean lock = BookNBTHelper.isLock(player, message.book.toString(), message.entry.toString(), message.id);
                    if (((CollectTaskPage) i).consume) {
                        for (int num = 0; num < list.size(); num++) {
                            BookNBTHelper.setTaskNum(player, message.book.toString(), message.entry.toString(), message.id, num, list.get(num));
                        }

                        new S2CCollectTaskCheckPacket(message.book, message.entry, t, message.id, list, hide, lock, list1).send(player);
                    } else {

                        new S2CTaskCheckPacket(message.book, message.entry, over, message.id, hide, lock, list1).send(player);
                    }

                }
            });
            ctx.get().setPacketHandled(true);
        }

    }

}
