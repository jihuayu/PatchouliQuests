package jihuayu.patchoulitask.page;

import com.google.gson.*;
import jihuayu.patchoulitask.api.MouseHandler;
import jihuayu.patchoulitask.api.NetComp;
import jihuayu.patchoulitask.api.PageComp;
import jihuayu.patchoulitask.page.reward.BaseReward;
import jihuayu.patchoulitask.page.task.BaseTask;
import jihuayu.patchoulitask.util.NBTHelper;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
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

public class PageBaseQuest extends PageQuest implements MouseHandler, NetComp {

    public boolean hide = false;
    public boolean lock = false;
    public int stats = 0;

    public transient int mouseX = -1;
    public transient int mouseY = -1;
    public transient boolean is_hide = false;
    public transient int now_task_page = 0;
    public transient int now_reward_page = 0;
    protected transient Button button;

    public static final int rewardPrePage = 4;

    public transient List<BaseTask> tasks = new ArrayList<>();
    public transient List<BaseReward> rewards = new ArrayList<>();
    public transient boolean defaultHide = false;
    public transient boolean defaultLock = false;
    public transient int id;

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
        int num = 0;
        for (BaseTask i : tasks) {
            if (i.stats > 0) {
                num = i.num;
                break;
            }
        }
        now_task_page = num;
    }

    @Override
    final public void render(int mouseX, int mouseY, float pticks) {
        super.render(mouseX, mouseY, pticks);
        render1(mouseX, mouseY, pticks);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    protected boolean render1(int mouseX, int mouseY, float pticks) {
        if ((!is_hide) && hide)
            onHidden(parent);
        if (hide) {
            return false;
        }
        if (is_hide)
            addButton(button);
        checkStats(parent.book);
        updateButtonText(button);
        int recipeY = 25;
        if (tasks.size() > 0) {
            parent.drawCenteredStringNoShadow( I18n.format("patchouliquests.task.need", now_task_page + 1, tasks.size()) ,
                    fontRenderer.getStringWidth(I18n.format("patchouliquests.task.need", now_task_page + 1, tasks.size())) / 2 + 4, recipeY - 6, book.textColor);
            if (tasks.size() > now_task_page)
                tasks.get(now_task_page).render1(mouseX, mouseY, pticks);
        }
        recipeY = GuiBook.PAGE_HEIGHT - 24 - 12 - 25;
        if (rewards.size() > 0) {
            parent.drawCenteredStringNoShadow(I18n.format("patchouliquests.task.reward", now_reward_page + 1, (rewards.size() - 1) / rewardPrePage + 1),
                    fontRenderer.getStringWidth(I18n.format("patchouliquests.task.reward", now_reward_page + 1, (rewards.size() - 1) / rewardPrePage + 1)) / 2 + 4,
                    recipeY - 6, stats > 0 ? 0xff00ff00 : book.textColor);
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
        if (stats > 0) {
            button.setMessage(I18n.format("patchouliquests.task.over"));
            return;
        }
        String s = I18n.format(tasks.get(now_task_page).stats > 0 ? "patchouliquests.task.over" : "patchouliquests.task.submit");
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
    public boolean onMouseWheel(double y, double x, double scroll) {
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

                if (now_task_page < tasks.size() - 1)
                    now_task_page++;
                return true;
            }
            if (scroll > 0) {
                if (now_task_page > 0)
                    now_task_page--;
                return true;
            }
        }
        return false;
    }

    protected int mouseClicked1(double mouseX, double mouseY, int mouseButton) {
        if (hide) return -1;
        if (parent.isMouseInRelativeRange(mouseX, mouseY, GuiBook.PAGE_WIDTH / 2 - 49, GuiBook.PAGE_HEIGHT - 12 - 25 - 24, 24 * 4, 24)) {
            if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_4) {
                if (now_reward_page < rewards.size() / rewardPrePage - 1) {
                    now_reward_page++;
                    return 1;
                }

            }
            if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_5) {
                if (now_reward_page > 0) {
                    now_reward_page--;
                    return 1;
                }
            }
            for (int i = now_reward_page * 4; i < Math.min((now_task_page + 1) * 4, rewards.size()); i++) {
                if (rewards.get(i).mouseClicked1(mouseX, mouseY, mouseButton) == 1) {
                    return 1;
                }
            }
        }
        if (parent.isMouseInRelativeRange(mouseX, mouseY, GuiBook.PAGE_WIDTH / 2 - 49, 25, 24 * 4, 24 * 2)) {
            if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_4) {
                if (now_task_page < tasks.size() - 1) {
                    now_task_page++;
                    return 1;
                }
            }
            if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_5) {
                if (now_task_page > 0) {
                    now_task_page--;
                    return 1;
                }
            }
            if (tasks.get(now_task_page).mouseClicked1(mouseX, mouseY, mouseButton) == 1) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    protected void questButtonClicked(Button button) {
        questButtonClicked1(button);
    }

    public void checkStats(Book book) {
        String res = entry.getId().toString();
        PersistentData.DataHolder.BookData data = PersistentData.data.getBookData(book);
        for (BaseTask i : tasks) {
            if (i.stats < 1) {
                stats = -1;
                if (data.completedManualQuests.contains(res)) {
                    data.completedManualQuests.remove(res);
                    PersistentData.save();
                    entry.markReadStateDirty();
                }
                return;
            }
        }
        stats = 1;
        if (!data.completedManualQuests.contains(res)) {
            data.completedManualQuests.add(res);
        }
        PersistentData.save();
        entry.markReadStateDirty();
    }

    protected void questButtonClicked1(Button button) {
        if (lock) return;
        if (tasks.get(now_task_page).stats > 0) return;
        tasks.get(now_task_page).tryComplete();
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

    public int getStats(PlayerEntity playerEntity) {
        return NBTHelper.of(playerEntity.getPersistentData()).getInt(String.format("patchouliquests.%s.%s.%d.stats",
                book.id.toString(), getEntry().getId().toString(), id), 0);
    }

    public void setStats(PlayerEntity playerEntity, int stats) {
        NBTHelper.of(playerEntity.getPersistentData()).setInt(String.format("patchouliquests.%s.%s.%d.stats",
                book.id.toString(), getEntry().getId().toString(), id), stats);
    }

    public boolean getHide(PlayerEntity playerEntity) {
        return NBTHelper.of(playerEntity.getPersistentData()).getBoolean(String.format("patchouliquests.%s.%s.%d.hide",
                book.id.toString(), getEntry().getId().toString(), id), false);
    }

    public void setHide(PlayerEntity playerEntity, boolean hide) {
        NBTHelper.of(playerEntity.getPersistentData()).setBoolean(String.format("patchouliquests.%s.%s.%d.hide",
                book.id.toString(), getEntry().getId().toString(), id), hide);
    }

    public boolean getLock(PlayerEntity playerEntity) {
        return NBTHelper.of(playerEntity.getPersistentData()).getBoolean(String.format("patchouliquests.%s.%s.%d.lock",
                book.id.toString(), getEntry().getId().toString(), id), false);
    }

    public void setLock(PlayerEntity playerEntity, boolean hide) {
        NBTHelper.of(playerEntity.getPersistentData()).setBoolean(String.format("patchouliquests.%s.%s.%d.lock",
                book.id.toString(), getEntry().getId().toString(), id), hide);
    }

    public void readBuffer(PacketBuffer buffer) {
//        stats = buffer.readVarInt();
        hide = buffer.readBoolean();
        lock = buffer.readBoolean();
    }

    public void writeBuffer(PacketBuffer buffer, ServerPlayerEntity entity) {
//        buffer.writeVarInt(getStats(entity));
        buffer.writeBoolean(getHide(entity));
        buffer.writeBoolean(getLock(entity));
    }
}
