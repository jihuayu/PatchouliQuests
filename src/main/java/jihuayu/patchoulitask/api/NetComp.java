package jihuayu.patchoulitask.api;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;

public interface NetComp {
    void readBuffer(PacketBuffer buffer);
    void writeBuffer(PacketBuffer buffer, ServerPlayerEntity entity);
}
