package jihuayu.patchoulitask.comand.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import jihuayu.patchoulitask.task.BaseTaskPage;
import jihuayu.patchoulitask.util.BookNBTHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.ItemModBook;

import java.util.ArrayList;
import java.util.List;

public class TSuggestionProviders {
    public static final SuggestionProvider<CommandSource> AVAILABLE_BOOKS = SuggestionProviders.register(new ResourceLocation("available_books"), (ctx, p_197495_1_) -> {
        return ISuggestionProvider.suggestIterable(BookRegistry.INSTANCE.books.keySet(), p_197495_1_);
    });
    public static final SuggestionProvider<CommandSource> AVAILABLE_ENTRIES = SuggestionProviders.register(new ResourceLocation("available_entries"), (ctx, p_197495_1_) -> {
        try {
            Book book = ItemModBook.getBook(ItemModBook.forBook(ctx.getArgument("book", ResourceLocation.class)));
            return ISuggestionProvider.suggestIterable(book.contents.entries.keySet(), p_197495_1_);
        }
        catch (Exception e){
            return ISuggestionProvider.suggestIterable(Lists.newArrayList(), p_197495_1_);
        }
    });

//    public static final SuggestionProvider<CommandSource> AVAILABLE_PAGES = SuggestionProviders.register(new ResourceLocation("available_pages"), (ctx, p_197495_1_) -> {
//        try {
//            Book book = ItemModBook.getBook(ItemModBook.forBook(ctx.getArgument("book", ResourceLocation.class)));
//            List<Integer> list = new ArrayList<>();
//            for (BookPage page : book.contents.entries.get(ctx.getArgument("entry", ResourceLocation.class)).getPages()){
//                if (page instanceof BaseTaskPage)
//                    list.add(((BaseTaskPage) page).id);
//            }
//            return ISuggestionProvider.suggestIterable(list, p_197495_1_);
//        }
//        catch (Exception e){
//            return ISuggestionProvider.suggestIterable(Lists.newArrayList(), p_197495_1_);
//        }
//    });
}
