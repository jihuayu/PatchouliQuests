package jihuayu.patchoulitask.page.task;

import com.google.gson.*;
import com.mojang.blaze3d.systems.RenderSystem;
import jihuayu.patchoulitask.page.reward.ItemReward;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemTask extends BaseTask {
    public transient List<Ingredient> items = new ArrayList<>();
    public transient List<Integer> items_num = new ArrayList<>();
    public boolean consume = false;

    @Override
    public boolean render1(int mouseX, int mouseY, float pticks) {
        RenderSystem.enableBlend();
        int wrap = GuiBook.PAGE_WIDTH / 24;
        int recipeX = GuiBook.PAGE_WIDTH / 2 - 49;
        int recipeY = 25;
        for (int i = 0; i < items.size(); i++) {
            RenderSystem.enableBlend();
            RenderSystem.color4f(1F, 1F, 1F, 1F);
            page.mc.textureManager.bindTexture(page.book.craftingTexture);
            AbstractGui.blit(recipeX + (i % wrap) * 24, recipeY + (i / wrap) * 24 + 4, 83, 71, 24, 24, 128, 128);
            renderIngredientAndNumAndJEIWithOver(recipeX + (i % wrap) * 24 + 4, recipeY + (i / wrap) * 24 + 8, mouseX, mouseY, items.get(i),
                    consume ? items_num.get(i) : -1);
        }
        return true;
    }

    public void renderIngredientAndNumAndJEIWithOver(int x, int y, int mouseX, int mouseY, Ingredient ingr, int over) {
        ItemStack[] stacks = ingr.getMatchingStacks();
        if (stacks.length > 0) {
            renderItemStackAndNumAndJEIWithOver(x, y, mouseX, mouseY, stacks[(page.parent.ticksInBook / 20) % stacks.length], over);
        }
    }
    public void renderItemStackAndNumAndJEIWithOver(int x, int y, int mouseX, int mouseY, ItemStack stack, int over) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        page.mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, x, y);
        page.mc.getItemRenderer().renderItemOverlays(page.fontRenderer, stack, x, y);
        if (page.parent.isMouseInRelativeRange(mouseX, mouseY, x, y, 16, 16)) {
            List<ITextComponent> list = new ArrayList<>(stack.getTooltip(page.mc.player,
                    page.mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL));
            list.add(new TranslationTextComponent("patchouliquests.tooltip.item.num").setStyle(new Style().setColor(TextFormatting.YELLOW))
                    .appendText(over >= 0 ? (over) + "/" : "" + (stack.getCount())));
            if (over >= 0)
                list.add(new TranslationTextComponent("patchouliquests.tooltip.item.consume").setStyle(new Style().setColor(TextFormatting.RED)));
            page.parent.setTooltip(list);
        }
        RenderHelper.disableStandardItemLighting();
    }

    public static ItemTask deserialize(JsonElement json, Type type1, JsonDeserializationContext ctx) throws JsonParseException {
        ItemTask reward = new ItemTask();
        if (json instanceof JsonObject) {
            JsonElement item = ((JsonObject) json).get("item");
            if (item != null) {
                if (item instanceof JsonObject){
                    Map<String,Double> map = ctx.deserialize(item, HashMap.class);
                    for (String i : map.keySet()){
                        System.out.println(i);
                        System.out.println(map.get(i));
                        Ingredient is = ItemStackUtil.loadIngredientFromString(i);
                        for (ItemStack j : is.getMatchingStacks()){
                            j.setCount((int)(Math.round(map.get(i))));
                        }
                        reward.items.add(is);
                        reward.items_num.add(0);
                    }
                }
            }
            JsonElement random = ((JsonObject) json).get("consume");
            if (random != null) {
                reward.consume = random.getAsBoolean();
            }
        }
        return reward;
    }
}