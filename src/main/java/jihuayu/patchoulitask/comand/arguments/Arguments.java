package jihuayu.patchoulitask.comand.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

public class Arguments {
    public static Book getBook(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
        return ItemModBook.getBook(ItemModBook.forBook(context.getArgument(name, ResourceLocation.class)));
    }

    public static BookEntry getEntry(CommandContext<CommandSource> context, Book book,String name) throws CommandSyntaxException {
        return book.contents.entries.get(context.getArgument(name, ResourceLocation.class));
    }
}
