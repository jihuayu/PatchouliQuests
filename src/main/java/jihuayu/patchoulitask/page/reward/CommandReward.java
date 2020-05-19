package jihuayu.patchoulitask.page.reward;

import com.google.gson.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CommandReward extends BaseReward{
    public List<String> commands = new ArrayList<>();

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
        }
        return reward;
    }
}
