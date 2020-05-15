package jihuayu.patchoulitask.net.nearpostition;


import com.mojang.brigadier.exceptions.CommandSyntaxException;
import jihuayu.patchoulitask.ModMain;
import jihuayu.patchoulitask.net.S2CTaskCheckPacket;
import jihuayu.patchoulitask.net.collect.S2CCollectTaskCheckPacket;
import jihuayu.patchoulitask.net.kiwi.ClientPacket;
import jihuayu.patchoulitask.task.BaseTaskPage;
import jihuayu.patchoulitask.task.CollectTaskPage;
import jihuayu.patchoulitask.task.NearPositionTaskPage;
import jihuayu.patchoulitask.util.BookHelper;
import jihuayu.patchoulitask.util.BookNBTHelper;
import jihuayu.patchoulitask.util.CheckUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class C2SNearPositionTaskCheckPacket extends ClientPacket {
    ResourceLocation book;
    ResourceLocation entry;
    int id;

    public C2SNearPositionTaskCheckPacket(ResourceLocation book, ResourceLocation entry, int id) {
        this.book = book;
        this.entry = entry;
        this.id = id;
    }

    public static class Handler extends PacketHandler<C2SNearPositionTaskCheckPacket> {

        @Override
        public void encode(C2SNearPositionTaskCheckPacket msg, PacketBuffer buffer) {
            buffer.writeResourceLocation(msg.book);
            buffer.writeResourceLocation(msg.entry);
            buffer.writeInt(msg.id);
        }

        @Override
        public C2SNearPositionTaskCheckPacket decode(PacketBuffer buffer) {
            ResourceLocation book = buffer.readResourceLocation();
            ResourceLocation entry = buffer.readResourceLocation();
            int page = buffer.readInt();
            return new C2SNearPositionTaskCheckPacket(book, entry, page);
        }

        @Override
        public void handle(C2SNearPositionTaskCheckPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Book book = ItemModBook.getBook(ItemModBook.forBook(message.book));
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null) return;
                BookPage i = BookNBTHelper.getPage(book.contents.entries.get(message.entry).getPages(),message.id);
                if (i instanceof NearPositionTaskPage) {
                    if (!BookNBTHelper.hasHide(player, message.book.toString(), message.entry.toString(), message.id)) {
                        BookNBTHelper.setHide(player, message.book.toString(), message.entry.toString(), message.id, ((NearPositionTaskPage) i).hide);
                    }
                    if (!BookNBTHelper.hasLock(player, message.book.toString(), message.entry.toString(), message.id)) {
                        BookNBTHelper.setLock(player, message.book.toString(), message.entry.toString(), message.id, ((NearPositionTaskPage) i).lock);
                    }
                    boolean over = BookNBTHelper.isOver(player, message.book.toString(), message.entry.toString(), message.id);
                    if (over) {
                        boolean hide = BookNBTHelper.isHide(player, message.book.toString(), message.entry.toString(), message.id);
                        boolean lock = BookNBTHelper.isLock(player, message.book.toString(), message.entry.toString(), message.id);
                        new S2CTaskCheckPacket(message.book, message.entry, over, message.id, hide, lock).send(player);
                        return;
                    }
                    if(player.getPosition().distanceSq(((NearPositionTaskPage) i).x, ((NearPositionTaskPage) i).y, ((NearPositionTaskPage) i).z,true)<=
                            ((NearPositionTaskPage) i).range){
                        BookHelper.complete(player, (BaseTaskPage) i);
                        BookNBTHelper.setOver(player, message.book.toString(), message.entry.toString(), message.id,true);
                        BookNBTHelper.setOver(player,message.book.toString(),message.entry.toString(),message.id,true);
                        return;
                    }
                    BookNBTHelper.setOver(player, message.book.toString(), message.entry.toString(), message.id,false);
                    BookNBTHelper.setOver(player,message.book.toString(),message.entry.toString(),message.id,false);
                }

            });
            ctx.get().setPacketHandled(true);
        }

    }

}
