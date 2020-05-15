package jihuayu.patchoulitask.task;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.text.*;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.page.PageQuest;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.ArrayList;
import java.util.List;

public class BaseTaskPage extends PageQuest {
    public transient int stats = 0;
    public transient List<ItemStack> reward = new ArrayList<>();
    Button button;
    @SerializedName("finish_cmd")
    public List<String> finishCmd;
    @SerializedName("reward")
    List<String> rewardStr;

    @Override
    public void build(BookEntry entry, int pageNum) {
        super.build(entry, pageNum);
        if (rewardStr != null){
            for (String i : rewardStr) {
                reward.add(ItemStackUtil.loadStackFromString(i));
            }
        }
        if (finishCmd == null)
            finishCmd = new ArrayList<>();
    }

    @Override
    public void onDisplayed(GuiBookEntry parent, int left, int top) {
        super.onDisplayed(parent, left, top);
        onHidden(parent);
        button = new Button(GuiBook.PAGE_WIDTH / 2 - 50, GuiBook.PAGE_HEIGHT - 25, 100, 20, "", this::questButtonClicked);
        addButton(button);
        updateButtonText(button);
    }

    @Override
    public void render(int mouseX, int mouseY, float pticks) {
        updateButtonText(button);
        int recipeX = GuiBook.PAGE_WIDTH / 2 - 49;
        int wrap = GuiBook.PAGE_WIDTH / 24;
        int recipeY = GuiBook.PAGE_HEIGHT - (((int) Math.ceil(reward.size() * 1.0 / wrap))+(finishCmd.isEmpty()?0:1)) * 24 - 12 - 25;
        if (reward.size()>0){
            parent.drawCenteredStringNoShadow(I18n.format("patchouliquests.task.reward"),
                    fontRenderer.getStringWidth(I18n.format("patchouliquests.task.reward"))/2+4, recipeY - 6, book.textColor);
            for (int i = 0; i < reward.size(); i++) {
                RenderSystem.enableBlend();
                RenderSystem.color4f(1F, 1F, 1F, 1F);
                mc.textureManager.bindTexture(book.craftingTexture);
                AbstractGui.blit(recipeX + (i % wrap) * 24, recipeY + (i / wrap) * 24 + 4, 83, 71, 24, 24, 128, 128);
                renderItemStackAndNumAndJEI(recipeX + (i % wrap) * 24 + 4, recipeY + (i / wrap) * 24 + 8, mouseX, mouseY, reward.get(i));
            }
            if (!finishCmd.isEmpty()){
                RenderSystem.enableBlend();
                RenderSystem.color4f(1F, 1F, 1F, 1F);
                mc.textureManager.bindTexture(book.craftingTexture);
                AbstractGui.blit(recipeX + (reward.size() % wrap) * 24, recipeY + (reward.size() / wrap) * 24 + 4, 83, 71, 24, 24, 128, 128);
                renderCommand(recipeX + (reward.size() % wrap) * 24 + 4, recipeY + (reward.size() / wrap) * 24 + 8, mouseX, mouseY,finishCmd);
            }
        }

    }

    protected void questButtonClicked(Button button) {
        String res = entry.getId().toString();
        boolean task = true;
        for (BookPage i : entry.getPages()) {
            if (i instanceof BaseTaskPage) {
                if (((BaseTaskPage) i).stats <= 0) {
                    task = false;
                }
            }
        }
        PersistentData.DataHolder.BookData data = PersistentData.data.getBookData(parent.book);
        if (task) {
            if (!data.completedManualQuests.contains(res)) {
                data.completedManualQuests.add(res);
            }
        } else {
            data.completedManualQuests.remove(res);
        }
        PersistentData.save();
        updateButtonText(button);
        entry.markReadStateDirty();
    }

    public void updateButtonText(Button button) {
        String s = I18n.format(stats > 0 ? "patchouliquests.task.over" : "patchouliquests.task.submit");
        button.setMessage(s);
    }

    public boolean isCompleted(Book book) {
        try {
            return PersistentData.data.getBookData(book).completedManualQuests.contains(entry.getId().toString());

        } catch (Exception e) {
            return false;
        }
    }

    public void renderCommand(int x, int y, int mouseX, int mouseY, List<String> commands) {
        if (commands == null || commands.size()<1) {
            return;
        }
        ItemStack stack = new ItemStack(Items.COMMAND_BLOCK,commands.size());
        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, x, y);
        mc.getItemRenderer().renderItemOverlays(fontRenderer, stack, x, y);
        if (parent.isMouseInRelativeRange(mouseX, mouseY, x, y, 16, 16)) {
            List<ITextComponent> list = new ArrayList<>();
            for (String i : commands){
                list.add(new StringTextComponent(i).setStyle(new Style().setColor(TextFormatting.YELLOW)));
            }
            parent.setTooltip(list);
        }
        RenderHelper.disableStandardItemLighting();
    }
    public void renderItemStackAndNumAndJEI(int x, int y, int mouseX, int mouseY, ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, x, y);
        mc.getItemRenderer().renderItemOverlays(fontRenderer, stack, x, y);
        if (parent.isMouseInRelativeRange(mouseX, mouseY, x, y, 16, 16)) {
            List<ITextComponent> list = new ArrayList<>(stack.getTooltip(mc.player,
                    mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL));
            list.add(new TranslationTextComponent("patchouliquests.tooltip.item.num").setStyle(new Style().setColor(TextFormatting.YELLOW))
                    .appendText(String.valueOf(stack.getCount())));
            parent.setTooltip(list);
        }
        RenderHelper.disableStandardItemLighting();
    }

    public void renderItemStackAndNumAndJEIWithOver(int x, int y, int mouseX, int mouseY, ItemStack stack, int over) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, x, y);
        mc.getItemRenderer().renderItemOverlays(fontRenderer, stack, x, y);
        if (parent.isMouseInRelativeRange(mouseX, mouseY, x, y, 16, 16)) {
            List<ITextComponent> list = new ArrayList<>(stack.getTooltip(mc.player,
                    mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL));
            list.add(new TranslationTextComponent("patchouliquests.tooltip.item.num").setStyle(new Style().setColor(TextFormatting.YELLOW))
                    .appendText(over >= 0 ? (over) + "/" : "" + (stack.getCount())));
            if (over >= 0)
                list.add(new TranslationTextComponent("patchouliquests.tooltip.item.consume").setStyle(new Style().setColor(TextFormatting.RED)));
            parent.setTooltip(list);
        }
        RenderHelper.disableStandardItemLighting();
    }

    public void renderIngredientAndNumAndJEIWithOver(int x, int y, int mouseX, int mouseY, Ingredient ingr, int over) {
        ItemStack[] stacks = ingr.getMatchingStacks();
        if (stacks.length > 0) {
            renderItemStackAndNumAndJEIWithOver(x, y, mouseX, mouseY, stacks[(parent.ticksInBook / 20) % stacks.length], over);
        }
    }
}
