package jihuayu.patchoulitask.page.reward;

import com.google.gson.*;
import jihuayu.patchoulitask.api.RenderAble;
import jihuayu.patchoulitask.page.BaseComp;
import jihuayu.patchoulitask.page.PageBaseQuest;
import net.minecraft.client.gui.widget.button.Button;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.lang.reflect.Type;

public class BaseReward extends BaseComp {
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
}
