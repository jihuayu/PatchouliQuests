package jihuayu.patchoulitask.comand;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import jihuayu.patchoulitask.net.S2CHideTaskPacket;
import jihuayu.patchoulitask.net.S2CLockTaskPacket;
import jihuayu.patchoulitask.task.BaseTaskPage;
import jihuayu.patchoulitask.util.BookNBTHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.common.item.ItemModBook;

import static jihuayu.patchoulitask.comand.arguments.TSuggestionProviders.AVAILABLE_BOOKS;
import static jihuayu.patchoulitask.comand.arguments.TSuggestionProviders.AVAILABLE_ENTRIES;

public class LockAndHideCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> builder = Commands.literal("pt");
        register2(builder);
        LiteralArgumentBuilder<CommandSource> builder1 = Commands.literal("patchouli_quests");
        register2(builder1);
        dispatcher.register(builder);
        dispatcher.register(builder1);

    }

    public static void register2(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(Commands.literal("task").requires((p_198496_0_) -> {
                    return p_198496_0_.hasPermissionLevel(2);
                }).then(Commands.argument("player", EntityArgument.players())
                .then(Commands.argument("book", ResourceLocationArgument.resourceLocation()).suggests(AVAILABLE_BOOKS)
                        .then(Commands.argument("entry", ResourceLocationArgument.resourceLocation()).suggests(AVAILABLE_ENTRIES)
                                .then(Commands.argument("page", IntegerArgumentType.integer(0))
                                        .then(Commands.literal("lock").executes((ctx) -> {
                                            return lock(EntityArgument.getPlayer(ctx, "player"), ResourceLocationArgument.getResourceLocation(ctx, "book"),
                                                    ResourceLocationArgument.getResourceLocation(ctx, "entry"),
                                                    IntegerArgumentType.getInteger(ctx, "page"), ctx, true);
                                        }))
                                        .then(Commands.literal("un_lock").executes((ctx) -> {
                                            return lock(EntityArgument.getPlayer(ctx, "player"), ResourceLocationArgument.getResourceLocation(ctx, "book"),
                                                    ResourceLocationArgument.getResourceLocation(ctx, "entry"),
                                                    IntegerArgumentType.getInteger(ctx, "page"), ctx, false);
                                        }))
                                        .then(Commands.literal("hide").executes((ctx) -> {
                                            return hide(EntityArgument.getPlayer(ctx, "player"), ResourceLocationArgument.getResourceLocation(ctx, "book"),
                                                    ResourceLocationArgument.getResourceLocation(ctx, "entry"),
                                                    IntegerArgumentType.getInteger(ctx, "page"), ctx, true);
                                        }))
                                        .then(Commands.literal("un_hide").executes((ctx) -> {
                                            return hide(EntityArgument.getPlayer(ctx, "player"), ResourceLocationArgument.getResourceLocation(ctx, "book"),
                                                    ResourceLocationArgument.getResourceLocation(ctx, "entry"),
                                                    IntegerArgumentType.getInteger(ctx, "page"), ctx, false);
                                        }))

                        )))));
    }

    public static int lock(PlayerEntity player, ResourceLocation book, ResourceLocation entry, int page, CommandContext<CommandSource> source, boolean lock) throws CommandSyntaxException {
        try {
            if (player instanceof ServerPlayerEntity) {
                BookPage page1 = ItemModBook.getBook(ItemModBook.forBook(book)).contents.entries.get(entry).getPages().get(page);
                if (page1 instanceof BaseTaskPage) {
                    if (BookNBTHelper.isLock(player, book.toString(), entry.toString(), page) != lock) {
                        BookNBTHelper.setLock(player, book.toString(), entry.toString(), page, lock);
                        new S2CLockTaskPacket(book, entry, page, lock).send((ServerPlayerEntity) player);
                    }
                }
            }
        } catch (Exception e) {
            player.sendMessage(new TranslationTextComponent("patchouliquests.command.failed"));
        }
        return 0;
    }

    public static int hide(PlayerEntity player, ResourceLocation book, ResourceLocation entry, int page, CommandContext<CommandSource> source, boolean hide) throws CommandSyntaxException {
        try {
            System.out.println(1111);
            if (player instanceof ServerPlayerEntity) {
                BookPage page1 = ItemModBook.getBook(ItemModBook.forBook(book)).contents.entries.get(entry).getPages().get(page);
                System.out.println(page1);
                if (page1 instanceof BaseTaskPage) {
                    if (BookNBTHelper.isHide(player, book.toString(), entry.toString(), page) != hide) {
                        BookNBTHelper.setHide(player, book.toString(), entry.toString(), page, hide);
                        System.out.println(hide);
                        new S2CHideTaskPacket(book, entry, page, hide).send((ServerPlayerEntity) player);
                    }
                }
            }
        } catch (Exception e) {
            player.sendMessage(new TranslationTextComponent("patchouliquests.command.failed").setStyle(new Style().setColor(TextFormatting.RED)));
        }
        return 0;
    }
}