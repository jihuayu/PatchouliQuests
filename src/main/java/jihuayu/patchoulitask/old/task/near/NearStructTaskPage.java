package jihuayu.patchoulitask.old.task.near;

import jihuayu.patchoulitask.old.net.C2STaskSyncPacket;
import jihuayu.patchoulitask.old.task.BaseTaskPage;
import net.minecraft.client.resources.I18n;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBookEntry;

import java.util.ArrayList;
import java.util.List;


public class NearStructTaskPage extends BaseTaskPage {
    public transient static final List<NearStructTaskPage> POSES = new ArrayList<>();

    public String struct;

    public double range;

    @Override
    public void build(BookEntry entry, int pageNum) {
        super.build(entry, pageNum);
        POSES.add(this);
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
        parent.drawCenteredStringNoShadow(I18n.format("patchouliquests.task.near_struct.find", struct),
                fontRenderer.getStringWidth(I18n.format("patchouliquests.task.near_struct.find", struct)) / 2 + 12, recipeY - 6, book.textColor);
        recipeY += 10;
        parent.drawCenteredStringNoShadow(I18n.format("patchouliquests.task.near_pos.range", range),
                fontRenderer.getStringWidth(I18n.format("patchouliquests.task.near_pos.range", range)) / 2 + 12, recipeY - 6, book.textColor);
        return true;
    }
}
