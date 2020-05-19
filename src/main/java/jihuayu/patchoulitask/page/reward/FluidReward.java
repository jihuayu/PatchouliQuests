package jihuayu.patchoulitask.page.reward;

import com.google.gson.*;

import java.lang.reflect.Type;

public class FluidReward extends BaseReward {

    public static CommandReward deserialize(JsonElement json, Type type1, JsonDeserializationContext ctx) throws JsonParseException {
        CommandReward reward = new CommandReward();
        if (json instanceof JsonObject) {
            JsonElement item = ((JsonObject) json).get("command");
            if (item != null) {
                if (item instanceof JsonArray) {
                    for (JsonElement i : item.getAsJsonArray()) {
                        String itemSTack = i.getAsString();
                        reward.commands.add(itemSTack);
                    }
                }
                else {
                    String itemStack = item.getAsString();
                    reward.commands.add((itemStack));
                }
            }
            JsonElement group = ((JsonObject) json).get("group");
            if (group!=null){
                reward.group = group.getAsInt();
            }
        }
        return reward;
    }
}
