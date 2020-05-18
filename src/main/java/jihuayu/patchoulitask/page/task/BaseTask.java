package jihuayu.patchoulitask.page.task;

import com.google.gson.*;
import jihuayu.patchoulitask.api.RenderAble;
import jihuayu.patchoulitask.page.BaseComp;
import jihuayu.patchoulitask.page.PageBaseQuest;
import jihuayu.patchoulitask.page.reward.BaseReward;
import jihuayu.patchoulitask.page.reward.ItemReward;
import net.minecraft.client.gui.widget.button.Button;

import java.lang.reflect.Type;

public abstract class BaseTask extends BaseComp {
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
}
