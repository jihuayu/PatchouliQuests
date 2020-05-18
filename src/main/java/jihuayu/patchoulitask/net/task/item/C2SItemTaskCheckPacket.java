package jihuayu.patchoulitask.net.task.item;

import jihuayu.patchoulitask.net.kiwi.ClientPacket;
import jihuayu.patchoulitask.page.PageBaseQuest;
import jihuayu.patchoulitask.page.task.BaseTask;
import jihuayu.patchoulitask.page.task.ItemTask;
import jihuayu.patchoulitask.util.BookNBTHelper;
import jihuayu.patchoulitask.util.BufferHelper;
import jihuayu.patchoulitask.util.CheckUtil;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import java.util.function.Supplier;

public class C2SItemTaskCheckPacket extends ClientPacket {
    ResourceLocation book;
    ResourceLocation entry;
    int id;
    int index;

    public C2SItemTaskCheckPacket(ResourceLocation book, ResourceLocation entry, int id, int index) {
        this.book = book;
        this.entry = entry;
        this.id = id;
        this.index = index;
    }

    public static class Handler extends PacketHandler<C2SItemTaskCheckPacket> {

        @Override
        public void encode(C2SItemTaskCheckPacket msg, PacketBuffer buffer) {
            BufferHelper.writeTaskId(buffer, msg.book, msg.entry, msg.id);
            buffer.writeVarInt(msg.index);
        }

        @Override
        public C2SItemTaskCheckPacket decode(PacketBuffer buffer) {
            BufferHelper.TaskRead i = BufferHelper.readTaskId(buffer);
            ResourceLocation book = i.book;
            ResourceLocation entry = i.entry;
            int page = i.id;
            int index = buffer.readVarInt();

            return new C2SItemTaskCheckPacket(book, entry, page, index);
        }

        @Override
        public void handle(C2SItemTaskCheckPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Book book = ItemModBook.getBook(ItemModBook.forBook(message.book));
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null) return;
                PageBaseQuest page = BookNBTHelper.getPage(book.contents.entries.get(message.entry).getPages(), message.id);
                if (page!=null){
                    BaseTask task = page.tasks.get(message.index);
                    if (task instanceof ItemTask){
                        boolean ok = CheckUtil.checkTask(((ItemTask) task).items,player.container.getInventory(),((ItemTask) task).consume,((ItemTask) task).getItemsNum(player));
                        task.setStats(player,ok?1:-1);
                        new S2CItemTaskPacket(message.book,message.entry,message.id,message.index, ((ItemTask) task).getItemsNum(player),ok).send(player);
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }

    }

}
