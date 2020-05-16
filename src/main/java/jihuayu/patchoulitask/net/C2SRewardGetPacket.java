package jihuayu.patchoulitask.net;

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

public class C2SRewardGetPacket extends ClientPacket {
    ResourceLocation book;
    ResourceLocation entry;
    int id;
    int index;

    public C2SRewardGetPacket(ResourceLocation book, ResourceLocation entry, int id, int index) {
        this.book = book;
        this.entry = entry;
        this.id = id;
        this.index = index;
    }

    public static class Handler extends PacketHandler<C2SRewardGetPacket> {

        @Override
        public void encode(C2SRewardGetPacket msg, PacketBuffer buffer) {
            BufferHelper.writeTaskId(buffer, msg.book, msg.entry, msg.id);
            buffer.writeInt(msg.index);
        }

        @Override
        public C2SRewardGetPacket decode(PacketBuffer buffer) {
            BufferHelper.TaskRead i = BufferHelper.readTaskId(buffer);
            ResourceLocation book = i.book;
            ResourceLocation entry = i.entry;
            int page = i.id;
            int index = buffer.readInt();
            return new C2SRewardGetPacket(book, entry, page, index);
        }

        @Override
        public void handle(C2SRewardGetPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Book book = ItemModBook.getBook(ItemModBook.forBook(message.book));
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null) return;
                BookPage page = BookNBTHelper.getPage(book.contents.entries.get(message.entry).getPages(), message.id);
                if (page instanceof BaseTaskPage) {
                    if (!BookNBTHelper.hasHide(player, message.book.toString(), message.entry.toString(), message.id)) {
                        BookNBTHelper.setHide(player, message.book.toString(), message.entry.toString(), message.id, ((BaseTaskPage) page).hide);
                    }
                    if (!BookNBTHelper.hasLock(player, message.book.toString(), message.entry.toString(), message.id)) {
                        BookNBTHelper.setLock(player, message.book.toString(), message.entry.toString(), message.id, ((BaseTaskPage) page).lock);
                    }
                    if (!BookNBTHelper.getRewardStats(player, message.book.toString(), message.entry.toString(), message.id, message.index)) {
                        BookNBTHelper.setRewardStats(player, message.book.toString(), message.entry.toString(), message.id, message.index, true);
                        player.addItemStackToInventory(((BaseTaskPage) page).reward.get(message.index).copy());
                    }
                    boolean over = BookNBTHelper.isOver(player, message.book.toString(), message.entry.toString(), message.id);
                    boolean hide = BookNBTHelper.isHide(player, message.book.toString(), message.entry.toString(), message.id);
                    boolean lock = BookNBTHelper.isLock(player, message.book.toString(), message.entry.toString(), message.id);
                    ArrayList<Boolean> list = new ArrayList<>();
                    for (int j = 0; j < ((BaseTaskPage) page).reward.size(); j++) {
                        list.add(BookNBTHelper.getRewardStats(player, message.book.toString(), message.entry.toString(), message.id, j));
                    }
                    new S2CTaskCheckPacket(message.book, message.entry, over, message.id, hide, lock, list).send(player);
                }
            });
            ctx.get().setPacketHandled(true);
        }

    }

}
