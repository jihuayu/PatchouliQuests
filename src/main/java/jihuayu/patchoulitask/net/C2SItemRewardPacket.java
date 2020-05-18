package jihuayu.patchoulitask.net;

import jihuayu.patchoulitask.old.net.S2CTaskCheckPacket;
import jihuayu.patchoulitask.old.net.kiwi.ClientPacket;
import jihuayu.patchoulitask.old.task.BaseTaskPage;
import jihuayu.patchoulitask.page.PageBaseQuest;
import jihuayu.patchoulitask.page.reward.BaseReward;
import jihuayu.patchoulitask.page.reward.ItemReward;
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

public class C2SItemRewardPacket extends ClientPacket {
    ResourceLocation book;
    ResourceLocation entry;
    int id;
    int index;
    int index2;
    public C2SItemRewardPacket(ResourceLocation book, ResourceLocation entry, int id, int index,int index2) {
        this.book = book;
        this.entry = entry;
        this.id = id;
        this.index = index;
        this.index2 = index2;
    }

    public static class Handler extends PacketHandler<C2SItemRewardPacket> {

        @Override
        public void encode(C2SItemRewardPacket msg, PacketBuffer buffer) {
            BufferHelper.writeTaskId(buffer, msg.book, msg.entry, msg.id);
            buffer.writeVarInt(msg.index);
            buffer.writeVarInt(msg.index2);
        }

        @Override
        public C2SItemRewardPacket decode(PacketBuffer buffer) {
            BufferHelper.TaskRead i = BufferHelper.readTaskId(buffer);
            ResourceLocation book = i.book;
            ResourceLocation entry = i.entry;
            int page = i.id;
            int index = buffer.readVarInt();
            int index2 = buffer.readVarInt();
            return new C2SItemRewardPacket(book, entry, page, index,index2);
        }

        @Override
        public void handle(C2SItemRewardPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Book book = ItemModBook.getBook(ItemModBook.forBook(message.book));
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null) return;
                BookPage page = BookNBTHelper.getPage(book.contents.entries.get(message.entry).getPages(), message.id);
                if (page instanceof PageBaseQuest) {
                    BaseReward reward = ((PageBaseQuest) page).rewards.get(message.index);
                    if (reward instanceof ItemReward) {
                        int ok = ((ItemReward) reward).isReceive(player);
                        if (ok==0){
                            player.addItemStackToInventory(((ItemReward) reward).item.get(message.index2).copy());
                            ((ItemReward) reward).setReceive(player,1);
                        }
                        new S2CItemRewardPacket(message.book, message.entry, message.id, message.index, 1);
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }

    }

}
