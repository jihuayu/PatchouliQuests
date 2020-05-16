package jihuayu.patchoulitask.net;

import jihuayu.patchoulitask.net.kiwi.Packet;
import jihuayu.patchoulitask.task.BaseTaskPage;
import jihuayu.patchoulitask.util.BookNBTHelper;
import jihuayu.patchoulitask.util.BufferHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import java.util.List;
import java.util.function.Supplier;

public class S2CTaskCheckPacket extends Packet {
    public boolean ok;
    public boolean hide;
    public boolean lock;
    public ResourceLocation book;
    public ResourceLocation entry;
    public int id;
    public List<Boolean> reward;

    public S2CTaskCheckPacket(ResourceLocation book, ResourceLocation entry, boolean ok, int id, boolean hide, boolean lock, List<Boolean> reward) {
        this.book = book;
        this.entry = entry;
        this.ok = ok;
        this.id = id;
        this.hide = hide;
        this.lock = lock;
        this.reward = reward;
    }

    public static class Handler extends PacketHandler<S2CTaskCheckPacket> {

        @Override
        public void encode(S2CTaskCheckPacket msg, PacketBuffer buffer) {
            BufferHelper.writeTaskId(buffer, msg.book, msg.entry, msg.id);
            buffer.writeBoolean(msg.ok);
            buffer.writeBoolean(msg.hide);
            buffer.writeBoolean(msg.lock);
            BufferHelper.writeList(buffer, (i, j) -> i.writeBoolean((boolean) j), msg.reward);
        }

        @Override
        public S2CTaskCheckPacket decode(PacketBuffer buffer) {
            BufferHelper.TaskRead tr = BufferHelper.readTaskId(buffer);
            ResourceLocation book = tr.book;
            ResourceLocation entry = tr.entry;
            int id = tr.id;
            boolean ok = buffer.readBoolean();
            boolean hide = buffer.readBoolean();
            boolean lock = buffer.readBoolean();
            List<Boolean> list = BufferHelper.readList(buffer, (i, j) -> j.add(i.readBoolean()), false);
            return new S2CTaskCheckPacket(book, entry, ok, id, hide, lock, list);

        }

        @Override
        public void handle(S2CTaskCheckPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Book book = ItemModBook.getBook(ItemModBook.forBook(message.book));
                BookPage i = BookNBTHelper.getPage(book.contents.entries.get(message.entry).getPages(), message.id);
                if (i instanceof BaseTaskPage) {
                    ((BaseTaskPage) i).stats = message.ok ? 1 : -1;
                    ((BaseTaskPage) i).lock = message.lock;
                    ((BaseTaskPage) i).hide = message.hide;
                    ((BaseTaskPage) i).reward_stats = message.reward;
                }
            });
            ctx.get().setPacketHandled(true);
        }

    }
}
