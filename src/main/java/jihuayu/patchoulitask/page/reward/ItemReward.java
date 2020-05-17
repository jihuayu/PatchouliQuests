package jihuayu.patchoulitask.page.reward;

import com.google.gson.*;
import com.mojang.blaze3d.systems.RenderSystem;
import jihuayu.patchoulitask.page.PageBaseQuest;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ItemReward extends BaseReward {

    public transient List<ItemStack> item = new ArrayList<>();
    public int receive = 0;

    @Override
    public int mouseClicked1(double mouseX, double mouseY, int mouseButton) {
        return 0;
    }

    @Override
    public boolean questButtonClicked1(Button button) {
        return false;
    }

    @Override
    public boolean render1(int mouseX, int mouseY, float pticks) {
        int index = num % PageBaseQuest.rewardPrePage;
        int X = index * 24 + GuiBook.PAGE_WIDTH / 2 - 49;
        int Y = GuiBook.PAGE_HEIGHT - 12 - 25 - 24;
        RenderSystem.enableBlend();
        RenderSystem.color4f(1F, 1F, 1F, 1F);
        page.mc.textureManager.bindTexture(page.book.craftingTexture);
        AbstractGui.blit(X, Y + 4, 83, 71, 24, 24, 128, 128);
        page.renderItemStackAndNumAndGet(X + 4, Y + 8, mouseX, mouseY, item.get((page.getParent().ticksInBook / 20) % item.size()), receive);
        return true;
    }

    public static ItemReward deserialize(JsonElement json, Type type1, JsonDeserializationContext ctx) throws JsonParseException {
        ItemReward reward = new ItemReward();
        if (json instanceof JsonObject) {
            JsonElement item = ((JsonObject) json).get("item");
            if (item!=null){
                if (item instanceof JsonArray){
                    for (JsonElement i : item.getAsJsonArray()){
                        String itemSTack = i.getAsString();
                        reward.item.add(ItemStackUtil.loadStackFromString(itemSTack));
                    }
                }
                else {
                    String itemStack = item.getAsString();
                    reward.item.add(ItemStackUtil.loadStackFromString(itemStack));
                }
            }
        }
        return reward;
    }
}
