package jihuayu.patchoulitask.old.task.near;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import jihuayu.patchoulitask.old.net.C2STaskSyncPacket;
import jihuayu.patchoulitask.old.task.BaseTaskPage;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.common.multiblock.StringStateMatcher;

import java.util.ArrayList;
import java.util.List;


public class NearBlockTaskPage extends BaseTaskPage {
    public transient static final List<NearBlockTaskPage> LISTS = new ArrayList<>();

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
            LISTS.add(this);

        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        entry.getPages().add(this);
    }

    @Override
    public void onDisplayed(GuiBookEntry parent, int left, int top) {
        super.onDisplayed(parent, left, top);
        if (stats == 0) {
            new C2STaskSyncPacket(book.id, this.entry.getId(), this.id).send();
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
