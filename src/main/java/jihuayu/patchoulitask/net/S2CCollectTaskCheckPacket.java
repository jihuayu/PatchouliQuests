package jihuayu.patchoulitask.net;

import jihuayu.patchoulitask.net.S2CTaskCheckPacket;
import jihuayu.patchoulitask.task.BaseTaskPage;
import jihuayu.patchoulitask.task.CollectTaskPage;
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

    public S2CCollectTaskCheckPacket(ResourceLocation book, ResourceLocation entry, boolean ok, int page,List<Integer> task_num ) {
        super(book, entry, ok, page);
        this.task_num = task_num;
    }

    public static class Handler extends PacketHandler<S2CCollectTaskCheckPacket> {

        @Override
        public void encode(S2CCollectTaskCheckPacket msg, PacketBuffer buffer) {
            buffer.writeResourceLocation(msg.book);
            buffer.writeResourceLocation(msg.entry);
            buffer.writeBoolean(msg.ok);
            buffer.writeInt(msg.page);
            buffer.writeInt(msg.task_num.size());
            for (int i : msg.task_num){
                buffer.writeInt(i);
            }
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

            return new S2CCollectTaskCheckPacket(book, entry, ok, page,list);
        }

        @Override
        public void handle(S2CCollectTaskCheckPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Book book = ItemModBook.getBook(ItemModBook.forBook(message.book));
                BookPage i = book.contents.entries.get(message.entry).getPages().get(message.page);
                if (i instanceof CollectTaskPage) {
                    ((CollectTaskPage) i).stats = message.ok ? 1 : -1;
                    for (int j =0;j<message.task_num.size();j++){
                        ((CollectTaskPage) i).items_num.add(j);
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }

    }
}
