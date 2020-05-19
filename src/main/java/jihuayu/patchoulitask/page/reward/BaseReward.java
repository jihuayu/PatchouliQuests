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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.lang.reflect.Type;
import java.util.List;

public abstract class BaseReward extends BaseComp {
    public int receive = 0;
    public transient int group = 0;
    public transient boolean teamOne;

    public static class Deserializer implements JsonDeserializer<BaseReward> {
        public BaseReward deserialize(JsonElement json, Type type1, JsonDeserializationContext ctx) throws JsonParseException {
            if (json instanceof JsonObject) {
                JsonElement t = ((JsonObject) json).get("type");
                if (t!=null){
                    String type = t.getAsString();
                    if (type.equals("item")){
                        return ItemReward.deserialize(json,type1,ctx);
                    }
                    if (type.equals("command")){
                        return CommandReward.deserialize(json,type1,ctx);
                    }
                    if (type.equals("fluid")){
                        return FluidReward.deserialize(json,type1,ctx);
                    }
                    if (type.equals("forge_energy")){
                        return FEReward.deserialize(json,type1,ctx);
                    }
                }

            }
            return null;
        }
    }
    public int getReceive(PlayerEntity playerEntity) {
        return NBTHelper.of(playerEntity.getPersistentData()).getInt(String.format(teamOne?"patchouliquests.%s.%s.%d.%d.receive":"patchouliquests_person.%s.%s.%d.%d.receive",
                page.book.id.toString(), page.getEntry().getId().toString(), page.id, num), 0);
    }

    public void setReceive(PlayerEntity playerEntity, int receive) {
        NBTHelper.of(playerEntity.getPersistentData()).setInt(String.format(teamOne?"patchouliquests.%s.%s.%d.%d.receive":"patchouliquests_person.%s.%s.%d.%d.receive",
                page.book.id.toString(), page.getEntry().getId().toString(), page.id, num), receive);
    }

    public void rewardToolTip(List<ITextComponent> list){
        if (group!=0){
            list.add(new TranslationTextComponent("patchouliquests.tooltip.reward.group")
                    .setStyle(new Style().setColor(TextFormatting.YELLOW)));
            list.add(new TranslationTextComponent("patchouliquests.tooltip.reward.group.text")
                    .setStyle(new Style().setColor(TextFormatting.YELLOW)));
        }
        if (teamOne){
            list.add(new TranslationTextComponent("patchouliquests.tooltip.reward.team_one")
                    .setStyle(new Style().setColor(TextFormatting.RED)));
        }
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
