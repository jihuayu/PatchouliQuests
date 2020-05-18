package jihuayu.patchoulitask.net.itemreward;

import jihuayu.patchoulitask.net.kiwi.Packet;
import jihuayu.patchoulitask.page.PageBaseQuest;
import jihuayu.patchoulitask.page.reward.BaseReward;
import jihuayu.patchoulitask.page.reward.ItemReward;
import jihuayu.patchoulitask.util.BookNBTHelper;
import jihuayu.patchoulitask.util.BufferHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import java.util.function.Supplier;

public class S2CItemRewardPacket extends Packet {
    ResourceLocation book;
    ResourceLocation entry;
    int id;
    int index;
    int ok;

    public S2CItemRewardPacket(ResourceLocation book, ResourceLocation entry, int id, int index, int ok) {
        this.book = book;
        this.entry = entry;
        this.id = id;
        this.index = index;
        this.ok = ok;
    }

    public static class Handler extends PacketHandler<S2CItemRewardPacket> {

        @Override
        public void encode(S2CItemRewardPacket msg, PacketBuffer buffer) {
            BufferHelper.writeTaskId(buffer, msg.book, msg.entry, msg.id);
            buffer.writeVarInt(msg.index);
            buffer.writeVarInt(msg.ok);
        }

        @Override
        public S2CItemRewardPacket decode(PacketBuffer buffer) {
            BufferHelper.TaskRead i = BufferHelper.readTaskId(buffer);
            ResourceLocation book = i.book;
            ResourceLocation entry = i.entry;
            int page = i.id;
            int index = buffer.readVarInt();
            int ok = buffer.readVarInt();
            return new S2CItemRewardPacket(book, entry, page, index, ok);
        }

        @Override
        public void handle(S2CItemRewardPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Book book = ItemModBook.getBook(ItemModBook.forBook(message.book));
                BookPage page = BookNBTHelper.getPage(book.contents.entries.get(message.entry).getPages(), message.id);
                if (page instanceof PageBaseQuest) {
                    BaseReward reward = ((PageBaseQuest) page).rewards.get(message.index);
                    if (reward instanceof ItemReward) {
                        ((ItemReward) reward).receive = message.ok;
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }

    }

}
