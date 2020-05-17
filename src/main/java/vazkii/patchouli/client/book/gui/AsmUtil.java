package vazkii.patchouli.client.book.gui;

import jihuayu.patchoulitask.api.MouseHandler;

public class AsmUtil {

    public static boolean asm1(GuiBook book, double mouseX, double mouseY, double scroll) {
        if (book instanceof GuiBookEntry) {
            if (((GuiBookEntry) book).leftPage instanceof MouseHandler) {
                if (((MouseHandler) ((GuiBookEntry) book).leftPage).onMouse(mouseX - GuiBookEntry.LEFT_PAGE_X
                        , mouseY- GuiBookEntry.TOP_PADDING, scroll )) {
                    return true;
                }
            }
            if (((GuiBookEntry) book).rightPage instanceof MouseHandler) {
                if (((MouseHandler) ((GuiBookEntry) book).rightPage).onMouse(mouseX - GuiBookEntry.PAGE_WIDTH - GuiBookEntry.LEFT_PAGE_X - GuiBookEntry.MAX_BOOKMARKS
                        , mouseY - GuiBookEntry.TOP_PADDING, scroll)) {
                    return true;

                }
            }
        }
        return false;
    }
}
