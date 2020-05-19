package jihuayu.patchoulitask.page.reward;

import com.google.gson.*;
import jihuayu.patchoulitask.api.RenderAble;
import jihuayu.patchoulitask.page.BaseComp;
import jihuayu.patchoulitask.page.PageBaseQuest;
import jihuayu.patchoulitask.util.NBTHelper;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.lang.reflect.Type;

public abstract class BaseReward extends BaseComp {
    public int receive = 0;
    public static class Deserializer implements JsonDeserializer<BaseReward> {
        public BaseReward deserialize(JsonElement json, Type type1, JsonDeserializationContext ctx) throws JsonParseException {
            if (json instanceof JsonObject) {
                JsonElement t = ((JsonObject) json).get("type");
                if (t!=null){
                    String type = t.getAsString();
                    if (type.equals("item")){
                        return ItemReward.deserialize(json,type1,ctx);
                    }
                }
            }
            return null;
        }
    }
    public int getReceive(PlayerEntity playerEntity) {
        return NBTHelper.of(playerEntity.getPersistentData()).getInt(String.format("patchouliquests.%s.%s.%d.%d.receive",
                page.book.id.toString(), page.getEntry().getId().toString(), page.id, num), 0);
    }

    public void setReceive(PlayerEntity playerEntity, int receive) {
        NBTHelper.of(playerEntity.getPersistentData()).setInt(String.format("patchouliquests.%s.%s.%d.%d.receive",
                page.book.id.toString(), page.getEntry().getId().toString(), page.id, num), receive);
    }
    @Override
    public void readBuffer(PacketBuffer buffer) {
        receive = buffer.readVarInt();
    }

    @Override
    public void writeBuffer(PacketBuffer buffer, ServerPlayerEntity entity) {
        buffer.writeVarInt(getReceive(entity));
    }
}
