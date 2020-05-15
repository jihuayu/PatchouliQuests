package jihuayu.patchoulitask.net.collect;

import jihuayu.patchoulitask.net.S2CTaskCheckPacket;
import jihuayu.patchoulitask.task.CollectTaskPage;
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

public class S2CCollectTaskCheckPacket extends S2CTaskCheckPacket {

    public List<Integer> task_num;

    public S2CCollectTaskCheckPacket(ResourceLocation book, ResourceLocation entry, boolean ok, int page,List<Integer> task_num,boolean hide,boolean lock ) {
        super(book, entry, ok, page,hide,lock);
        this.task_num = task_num;
    }

    public static class Handler extends PacketHandler<S2CCollectTaskCheckPacket> {

        @Override
        public void encode(S2CCollectTaskCheckPacket msg, PacketBuffer buffer) {
            buffer.writeResourceLocation(msg.book);
            buffer.writeResourceLocation(msg.entry);
            buffer.writeBoolean(msg.ok);
            buffer.writeInt(msg.id);
            buffer.writeInt(msg.task_num.size());
            for (int i : msg.task_num){
                buffer.writeInt(i);
            }
            buffer.writeBoolean(msg.hide);
            buffer.writeBoolean(msg.lock);
        }

        @Override
        public S2CCollectTaskCheckPacket decode(PacketBuffer buffer) {
            ResourceLocation book = buffer.readResourceLocation();
            ResourceLocation entry = buffer.readResourceLocation();
            boolean ok = buffer.readBoolean();
            int page = buffer.readInt();
            int size = buffer.readInt();
            List<Integer> list = new ArrayList<>();
            for (int i = 0 ;i< size;i++){
                list.add(buffer.readInt());
            }
            boolean hide = buffer.readBoolean();
            boolean lock = buffer.readBoolean();
            return new S2CCollectTaskCheckPacket(book, entry, ok, page,list,hide,lock);
        }

        @Override
        public void handle(S2CCollectTaskCheckPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Book book = ItemModBook.getBook(ItemModBook.forBook(message.book));
                BookPage i = BookNBTHelper.getPage(book.contents.entries.get(message.entry).getPages(),message.id);
                if (i instanceof CollectTaskPage) {
                    ((CollectTaskPage) i).stats = message.ok ? 1 : -1;
                    for (int j =0;j<message.task_num.size();j++){
                        ((CollectTaskPage) i).items_num.clear();
                        ((CollectTaskPage) i).items_num.add(message.task_num.get(j));
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }

    }
}
