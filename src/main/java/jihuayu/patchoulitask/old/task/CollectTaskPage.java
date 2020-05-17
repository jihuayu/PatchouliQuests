package jihuayu.patchoulitask.old.task;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.systems.RenderSystem;
import jihuayu.patchoulitask.old.net.collect.C2SCollectTaskCheckPacket;
import jihuayu.patchoulitask.old.net.collect.C2SCollectTaskSyncPacket;
import jihuayu.patchoulitask.util.JEIUtil;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static vazkii.patchouli.common.item.ItemModBook.TAG_BOOK;

public class CollectTaskPage extends BaseTaskPage {
    public transient List<Ingredient> items = new ArrayList<>();
    public transient List<Integer> items_num = new ArrayList<>();
    @SerializedName("consume")
    public boolean consume;
    @SerializedName("collect")
    Map<String, Integer> itemsStr;

    @Override
    public void build(BookEntry entry, int pageNum) {
        super.build(entry, pageNum);
        for (String i : itemsStr.keySet()) {
            Ingredient ii = ItemStackUtil.loadIngredientFromString(i);

            for (ItemStack t : ii.getMatchingStacks()) {
                t.setCount(itemsStr.get(i));
            }
            items.add(ii);
            items_num.add(0);
        }

    }

    @Override
    public void onDisplayed(GuiBookEntry parent, int left, int top) {
        super.onDisplayed(parent, left, top);
        if (stats == 0) {
            new C2SCollectTaskSyncPacket(book.id, this.entry.getId(), this.id).send();
        }
    }

    @Override
    public int getTextHeight() {
        int wrap = GuiBook.PAGE_WIDTH / 24;
        return 10 + (4 + 8 + 24 * (items.size() / wrap + 1));
    }

    public boolean render1(int mouseX, int mouseY, float pticks) {
        if (!super.render1(mouseX, mouseY, pticks)) return false;
        int wrap = GuiBook.PAGE_WIDTH / 24;
        int recipeX = GuiBook.PAGE_WIDTH / 2 - 49;
        int recipeY = 25;
        parent.drawCenteredStringNoShadow(I18n.format("patchouliquests.task.need"), fontRenderer.getStringWidth(I18n.format("patchouliquests.task.need")) / 2 + 4, recipeY - 6, book.textColor);
        for (int i = 0; i < items.size(); i++) {
            RenderSystem.enableBlend();
            RenderSystem.color4f(1F, 1F, 1F, 1F);
            mc.textureManager.bindTexture(book.craftingTexture);
            AbstractGui.blit(recipeX + (i % wrap) * 24, recipeY + (i / wrap) * 24 + 4, 83, 71, 24, 24, 128, 128);
            renderIngredientAndNumAndJEIWithOver(recipeX + (i % wrap) * 24 + 4, recipeY + (i / wrap) * 24 + 8, mouseX, mouseY, items.get(i),
                    consume ? items_num.get(i) : -1);
        }
        RenderHelper.disableStandardItemLighting();
        return true;
    }

    @Override
    protected boolean questButtonClicked1(Button button) {
        if (!super.questButtonClicked1(button)) return false;
        new C2SCollectTaskCheckPacket(new ResourceLocation(book.getBookItem().getTag().getString(TAG_BOOK)), this.entry.getId(), this.id).send();
        return true;
    }

    public int mouseClicked1(double mouseX, double mouseY, int mouseButton) {
        if (super.mouseClicked1(mouseX, mouseY, mouseButton) < 0) return -1;
        int wrap = GuiBook.PAGE_WIDTH / 24;
        int recipeX = GuiBook.PAGE_WIDTH / 2 - 49;
        int recipeY = 25;
        for (int i = 0; i < items.size(); i++) {
            if (parent.isMouseInRelativeRange(mouseX, mouseY, recipeX + (i % wrap) * 24, recipeY + (i / wrap) * 24 + 4, 24, 24)) {
                ItemStack[] stacks = items.get(i).getMatchingStacks();
                JEIUtil.showRecipes(stacks[(parent.ticksInBook / 20) % stacks.length]);
                return 1;
            }
        }
        return 0;
    }

}