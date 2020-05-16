package jihuayu.patchoulitask.net.kill;

import jihuayu.patchoulitask.net.kiwi.ClientPacket;
import jihuayu.patchoulitask.task.BaseTaskPage;
import jihuayu.patchoulitask.util.BookNBTHelper;
import jihuayu.patchoulitask.util.BufferHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import java.util.ArrayList;
import java.util.function.Supplier;

public class C2SKillTypeTaskSyncPacket extends ClientPacket {
    ResourceLocation book;
    ResourceLocation entry;
    int id;

    public C2SKillTypeTaskSyncPacket(ResourceLocation book, ResourceLocation entry, int id) {
        this.book = book;
        this.entry = entry;
        this.id = id;
    }

    public static class Handler extends PacketHandler<C2SKillTypeTaskSyncPacket> {

        @Override
        public void encode(C2SKillTypeTaskSyncPacket msg, PacketBuffer buffer) {
            BufferHelper.writeTaskId(buffer, msg.book, msg.entry, msg.id);
        }

        @Override
        public C2SKillTypeTaskSyncPacket decode(PacketBuffer buffer) {
            BufferHelper.TaskRead i = BufferHelper.readTaskId(buffer);
            ResourceLocation book = i.book;
            ResourceLocation entry = i.entry;
            int page = i.id;
            return new C2SKillTypeTaskSyncPacket(book, entry, page);
        }

        @Override
        public void handle(C2SKillTypeTaskSyncPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Book book = ItemModBook.getBook(ItemModBook.forBook(message.book));
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null) return;
                BookPage i = BookNBTHelper.getPage(book.contents.entries.get(message.entry).getPages(), message.id);
                if (i instanceof BaseTaskPage) {
                    if (!BookNBTHelper.hasHide(player, message.book.toString(), message.entry.toString(), message.id)) {
                        BookNBTHelper.setHide(player, message.book.toString(), message.entry.toString(), message.id, ((BaseTaskPage) i).hide);
                    }
                    if (!BookNBTHelper.hasLock(player, message.book.toString(), message.entry.toString(), message.id)) {
                        BookNBTHelper.setLock(player, message.book.toString(), message.entry.toString(), message.id, ((BaseTaskPage) i).lock);
                    }
                    boolean over = BookNBTHelper.isOver(player, message.book.toString(), message.entry.toString(), message.id);
                    boolean hide = BookNBTHelper.isHide(player, message.book.toString(), message.entry.toString(), message.id);
                    boolean lock = BookNBTHelper.isLock(player, message.book.toString(), message.entry.toString(), message.id);
                    ArrayList<Boolean> list = new ArrayList<>();
                    for (int j = 0; j < ((BaseTaskPage) i).reward.size(); j++) {
                        list.add(BookNBTHelper.getRewardStats(player, message.book.toString(), message.entry.toString(), message.id, j));
                    }
                    int num = BookNBTHelper.getKillNum(player, message.book.toString(), message.entry.toString(), message.id);
                    new S2CKillTypeTaskCheckPacket(message.book, message.entry, message.id, over, hide, lock, list, num).send(player);
                }
            });
            ctx.get().setPacketHandled(true);
        }

    }

}
