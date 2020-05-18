package jihuayu.patchoulitask.api;

import jihuayu.patchoulitask.page.PageBaseQuest;
import net.minecraft.network.PacketBuffer;

public interface PageComp {
    void setIndex(int x);
    void setPage(PageBaseQuest page);

}
