package jihuayu.patchoulitask.task;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import jihuayu.patchoulitask.net.C2STaskSyncPacket;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.common.multiblock.StringStateMatcher;

import java.util.ArrayList;
import java.util.List;

import static vazkii.patchouli.common.item.ItemModBook.TAG_BOOK;


public class NearBlockTaskPage extends BaseTaskPage {
    public transient static final List<NearBlockTaskPage> POSES = new ArrayList<>();

    public transient BlockState blockState;

    public String block;

    public double range;

    @Override
    public void build(BookEntry entry, int pageNum) {
        super.build(entry, pageNum);
        try {
            if (!block.isEmpty()) {
                blockState = StringStateMatcher.fromString(block).getDisplayedState(0);
            }
            POSES.add(this);
            if (stats == 0) {
                new C2STaskSyncPacket(new ResourceLocation(book.getBookItem().getTag().getString(TAG_BOOK)), this.entry.getId(), this.id).send();
            }
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean render1(int mouseX, int mouseY, float pticks) {
        if (!super.render1(mouseX, mouseY, pticks)) return false;
        int recipeY = 25;

        recipeY += 10;
        parent.drawCenteredStringNoShadow(I18n.format("patchouliquests.task.near_pos.range", range),
                fontRenderer.getStringWidth(I18n.format("patchouliquests.task.near_pos.range", range)) / 2 + 12, recipeY - 6, book.textColor);
        return true;
    }
}
