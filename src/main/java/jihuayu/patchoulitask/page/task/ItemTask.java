package jihuayu.patchoulitask.page.task;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.systems.RenderSystem;
import jihuayu.patchoulitask.net.task.item.C2SItemTaskCheckPacket;
import jihuayu.patchoulitask.util.BufferHelper;
import jihuayu.patchoulitask.util.JEIUtil;
import jihuayu.patchoulitask.util.NBTHelper;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.*;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemTask extends BaseTask {
    public transient List<Ingredient> items = new ArrayList<>();
    public List<Integer> itemsNum = new ArrayList<>();
    public transient boolean consume = false;

    @Override
    public String getDesc() {
        return (consume?"patchouliquests.item_task.no_consume":"patchouliquests.item_task.consume");
    }

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
                    consume ? itemsNum.get(i) : -1);
        }
        return true;
    }
    public int mouseClicked1(double mouseX, double mouseY, int mouseButton) {
        int wrap = GuiBook.PAGE_WIDTH / 24;
        int recipeX = GuiBook.PAGE_WIDTH / 2 - 49;
        int recipeY = 25;
        for (int i = 0; i < items.size(); i++) {
            if (page.parent.isMouseInRelativeRange(mouseX, mouseY, recipeX + (i % wrap) * 24, recipeY + (i / wrap) * 24 + 4, 24, 24)) {
                ItemStack[] stacks = items.get(i).getMatchingStacks();
                JEIUtil.showRecipes(stacks[(page.parent.ticksInBook / 20) % stacks.length]);
                return 1;
            }
        }
        return 0;
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
            if (stats > 0 && list.size() > 0) {
                list.get(0).setStyle(new Style().setColor(TextFormatting.GREEN));
                list.add(new TranslationTextComponent("patchouliquests.task.item.over").setStyle(new Style().setColor(TextFormatting.GREEN)));
            }
            else {
                list.add(new TranslationTextComponent(getDesc(),(over >= 0 ? (over) + "/" : "") + (stack.getCount()),stack.getDisplayName())
                        .setStyle(new Style().setColor(TextFormatting.YELLOW)));
                if (over >= 0)
                    list.add(new TranslationTextComponent("patchouliquests.tooltip.item.consume").setStyle(new Style().setColor(TextFormatting.RED)));
            }

            page.parent.setTooltip(list);
        }
        RenderHelper.disableStandardItemLighting();
    }

    public static ItemTask deserialize(JsonElement json, Type type1, JsonDeserializationContext ctx) throws JsonParseException {
        ItemTask reward = new ItemTask();
        if (json instanceof JsonObject) {
            JsonElement item = ((JsonObject) json).get("item");
            if (item != null) {
                if (item instanceof JsonObject) {
                    Map<String, Double> map = ctx.deserialize(item, HashMap.class);
                    for (String i : map.keySet()) {
                        Ingredient is = ItemStackUtil.loadIngredientFromString(i);
                        for (ItemStack j : is.getMatchingStacks()) {
                            j.setCount((int) (Math.round(map.get(i))));
                        }
                        reward.items.add(is);
                        reward.itemsNum.add(0);
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

    @Override
    public void readBuffer(PacketBuffer buffer) {
        this.itemsNum = BufferHelper.readList(buffer, (i, j) -> j.add(i.readVarInt()), 0);
        this.stats = buffer.readBoolean() ? 1 : -1;
        System.out.println(stats);
    }

    @Override
    public void writeBuffer(PacketBuffer buffer, ServerPlayerEntity entity) {
        BufferHelper.writeList(buffer, (i, j) -> i.writeVarInt((int) j), getItemsNum(entity));
        buffer.writeBoolean(getStats(entity) > 0);
    }

    public List<Integer> getItemsNum(ServerPlayerEntity player) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            int num1 = NBTHelper.of(player.getPersistentData()).getInt(String.format("patchouliquests.%s.%s.%d.%d.num.%d", page.book.id, page.getEntry().getId(), page.id, num, i), 0);
            list.add(num1);
        }
        return list;
    }

    public void setItemsNum(ServerPlayerEntity player, List<Integer> list) {
        for (int i = 0; i < list.size(); i++) {
            NBTHelper.of(player.getPersistentData()).setInt(String.format("patchouliquests.%s.%s.%d.%d.num.%d", page.book.id, page.getEntry().getId(), page.id, num, i), list.get(i));
        }
    }

    @Override
    public void tryComplete() {
        new C2SItemTaskCheckPacket(page.book.id, page.getEntry().getId(), page.id, num).send();
    }
}
