package jihuayu.patchoulitask.task;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.systems.RenderSystem;
import jihuayu.patchoulitask.ModMain;
import jihuayu.patchoulitask.net.C2STaskCheckPacket;
import jihuayu.patchoulitask.net.C2STaskSyncPacket;
import jihuayu.patchoulitask.util.CheckUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.BookTextRenderer;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.page.PageQuest;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.ArrayList;
import java.util.List;

import static vazkii.patchouli.common.item.ItemModBook.TAG_BOOK;

public class BaseTaskPage extends PageQuest {
    public transient List<Ingredient> items = new ArrayList<>();
    public transient List<ItemStack> reward = new ArrayList<>();
    public transient int stats = 0;

    @Override
    public void build(BookEntry entry, int pageNum) {
        super.build(entry, pageNum);
        if(stats==0)
            new C2STaskSyncPacket(new ResourceLocation(book.getBookItem().getTag().getString(TAG_BOOK)), this.entry.getId(), this.pageNum).send();
    }

    @Override
    public void onDisplayed(GuiBookEntry parent, int left, int top) {
        super.onDisplayed(parent, left, top);
        onHidden(parent);
        Button button = new Button(GuiBook.PAGE_WIDTH / 2 - 50, GuiBook.PAGE_HEIGHT - 25, 100, 20, "", this::questButtonClicked);
        addButton(button);
        updateButtonText(button);
    }

    @Override
    public void render(int mouseX, int mouseY, float pticks) {

    }

    protected void questButtonClicked(Button button) {
        String res = entry.getId().toString();
        boolean task = true;
        for (BookPage i : entry.getPages()){
            if (i instanceof BaseTaskPage){
                if (((BaseTaskPage) i).stats <= 0){
                    task = false;
                }
            }
        }
        PersistentData.DataHolder.BookData data = PersistentData.data.getBookData(parent.book);
        if (task){
            if (!data.completedManualQuests.contains(res)) {
                data.completedManualQuests.add(res);
            }
        }
        else {
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
        return PersistentData.data.getBookData(book).completedManualQuests.contains(entry.getId().toString());
    }

    public void renderItemStackWithAddonToolTips(int x, int y, int mouseX, int mouseY, ItemStack stack, List<ITextComponent> addon) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, x, y);
        mc.getItemRenderer().renderItemOverlays(fontRenderer, stack, x, y);
        if (parent.isMouseInRelativeRange(mouseX, mouseY, x, y, 16, 16)) {
            List<ITextComponent> list = new ArrayList<>(stack.getTooltip(mc.player,
                    mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL));
            if (addon != null)
                list.addAll(addon);
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
            list.add(new TranslationTextComponent("patchouliquests.tooltip.num").setStyle(new Style().setColor(TextFormatting.YELLOW))
                    .appendText(String.valueOf(stack.getCount())));
            if (mc.gameSettings.advancedItemTooltips)
                list.add(new TranslationTextComponent("patchouliquests.tooltip.open_jei"));
            parent.setTooltip(list);
        }
        RenderHelper.disableStandardItemLighting();
    }

    public void renderIngredientAndNumAndJEI(int x, int y, int mouseX, int mouseY, Ingredient ingr) {
        ItemStack[] stacks = ingr.getMatchingStacks();
        if (stacks.length > 0) {
            renderItemStackAndNumAndJEI(x, y, mouseX, mouseY, stacks[(parent.ticksInBook / 20) % stacks.length]);
        }
    }
}
