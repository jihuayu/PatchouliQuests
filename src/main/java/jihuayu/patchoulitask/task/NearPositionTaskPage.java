package jihuayu.patchoulitask.task;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import vazkii.patchouli.client.book.BookEntry;

import java.util.ArrayList;
import java.util.List;


public class NearPositionTaskPage extends BaseTaskPage {
    public transient List<ItemStack> reward = new ArrayList<>();
    public transient static final List<NearPositionTaskPage> POSES = new ArrayList<>();

    public int x;
    public int y;
    public int z;

    public double range;

    @Override
    public void build(BookEntry entry, int pageNum) {
        super.build(entry, pageNum);
        POSES.add(this);

    }

    @Override
    public boolean render1(int mouseX, int mouseY, float pticks) {
        if (!super.render1(mouseX, mouseY, pticks)) return false;
        int recipeY = 25;
        parent.drawCenteredStringNoShadow(I18n.format("patchouliquests.task.near_pos.intr"),
                fontRenderer.getStringWidth(I18n.format("patchouliquests.task.near_pos.intr")) / 2 + 4, recipeY - 6, book.textColor);
        recipeY+=10;
        parent.drawCenteredStringNoShadow(I18n.format("patchouliquests.task.near_pos.x",x),
                fontRenderer.getStringWidth(I18n.format("patchouliquests.task.near_pos.x",x)) / 2 + 12, recipeY - 6, book.textColor);
        recipeY+=10;
        parent.drawCenteredStringNoShadow(I18n.format("patchouliquests.task.near_pos.y",y),
                fontRenderer.getStringWidth(I18n.format("patchouliquests.task.near_pos.y",y)) / 2 + 12, recipeY - 6, book.textColor);
        recipeY+=10;
        parent.drawCenteredStringNoShadow(I18n.format("patchouliquests.task.near_pos.z",z),
                fontRenderer.getStringWidth(I18n.format("patchouliquests.task.near_pos.z",z)) / 2 + 12, recipeY - 6, book.textColor);
        recipeY+=10;
        parent.drawCenteredStringNoShadow(I18n.format("patchouliquests.task.near_pos.range",z),
                fontRenderer.getStringWidth(I18n.format("patchouliquests.task.near_pos.range",z)) / 2 + 12, recipeY - 6, book.textColor);
        return true;
    }
}
