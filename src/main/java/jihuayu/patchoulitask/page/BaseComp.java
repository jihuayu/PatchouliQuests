package jihuayu.patchoulitask.page;

import jihuayu.patchoulitask.api.MouseHandler;
import jihuayu.patchoulitask.api.PageComp;
import jihuayu.patchoulitask.api.RenderAble;
import net.minecraft.client.gui.widget.button.Button;

public abstract class BaseComp implements RenderAble, PageComp, MouseHandler {
    public transient PageBaseQuest page;
    public transient int num;

    @Override
    public int mouseClicked1(double mouseX, double mouseY, int mouseButton) {
        return 0;
    }

    @Override
    public boolean questButtonClicked1(Button button) {
        return false;
    }

    @Override
    public boolean render1(int mouseX, int mouseY, float pticks) {
        return false;
    }

    @Override
    public void setIndex(int x) {
        num = x;
    }

    @Override
    public void setPage(PageBaseQuest page) {
        this.page = page;
    }

    @Override
    public boolean onMouse(double mouseX, double mouseY, double scroll) {
        return false;
    }
}