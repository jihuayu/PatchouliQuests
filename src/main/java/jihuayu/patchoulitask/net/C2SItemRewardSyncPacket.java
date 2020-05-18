package jihuayu.patchoulitask.net;

import jihuayu.patchoulitask.old.net.kiwi.ClientPacket;
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

import java.util.function.Supplier;

public class C2SItemRewardSyncPacket extends ClientPacket {
    ResourceLocation book;
    ResourceLocation entry;
    int id;
    int index;

    public C2SItemRewardSyncPacket(ResourceLocation book, ResourceLocation entry, int id, int index) {
        this.book = book;
        this.entry = entry;
        this.id = id;
        this.index = index;
    }

    public static class Handler extends PacketHandler<C2SItemRewardSyncPacket> {

        @Override
        public void encode(C2SItemRewardSyncPacket msg, PacketBuffer buffer) {
            BufferHelper.writeTaskId(buffer, msg.book, msg.entry, msg.id);
            buffer.writeVarInt(msg.index);
        }

        @Override
        public C2SItemRewardSyncPacket decode(PacketBuffer buffer) {
            BufferHelper.TaskRead i = BufferHelper.readTaskId(buffer);
            ResourceLocation book = i.book;
            ResourceLocation entry = i.entry;
            int page = i.id;
            int index = buffer.readVarInt();
            return new C2SItemRewardSyncPacket(book, entry, page, index);
        }

        @Override
        public void handle(C2SItemRewardSyncPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Book book = ItemModBook.getBook(ItemModBook.forBook(message.book));
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null) return;
                BookPage page = BookNBTHelper.getPage(book.contents.entries.get(message.entry).getPages(), message.id);
                if (page instanceof PageBaseQuest) {
                    BaseReward reward = ((PageBaseQuest) page).rewards.get(message.index);
                    if (reward instanceof ItemReward) {
                        int ok = ((ItemReward) reward).isReceive(player);
                        new S2CItemRewardPacket(message.book, message.entry, message.id, message.index, ok);
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }

    }

}
