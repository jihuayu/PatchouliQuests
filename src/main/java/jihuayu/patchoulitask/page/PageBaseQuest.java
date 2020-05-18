package jihuayu.patchoulitask.page;

import com.google.gson.*;
import jihuayu.patchoulitask.ModMain;
import jihuayu.patchoulitask.api.MouseHandler;
import jihuayu.patchoulitask.api.PageComp;
import jihuayu.patchoulitask.page.reward.BaseReward;
import jihuayu.patchoulitask.page.task.BaseTask;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.page.PageQuest;
import vazkii.patchouli.common.book.Book;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PageBaseQuest extends PageQuest implements MouseHandler {
    private static final ResourceLocation L_ARRAY = new ResourceLocation(ModMain.MOD_ID, "l_array");
    private static final ResourceLocation R_ARRAY = new ResourceLocation(ModMain.MOD_ID, "r_array");

    public transient int defaultTask = 0;
    public transient int defaultReward = 0;
    public transient boolean hide = false;
    public transient boolean lock = false;
    public transient boolean is_hide = false;
    public transient int stats = 0;
    public transient int now_task_page = 0;
    public transient int now_reward_page = 0;
    protected transient Button button;

    public static final int rewardPrePage = 4;

    public List<BaseTask> tasks = new ArrayList<>();
    public List<BaseReward> rewards = new ArrayList<>();
    public boolean defaultHide = false;
    public boolean defaultLock = false;
    public int id;

    public BookEntry getEntry() {
        return entry;
    }

    public GuiBookEntry getParent() {
        return parent;
    }

    @Override
    public void build(BookEntry entry, int pageNum) {
        super.build(entry, pageNum);
        if (id == 0) {
            id = pageNum + 1;
        }
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
    final public void render(int mouseX, int mouseY, float pticks) {
        super.render(mouseX, mouseY, pticks);
        render1(mouseX, mouseY, pticks);
    }

    protected boolean render1(int mouseX, int mouseY, float pticks) {
        if ((!is_hide) && hide)
            onHidden(parent);
        if (hide) {
            return false;
        }
        if (is_hide)
            addButton(button);
        updateButtonText(button);
        int recipeY = 25;
        if (tasks.size() > 0) {
            parent.drawCenteredStringNoShadow(I18n.format("patchouliquests.task.need", now_task_page + 1, tasks.size()), fontRenderer.getStringWidth(I18n.format("patchouliquests.task.need", now_task_page + 1, tasks.size())) / 2 + 4, recipeY - 6, book.textColor);
            if (tasks.size() > now_task_page)
                tasks.get(now_task_page).render1(mouseX, mouseY, pticks);
        }
        recipeY = GuiBook.PAGE_HEIGHT - 24 - 12 - 25;
        if (rewards.size() > 0) {
            parent.drawCenteredStringNoShadow(I18n.format("patchouliquests.task.reward", now_reward_page + 1, (rewards.size() - 1) / rewardPrePage + 1),
                    fontRenderer.getStringWidth(I18n.format("patchouliquests.task.reward", now_reward_page + 1, (rewards.size() - 1) / rewardPrePage + 1)) / 2 + 4, recipeY - 6, book.textColor);
            for (int i = now_reward_page * rewardPrePage; i < (now_reward_page + 1) * rewardPrePage; i++) {
                if (rewards.size() > i) {
                    rewards.get(i).render1(mouseX, mouseY, pticks);
                }
            }
        }

        return true;
    }

    public void updateButtonText(Button button) {
        if (lock) {
            button.setMessage(I18n.format("patchouliquests.task.lock"));
            return;
        }
        String s = I18n.format(stats > 0 ? "patchouliquests.task.over" : "patchouliquests.task.submit");
        button.setMessage(s);
    }

    @Override
    public boolean isCompleted(Book book) {
        try {
            return PersistentData.data.getBookData(book).completedManualQuests.contains(entry.getId().toString());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    final public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return mouseClicked1(mouseX, mouseY, mouseButton) > 0;
    }

    @Override
    public boolean onMouse(double mouseX, double mouseY, double scroll) {
        if (hide) return false;
        if (parent.isMouseInRelativeRange(mouseX, mouseY, GuiBook.PAGE_WIDTH / 2 - 49, GuiBook.PAGE_HEIGHT - 12 - 25 - 24, 24 * 4, 24)) {
            if (scroll < 0) {
                if (now_reward_page > 0)
                    now_reward_page--;
                return true;
            }
            if (scroll > 0) {
                if (now_reward_page < rewards.size() / rewardPrePage - 1)
                    now_reward_page++;
                return true;
            }
        }
        if (parent.isMouseInRelativeRange(mouseX, mouseY, GuiBook.PAGE_WIDTH / 2 - 49, 25, 24 * 4, 24 * 2)) {
            if (scroll < 0) {
                if (now_task_page > 0)
                    now_task_page--;
                return true;
            }
            if (scroll > 0) {
                if (now_task_page < tasks.size() - 1)
                    now_task_page++;
                return true;
            }
        }
        return false;
    }

    protected int mouseClicked1(double mouseX, double mouseY, int mouseButton) {
        if (hide) return -1;
        System.out.println(mouseButton);
        if (parent.isMouseInRelativeRange(mouseX, mouseY, GuiBook.PAGE_WIDTH / 2 - 49, GuiBook.PAGE_HEIGHT - 12 - 25 - 24, 24 * 4, 24)) {
            if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_4) {
                if (now_reward_page > 0)
                    now_reward_page--;
            }
            if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_5) {
                if (now_reward_page < rewards.size() / rewardPrePage - 1)
                    now_reward_page++;
            }
            return 1;
        }
        return 0;
    }

    @Override
    protected void questButtonClicked(Button button) {
        questButtonClicked1(button);
    }

    protected boolean questButtonClicked1(Button button) {
        if (lock) return false;
        return true;
    }

    public void renderItemStackAndNumAndGet(int x, int y, int mouseX, int mouseY, ItemStack stack, int ok) {
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
            if (ok == 0)
                list.add(new TranslationTextComponent("patchouliquests.tooltip.item.get"));
            parent.setTooltip(list);
        }
        RenderHelper.disableStandardItemLighting();
    }


    public static class Deserializer implements JsonDeserializer<PageBaseQuest> {
        public PageBaseQuest deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            PageBaseQuest page = new PageBaseQuest();
            if (json instanceof JsonObject) {
                JsonElement task = ((JsonObject) json).get("task");
                parser(task, BaseTask.class, page.tasks, ctx, page);
                JsonElement reward = ((JsonObject) json).get("reward");
                parser(reward, BaseReward.class, page.rewards, ctx, page);
                JsonElement hide = ((JsonObject) json).get("hide");
                if (hide != null)
                    page.defaultHide = hide.getAsBoolean();
                JsonElement lock = ((JsonObject) json).get("lock");
                if (lock != null)
                    page.defaultLock = lock.getAsBoolean();
                JsonElement id = ((JsonObject) json).get("id");
                if (id != null)
                    page.id = id.getAsInt();
            }
            return page;
        }

        private <T> void parser(JsonElement json, Class<T> type, List<T> list, JsonDeserializationContext ctx, PageBaseQuest page) {
            if (json instanceof JsonObject) {
                T i = ctx.deserialize(json, type);
                if (i instanceof PageComp) {
                    ((PageComp) i).setPage(page);
                    ((PageComp) i).setIndex(0);
                }
                list.add(i);
            } else if (json instanceof JsonArray) {
                int num = 0;
                for (JsonElement i : (JsonArray) json) {
                    T p = ctx.deserialize(i, type);
                    if (p instanceof PageComp) {
                        ((PageComp) p).setPage(page);
                        ((PageComp) p).setIndex(num++);
                    }
                    list.add(p);
                }
            }
        }
    }
}
