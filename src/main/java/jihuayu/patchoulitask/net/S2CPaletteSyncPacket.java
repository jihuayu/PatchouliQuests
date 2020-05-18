package jihuayu.patchoulitask.net;

//import jihuayu.patchoulitask.handler.PaletteUpdateHandler;

import jihuayu.patchoulitask.net.kiwi.Packet;
import jihuayu.patchoulitask.util.PaletteHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class S2CPaletteSyncPacket extends Packet {
    public ResourceLocation book;
    public List<ResourceLocation> list;

    public S2CPaletteSyncPacket(ResourceLocation book, List<ResourceLocation> list) {
        this.book = book;
        this.list = list;
    }

    public static class Handler extends PacketHandler<S2CPaletteSyncPacket> {

        @Override
        public void encode(S2CPaletteSyncPacket msg, PacketBuffer buffer) {
            buffer.writeInt(msg.list.size());
            for (ResourceLocation i : msg.list) {
                buffer.writeResourceLocation(i);
            }
            buffer.writeResourceLocation(msg.book);
        }

        @Override
        public S2CPaletteSyncPacket decode(PacketBuffer buffer) {
            List<ResourceLocation> list = new ArrayList<>();
            int num = buffer.readInt();
            for (int i = 0; i < num; i++) {
                list.add(buffer.readResourceLocation());
            }
            ResourceLocation book = buffer.readResourceLocation();
            return new S2CPaletteSyncPacket(book, list);
        }

        @Override
        public void handle(S2CPaletteSyncPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                PaletteHelper.PALETTE_LIST.put(message.book, message.list);
            });
            ctx.get().setPacketHandled(true);
        }

    }
}
