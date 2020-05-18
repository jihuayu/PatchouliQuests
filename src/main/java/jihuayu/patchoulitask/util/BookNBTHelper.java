package jihuayu.patchoulitask.util;

import jihuayu.patchoulitask.old.task.BaseTaskPage;
import jihuayu.patchoulitask.page.PageBaseQuest;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.ListNBT;
import vazkii.patchouli.client.book.BookPage;

import java.util.List;

public class BookNBTHelper {
    public static boolean isOver(PlayerEntity playerEntity, String book, String entry, int page) {
        return NBTHelper.of(playerEntity.getPersistentData()).getBoolean(String.format("patchouliquests.%s.%s.%d.over", book, entry, page), false);
    }

    public static ListNBT getTaskNum(PlayerEntity playerEntity, String book, String entry, int page) {
        return NBTHelper.of(playerEntity.getPersistentData()).getTagList(String.format("patchouliquests.%s.%s.%d.num", book, entry, page), NBTHelper.NBT.INT);
    }

    public static int getTaskNum(PlayerEntity playerEntity, String book, String entry, int page, int index) {
        return NBTHelper.of(playerEntity.getPersistentData()).getInt(String.format("patchouliquests.%s.%s.%d.num.%d", book, entry, page, index), 0);
    }

    public static ListNBT getRewardStats(PlayerEntity playerEntity, String book, String entry, int page) {
        return NBTHelper.of(playerEntity.getPersistentData()).getTagList(String.format("patchouliquests.%s.%s.%d.reward", book, entry, page), NBTHelper.NBT.BYTE);
    }

    public static boolean getRewardStats(PlayerEntity playerEntity, String book, String entry, int page, int index) {
        return NBTHelper.of(playerEntity.getPersistentData()).getBoolean(String.format("patchouliquests.%s.%s.%d.reward.%d", book, entry, page, index), false);
    }

    public static boolean isLock(PlayerEntity playerEntity, String book, String entry, int page) {
        return NBTHelper.of(playerEntity.getPersistentData()).getBoolean(String.format("patchouliquests.%s.%s.%d.lock", book, entry, page), false);
    }

    public static boolean isHide(PlayerEntity playerEntity, String book, String entry, int page) {
        return NBTHelper.of(playerEntity.getPersistentData()).getBoolean(String.format("patchouliquests.%s.%s.%d.hide", book, entry, page), false);
    }

    public static boolean hasLock(PlayerEntity playerEntity, String book, String entry, int page) {
        return NBTHelper.of(playerEntity.getPersistentData()).hasTag(String.format("patchouliquests.%s.%s.%d.lock", book, entry, page), NBTHelper.NBT.BYTE);
    }

    public static boolean hasHide(PlayerEntity playerEntity, String book, String entry, int page) {
        return NBTHelper.of(playerEntity.getPersistentData()).hasTag(String.format("patchouliquests.%s.%s.%d.hide", book, entry, page), NBTHelper.NBT.BYTE);
    }

    public static void setOver(PlayerEntity playerEntity, String book, String entry, int page, boolean ok) {
        NBTHelper.of(playerEntity.getPersistentData()).setBoolean(String.format("patchouliquests.%s.%s.%d.over", book, entry, page), ok);
    }

    public static void setTaskNum(PlayerEntity playerEntity, String book, String entry, int page, ListNBT list) {
        NBTHelper.of(playerEntity.getPersistentData()).setTag(String.format("patchouliquests.%s.%s.%d.num", book, entry, page), list);
    }

    public static void setTaskNum(PlayerEntity playerEntity, String book, String entry, int page, int index, int ok) {
        NBTHelper.of(playerEntity.getPersistentData()).setInt(String.format("patchouliquests.%s.%s.%d.num.%d", book, entry, page, index), ok);
    }

    public static void setRewardStats(PlayerEntity playerEntity, String book, String entry, int page, ListNBT list) {
        NBTHelper.of(playerEntity.getPersistentData()).setTag(String.format("patchouliquests.%s.%s.%d.reward", book, entry, page), list);
    }

    public static void setRewardStats(PlayerEntity playerEntity, String book, String entry, int page, int index, boolean ok) {
        NBTHelper.of(playerEntity.getPersistentData()).setBoolean(String.format("patchouliquests.%s.%s.%d.reward.%d", book, entry, page, index), ok);
    }


    public static void setLock(PlayerEntity playerEntity, String book, String entry, int page, boolean ok) {
        NBTHelper.of(playerEntity.getPersistentData()).setBoolean(String.format("patchouliquests.%s.%s.%d.lock", book, entry, page), ok);
    }

    public static void setHide(PlayerEntity playerEntity, String book, String entry, int page, boolean ok) {
        NBTHelper.of(playerEntity.getPersistentData()).setBoolean(String.format("patchouliquests.%s.%s.%d.hide", book, entry, page), ok);
    }

    public static void setKillNum(PlayerEntity playerEntity, String book, String entry, int page, int num) {
        NBTHelper.of(playerEntity.getPersistentData()).setInt(String.format("patchouliquests.%s.%s.%d.num", book, entry, page), num);
    }

    public static int getKillNum(PlayerEntity playerEntity, String book, String entry, int page) {
        return NBTHelper.of(playerEntity.getPersistentData()).getInt(String.format("patchouliquests.%s.%s.%d.num", book, entry, page), 0);
    }

    public static PageBaseQuest getPage(List<BookPage> pages, int id) {
        for (BookPage page : pages) {
            if (page instanceof PageBaseQuest && ((PageBaseQuest) page).id == id) {
                return (PageBaseQuest) page;
            }
        }
        return null;
    }
}
