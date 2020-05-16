package jihuayu.patchoulitask.net.kill;

import jihuayu.patchoulitask.net.kiwi.Packet;
import jihuayu.patchoulitask.task.BaseTaskPage;
import jihuayu.patchoulitask.task.kill.KillTypeTask;
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

public class S2CKillTypeTaskCheckPacket extends Packet {
    public boolean ok;
    public boolean hide;
    public boolean lock;
    public ResourceLocation book;
    public ResourceLocation entry;
    public int id;
    public List<Boolean> reward;
    public int num;

    public S2CKillTypeTaskCheckPacket(ResourceLocation book, ResourceLocation entry, int id, boolean ok, boolean hide, boolean lock, List<Boolean> reward, int num) {
        this.book = book;
        this.entry = entry;
        this.ok = ok;
        this.id = id;
        this.hide = hide;
        this.lock = lock;
        this.reward = reward;
        this.num = num;
    }

    public static class Handler extends PacketHandler<S2CKillTypeTaskCheckPacket> {

        @Override
        public void encode(S2CKillTypeTaskCheckPacket msg, PacketBuffer buffer) {
            BufferHelper.writeTaskId(buffer, msg.book, msg.entry, msg.id);
            buffer.writeBoolean(msg.ok);
            buffer.writeBoolean(msg.hide);
            buffer.writeBoolean(msg.lock);
            BufferHelper.writeList(buffer, (i, j) -> i.writeBoolean((boolean) j), msg.reward);
            buffer.writeVarInt(msg.num);
        }

        @Override
        public S2CKillTypeTaskCheckPacket decode(PacketBuffer buffer) {
            BufferHelper.TaskRead tr = BufferHelper.readTaskId(buffer);
            ResourceLocation book = tr.book;
            ResourceLocation entry = tr.entry;
            int id = tr.id;
            boolean ok = buffer.readBoolean();
            boolean hide = buffer.readBoolean();
            boolean lock = buffer.readBoolean();
            List<Boolean> list = BufferHelper.readList(buffer, (i, j) -> j.add(i.readBoolean()), false);
            int num = buffer.readVarInt();
            return new S2CKillTypeTaskCheckPacket(book, entry, id, ok, hide, lock, list, num);

        }

        @Override
        public void handle(S2CKillTypeTaskCheckPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Book book = ItemModBook.getBook(ItemModBook.forBook(message.book));
                BookPage i = BookNBTHelper.getPage(book.contents.entries.get(message.entry).getPages(), message.id);
                if (i instanceof KillTypeTask) {
                    ((BaseTaskPage) i).stats = message.ok ? 1 : -1;
                    ((BaseTaskPage) i).lock = message.lock;
                    ((BaseTaskPage) i).hide = message.hide;
                    ((BaseTaskPage) i).reward_stats = message.reward;
                    ((KillTypeTask) i).now_num = message.num;
                }
            });
            ctx.get().setPacketHandled(true);
        }

    }
}
