package jihuayu.patchoulitask.api;

import net.minecraft.client.gui.widget.button.Button;

public interface RenderAble {
    int mouseClicked1(double mouseX, double mouseY, int mouseButton);
    boolean questButtonClicked1(Button button);
    boolean render1(int mouseX, int mouseY, float pticks);

}
