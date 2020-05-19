//from kiwi
package jihuayu.patchoulitask.net.kiwi;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.PacketDistributor.PacketTarget;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public abstract class Packet {
    public Packet() {
    }

    public void send(PacketTarget target) {
        NetworkChannel.send(target, this);
    }

    /**
     * @since 2.7.0
     */
    public void send(ServerPlayerEntity player) {
        send(PacketDistributor.PLAYER.with(() -> player));
    }
    public void send(List<ServerPlayerEntity> players) {
        for (ServerPlayerEntity i : players){
            send(i);
        }
    }
    public void send(ServerPlayerEntity player,ListNBT players) {
        for (INBT i : players){
            if (i instanceof StringNBT){
                ServerPlayerEntity p = player.server.getPlayerList().getPlayerByUUID(UUID.fromString(i.getString()));

                if (p!=null){
                    send(p);
                }
            }

        }
    }
    public void send() {
    }

    public static abstract class PacketHandler<T extends Packet> {
        public abstract void encode(T msg, PacketBuffer buffer);

        public abstract T decode(PacketBuffer buffer);

        public abstract void handle(T msg, Supplier<NetworkEvent.Context> ctx);
    }
}
