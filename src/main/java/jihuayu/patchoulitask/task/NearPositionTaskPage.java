package jihuayu.patchoulitask.task;


import com.google.gson.annotations.SerializedName;
import jihuayu.patchoulitask.net.collect.C2SCollectTaskSyncPacket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.ArrayList;
import java.util.List;

import static vazkii.patchouli.common.item.ItemModBook.TAG_BOOK;

public class NearPositionTaskPage extends BaseTaskPage {
    public transient List<ItemStack> reward = new ArrayList<>();

    public int x;
    public int y;
    public int z;

    @SerializedName("reward")
    public List<String> rewardStr;
    @SerializedName("finish_cmd")
    public String finishCmd;

    @Override
    public void build(BookEntry entry, int pageNum) {
        super.build(entry, pageNum);

        for (String i : rewardStr) {
            reward.add(ItemStackUtil.loadStackFromString(i));
        }
        if (stats==0){
            new C2SCollectTaskSyncPacket(new ResourceLocation(book.getBookItem().getTag().getString(TAG_BOOK)), this.entry.getId(), this.pageNum).send();
        }
    }

    @Override
    public void onDisplayed(GuiBookEntry parent, int left, int top) {
        super.onDisplayed(parent, left, top);
    }
}
