package jihuayu.patchoulitask.task;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.systems.RenderSystem;
import jihuayu.patchoulitask.net.C2STaskCheckPacket;
import jihuayu.patchoulitask.net.C2STaskSyncPacket;
import jihuayu.patchoulitask.util.CheckUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
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

public class CollectTaskPage extends PageQuest {
    public transient List<Ingredient> items = new ArrayList<>();
    public transient List<ItemStack> reward = new ArrayList<>();
    public transient int stats = 0;

    @SerializedName("collect")
    List<String> itemsStr;
    @SerializedName("reward")
    List<String> rewardStr;
    @SerializedName("finish_cmd")
    String finishCmd;
    @SerializedName("task_text")
    String text;
    @SerializedName("consume")
    boolean consume;

    transient BookTextRenderer textRender;

    @Override
    public void build(BookEntry entry, int pageNum) {
        super.build(entry, pageNum);
        for (String i : itemsStr) {
            items.add(ItemStackUtil.loadIngredientFromString(i));
        }
        for (String i : rewardStr) {
            reward.add(ItemStackUtil.loadStackFromString(i));
        }
        new C2STaskSyncPacket(new ResourceLocation(book.getBookItem().getTag().getString(TAG_BOOK)), this.entry.getId(), this.pageNum).send();
    }

    @Override
    public void onDisplayed(GuiBookEntry parent, int left, int top) {
        super.onDisplayed(parent, left, top);
        onHidden(parent);
        Button button = new Button(GuiBook.PAGE_WIDTH / 2 - 50, GuiBook.PAGE_HEIGHT - 25, 100, 20, "", this::questButtonClicked);
        addButton(button);
        updateButtonText(button);
        textRender = new BookTextRenderer(parent, text, 0, getTextHeight());
    }

    @Override
    public int getTextHeight() {
        int wrap = GuiBook.PAGE_WIDTH / 24;
        return 10 + (4 + 8 + 24 * (items.size() / wrap + 1));
    }

    @Override
    public void render(int mouseX, int mouseY, float pticks) {
        int wrap = GuiBook.PAGE_WIDTH / 24;
        int recipeX = GuiBook.PAGE_WIDTH / 2 - 49;
        int recipeY = 10;
        parent.drawCenteredStringNoShadow(I18n.format("patchouliquests.task.need"), recipeX + 4, recipeY - 6, book.textColor);
        for (int i = 0; i < items.size(); i++) {
            RenderSystem.enableBlend();
            RenderSystem.color4f(1F, 1F, 1F, 1F);
            mc.textureManager.bindTexture(book.craftingTexture);
            AbstractGui.blit(recipeX + (i % wrap) * 24, recipeY + (i / wrap) * 24 + 4, 83, 71, 24, 24, 128, 128);
            parent.renderIngredient(recipeX + (i % wrap) * 24 + 4, recipeY + (i / wrap) * 24 + 8, mouseX, mouseY, items.get(i));
        }
        textRender.render(mouseX, mouseY);
        recipeY = GuiBook.PAGE_HEIGHT - ((int) Math.ceil(reward.size() * 1.0 / wrap)) * 24 - 12 - 25;
        parent.drawCenteredStringNoShadow(I18n.format("patchouliquests.task.reward"), recipeX + 4, recipeY - 6, book.textColor);
        for (int i = 0; i < reward.size(); i++) {
            RenderSystem.enableBlend();
            RenderSystem.color4f(1F, 1F, 1F, 1F);
            mc.textureManager.bindTexture(book.craftingTexture);
            AbstractGui.blit(recipeX + (i % wrap) * 24, recipeY + (i / wrap) * 24 + 4, 83, 71, 24, 24, 128, 128);
            parent.renderItemStack(recipeX + (i % wrap) * 24 + 4, recipeY + (i / wrap) * 24 + 8, mouseX, mouseY, reward.get(i));
        }
    }

    protected void questButtonClicked(Button button) {
        String res = entry.getId().toString();
        new C2STaskCheckPacket(new ResourceLocation(book.getBookItem().getTag().getString(TAG_BOOK)), this.entry.getId(), this.consume, this.pageNum).send();
        boolean ok = CheckUtil.checkTask(items, Minecraft.getInstance().player.container.getInventory(),consume);
        stats = ok ? 1 : -1;
        boolean task = true;
        for (BookPage i : entry.getPages()){
            if (i instanceof CollectTaskPage){
                if (((CollectTaskPage) i).stats <= 0){
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

    private void updateButtonText(Button button) {
        String s = I18n.format(stats > 0 ? "patchouliquests.task.over" : "patchouliquests.task.submit");
        button.setMessage(s);
    }

    public boolean isCompleted(Book book) {
        return PersistentData.data.getBookData(book).completedManualQuests.contains(entry.getId().toString());
    }
}
