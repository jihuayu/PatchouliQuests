package jihuayu.patchoulitask.util;

import jihuayu.patchoulitask.task.BaseTaskPage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.client.book.BookPage;

import java.util.List;

public class BookNBTHelper {
    public static boolean isOver(PlayerEntity playerEntity, String book, String entry,int page){
        return NBTHelper.of(playerEntity.getPersistentData()).getBoolean(String.format("patchouliquests.%s.%s.%d.over", book, entry, page),false);
    }

    public static ListNBT getTaskNum(PlayerEntity playerEntity, String book, String entry, int page){
        return NBTHelper.of(playerEntity.getPersistentData()).getTagList(String.format("patchouliquests.%s.%s.%d.num", book, entry, page),NBTHelper.NBT.INT);
    }

    public static boolean isLock(PlayerEntity playerEntity, String book, String entry,int page){
        return NBTHelper.of(playerEntity.getPersistentData()).getBoolean(String.format("patchouliquests.%s.%s.%d.lock", book, entry, page),false);
    }

    public static boolean isHide(PlayerEntity playerEntity, String book, String entry,int page){
        return NBTHelper.of(playerEntity.getPersistentData()).getBoolean(String.format("patchouliquests.%s.%s.%d.hide", book, entry, page),false);
    }
    public static boolean hasLock(PlayerEntity playerEntity, String book, String entry,int page){
        return NBTHelper.of(playerEntity.getPersistentData()).hasTag(String.format("patchouliquests.%s.%s.%d.lock", book, entry, page),NBTHelper.NBT.BYTE);
    }

    public static boolean hasHide(PlayerEntity playerEntity, String book, String entry,int page){
        return NBTHelper.of(playerEntity.getPersistentData()).hasTag(String.format("patchouliquests.%s.%s.%d.hide", book, entry, page),NBTHelper.NBT.BYTE);
    }
    public static void setOver(PlayerEntity playerEntity, String book, String entry,int page,boolean ok){
        NBTHelper.of(playerEntity.getPersistentData()).setBoolean(String.format("patchouliquests.%s.%s.%d.over", book, entry, page),ok);
    }

    public static void setTaskNum(PlayerEntity playerEntity, String book, String entry, int page,ListNBT list){
        NBTHelper.of(playerEntity.getPersistentData()).setTag(String.format("patchouliquests.%s.%s.%d.num", book, entry, page),list);
    }

    public static void setLock(PlayerEntity playerEntity, String book, String entry,int page,boolean ok){
        NBTHelper.of(playerEntity.getPersistentData()).setBoolean(String.format("patchouliquests.%s.%s.%d.lock", book, entry, page),ok);
    }

    public static void setHide(PlayerEntity playerEntity, String book, String entry,int page,boolean ok){
        NBTHelper.of(playerEntity.getPersistentData()).setBoolean(String.format("patchouliquests.%s.%s.%d.hide", book, entry, page),ok);
    }

    public static BookPage getPage(List<BookPage> pages,int id){
        for (BookPage page : pages){
            if (page instanceof BaseTaskPage && ((BaseTaskPage) page).id == id) {
                return page;
            }
        }
        return null;
    }
}
