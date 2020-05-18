package jihuayu.patchoulitask.net.task.item;

import jihuayu.patchoulitask.net.kiwi.Packet;
import jihuayu.patchoulitask.page.PageBaseQuest;
import jihuayu.patchoulitask.page.task.BaseTask;
import jihuayu.patchoulitask.page.task.ItemTask;
import jihuayu.patchoulitask.util.BookNBTHelper;
import jihuayu.patchoulitask.util.BufferHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import java.util.List;
import java.util.function.Supplier;

public class S2CItemTaskPacket extends Packet {
    ResourceLocation book;
    ResourceLocation entry;
    int id;
    int index;
    List<Integer> items_num;
    boolean ok;

    public S2CItemTaskPacket(ResourceLocation book, ResourceLocation entry, int id, int index, List<Integer> items_num, boolean ok) {
        this.book = book;
        this.entry = entry;
        this.id = id;
        this.index = index;
        this.items_num = items_num;
        this.ok = ok;
    }

    public static class Handler extends PacketHandler<S2CItemTaskPacket> {

        @Override
        public void encode(S2CItemTaskPacket msg, PacketBuffer buffer) {
            BufferHelper.writeTaskId(buffer, msg.book, msg.entry, msg.id);
            buffer.writeVarInt(msg.index);
            BufferHelper.writeList(buffer, (i, j) -> i.writeVarInt((int) j), msg.items_num);
            buffer.writeBoolean(msg.ok);
        }

        @Override
        public S2CItemTaskPacket decode(PacketBuffer buffer) {
            BufferHelper.TaskRead i = BufferHelper.readTaskId(buffer);
            ResourceLocation book = i.book;
            ResourceLocation entry = i.entry;
            int page = i.id;
            int index = buffer.readVarInt();
            List<Integer> items_num = BufferHelper.readList(buffer, (p, j) -> j.add(p.readVarInt()), 0);
            boolean ok = buffer.readBoolean();
            return new S2CItemTaskPacket(book, entry, page, index, items_num, ok);
        }

        @Override
        public void handle(S2CItemTaskPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Book book = ItemModBook.getBook(ItemModBook.forBook(message.book));
                PageBaseQuest page = BookNBTHelper.getPage(book.contents.entries.get(message.entry).getPages(), message.id);
                if (page != null) {
                    BaseTask task = page.tasks.get(message.index);
                    if (task instanceof ItemTask) {
                        ((ItemTask) task).itemsNum = message.items_num;
                        task.stats = message.ok ? 1 : -1;
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }

    }

}
