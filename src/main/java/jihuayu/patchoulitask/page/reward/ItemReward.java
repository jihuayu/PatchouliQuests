package jihuayu.patchoulitask.page.reward;

import com.google.gson.*;
import com.mojang.blaze3d.systems.RenderSystem;
import jihuayu.patchoulitask.page.PageBaseQuest;
import jihuayu.patchoulitask.util.NBTHelper;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ItemReward extends BaseReward {

    public transient List<ItemStack> item = new ArrayList<>();
    public transient int receive = 0;
    public boolean random = false;
    public transient int show_tick_index;
    public transient int past_tick;
    transient int index = num % PageBaseQuest.rewardPrePage;
    transient int X = index * 24 + GuiBook.PAGE_WIDTH / 2 - 49;
    transient int Y = GuiBook.PAGE_HEIGHT - 12 - 25 - 24;

    public int isReceive(PlayerEntity playerEntity) {
        return NBTHelper.of(playerEntity.getPersistentData()).getInt(String.format("patchouliquests.%s.%s.%d.%d.receive",
                page.book.id.toString(), page.getEntry().getId().toString(), page.id, num), 0);
    }

    public void setReceive(PlayerEntity playerEntity, int receive) {
        NBTHelper.of(playerEntity.getPersistentData()).setInt(String.format("patchouliquests.%s.%s.%d.%d.receive",
                page.book.id.toString(), page.getEntry().getId().toString(), page.id, num), receive);
    }

    @Override
    public int mouseClicked1(double mouseX, double mouseY, int mouseButton) {
        if (page.getParent().isMouseInRelativeRange(mouseX, mouseY, GuiBook.PAGE_WIDTH / 2 - 49, GuiBook.PAGE_HEIGHT - 12 - 25 - 24, 24 * 4, 24)) {
            if (mouseButton < GLFW.GLFW_KEY_0 && page.stats > 0) {
                show_tick_index -= 20;
                //send packet
                return 1;
            }
        }
        return 0;
    }

    @Override
    public boolean questButtonClicked1(Button button) {
        return false;
    }

    @Override
    public boolean onMouse(double mouseX, double mouseY, double scroll) {
        if (page.getParent().isMouseInRelativeRange(mouseX, mouseY, GuiBook.PAGE_WIDTH / 2 - 49, GuiBook.PAGE_HEIGHT - 12 - 25 - 24, 24 * 4, 24)) {
            if (scroll < 0) {
                show_tick_index -= 20;
                return true;
            }
            if (scroll > 0) {
                show_tick_index += 20;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean render1(int mouseX, int mouseY, float pticks) {
        RenderSystem.enableBlend();
        RenderSystem.color4f(1F, 1F, 1F, 1F);
        page.mc.textureManager.bindTexture(page.book.craftingTexture);
        AbstractGui.blit(X, Y + 4, 83, 71, 24, 24, 128, 128);
        renderItemStackAndNumAndGet(X + 4, Y + 8, mouseX, mouseY);
        return true;
    }

    public void renderItemStackAndNumAndGet(int x, int y, int mouseX, int mouseY) {
        if (InputMappings.isKeyDown(page.mc.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
            show_tick_index += (page.getParent().ticksInBook - past_tick);
        }
        page.getParent().ticksInBook = past_tick;
        ItemStack stack = item.get((show_tick_index / 20) % item.size());
        if (stack == null || stack.isEmpty()) {
            return;
        }

        page.mc.getItemRenderer().renderItemAndEffectIntoGUI(item.get((page.getParent().ticksInBook / 20) % item.size()), x, y);
        page.mc.getItemRenderer().renderItemOverlays(page.fontRenderer, stack, x, y);
        if (page.getParent().isMouseInRelativeRange(mouseX, mouseY, x, y, 16, 16)) {
            List<ITextComponent> list = new ArrayList<>(stack.getTooltip(page.mc.player,
                    page.mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL));
            list.add(new TranslationTextComponent("patchouliquests.tooltip.item.num").setStyle(new Style().setColor(TextFormatting.YELLOW))
                    .appendText(String.valueOf(stack.getCount())));
            if (page.stats > 0) {
                if (receive == 0) {
                    if (item.size() > 1) {
                        if (random) {
                            list.add(new TranslationTextComponent("patchouliquests.tooltip.item.get.random")
                                    .setStyle(new Style().setColor(TextFormatting.GREEN)));
                        } else {
                            list.add(new TranslationTextComponent("patchouliquests.tooltip.item.get.more")
                                    .setStyle(new Style().setColor(TextFormatting.GREEN)));
                            list.add(new TranslationTextComponent("patchouliquests.tooltip.item.change.more")
                                    .setStyle(new Style().setColor(TextFormatting.GREEN)));
                        }
                    } else {
                        list.add(new TranslationTextComponent("patchouliquests.tooltip.item.get")
                                .setStyle(new Style().setColor(TextFormatting.GREEN)));
                    }
                } else {
                    list.add(new TranslationTextComponent("patchouliquests.tooltip.item.alread_get"));
                }
            }
            page.getParent().setTooltip(list);
        }
        RenderHelper.disableStandardItemLighting();
    }

    public static ItemReward deserialize(JsonElement json, Type type1, JsonDeserializationContext ctx) throws JsonParseException {
        ItemReward reward = new ItemReward();
        if (json instanceof JsonObject) {
            JsonElement item = ((JsonObject) json).get("item");
            if (item != null) {
                if (item instanceof JsonArray) {
                    for (JsonElement i : item.getAsJsonArray()) {
                        String itemSTack = i.getAsString();
                        reward.item.add(ItemStackUtil.loadStackFromString(itemSTack));
                    }
                } else {
                    String itemStack = item.getAsString();
                    reward.item.add(ItemStackUtil.loadStackFromString(itemStack));
                }
            }
            JsonElement random = ((JsonObject) json).get("random");
            if (random != null) {
                reward.random = random.getAsBoolean();
            }
        }
        return reward;
    }
}