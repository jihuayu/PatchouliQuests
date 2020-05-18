package jihuayu.patchoulitask.page.task;

import com.google.gson.*;
import jihuayu.patchoulitask.api.RenderAble;
import jihuayu.patchoulitask.page.BaseComp;
import jihuayu.patchoulitask.page.PageBaseQuest;
import jihuayu.patchoulitask.page.reward.BaseReward;
import jihuayu.patchoulitask.page.reward.ItemReward;
import jihuayu.patchoulitask.util.NBTHelper;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseTask extends BaseComp {
    public int stats = 0;
    public static class Deserializer implements JsonDeserializer<BaseTask> {
        public BaseTask deserialize(JsonElement json, Type type1, JsonDeserializationContext ctx) throws JsonParseException {
            if (json instanceof JsonObject) {
                JsonElement t = ((JsonObject) json).get("type");
                if (t!=null){
                    String type = t.getAsString();
                    if (type.equals("item")){
                        return ItemTask.deserialize(json,type1,ctx);
                    }
                }
            }
            return null;
        }
    }
    public int getStats(ServerPlayerEntity player) {
        return NBTHelper.of(player.getPersistentData()).getInt(String.format("patchouliquests.%s.%s.%d.%d.stats", page.book.id, page.getEntry().getId(), page.id, num), -1);
    }

    public void setStats(ServerPlayerEntity player,int stats) {
        NBTHelper.of(player.getPersistentData()).setInt(String.format("patchouliquests.%s.%s.%d.%d.stats", page.book.id, page.getEntry().getId(), page.id, num), stats);
    }
    public abstract void tryComplete();
}
