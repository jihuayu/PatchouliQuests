package jihuayu.patchoulitask.net;

import jihuayu.patchoulitask.net.kiwi.Packet;
import jihuayu.patchoulitask.task.BaseTaskPage;
import jihuayu.patchoulitask.util.BookNBTHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import java.util.ArrayList;
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
            buffer.writeResourceLocation(msg.book);
            buffer.writeResourceLocation(msg.entry);
            buffer.writeBoolean(msg.ok);
            buffer.writeInt(msg.id);
            buffer.writeBoolean(msg.hide);
            buffer.writeBoolean(msg.lock);
            buffer.writeInt(msg.reward.size());
            for (boolean i : msg.reward){
                buffer.writeBoolean(i);
            }
        }

        @Override
        public S2CTaskCheckPacket decode(PacketBuffer buffer) {
            ResourceLocation book = buffer.readResourceLocation();
            ResourceLocation entry = buffer.readResourceLocation();
            boolean ok = buffer.readBoolean();
            int page = buffer.readInt();
            boolean hide = buffer.readBoolean();
            boolean lock = buffer.readBoolean();
            int num = buffer.readInt();
            List<Boolean> list = new ArrayList<>();
            for (int i = 0;i<num;i++){
                list.add(buffer.readBoolean());
            }
            return new S2CTaskCheckPacket(book, entry, ok, page,hide,lock,list);
        }

        @Override
        public void handle(S2CTaskCheckPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Book book = ItemModBook.getBook(ItemModBook.forBook(message.book));
                BookPage i = BookNBTHelper.getPage(book.contents.entries.get(message.entry).getPages(),message.id);
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
