package jihuayu.patchoulitask.util;

import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.common.book.BookRegistry;

import java.util.*;

public class PaletteHelper {
    public static Map<ResourceLocation,List<ResourceLocation>> PALETTE_LIST = new HashMap<>();
    public static List<ResourceLocation> BOOK_LIST = Lists.newArrayList(BookRegistry.INSTANCE.books.keySet());
}
