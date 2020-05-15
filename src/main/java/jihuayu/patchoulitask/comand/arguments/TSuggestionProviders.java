package jihuayu.patchoulitask.comand.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.ItemModBook;

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
}
