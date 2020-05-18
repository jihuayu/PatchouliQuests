package jihuayu.patchoulitask.comand;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import jihuayu.patchoulitask.net.S2CBasePagePacket;
import jihuayu.patchoulitask.old.net.S2CTaskCheckPacket;
import jihuayu.patchoulitask.old.net.cmd.S2CCompleteTaskPacket;
import jihuayu.patchoulitask.old.net.cmd.S2CHideTaskPacket;
import jihuayu.patchoulitask.old.net.cmd.S2CLockTaskPacket;
import jihuayu.patchoulitask.old.task.BaseTaskPage;
import jihuayu.patchoulitask.page.PageBaseQuest;
import jihuayu.patchoulitask.util.BookNBTHelper;
import jihuayu.patchoulitask.worldstorage.TeamWorldSavedData;
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

import java.util.ArrayList;

import static jihuayu.patchoulitask.comand.arguments.TSuggestionProviders.AVAILABLE_BOOKS;
import static jihuayu.patchoulitask.comand.arguments.TSuggestionProviders.AVAILABLE_ENTRIES;

public class LockAndHideCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> builder1 = Commands.literal("patchouli_quests");
        register2(builder1);
        dispatcher.register(builder1);

    }

    public static void register2(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(Commands.literal("task").requires((p_198496_0_) -> {
            return p_198496_0_.hasPermissionLevel(2);
        }).then(Commands.argument("player", EntityArgument.players())
                .then(Commands.argument("book", ResourceLocationArgument.resourceLocation()).suggests(AVAILABLE_BOOKS)
                        .then(Commands.argument("entry", ResourceLocationArgument.resourceLocation()).suggests(AVAILABLE_ENTRIES)
                                .then(Commands.argument("id", IntegerArgumentType.integer(0))
                                        .then(Commands.literal("lock").executes((ctx) -> {
                                            return lock(EntityArgument.getPlayer(ctx, "player"), ResourceLocationArgument.getResourceLocation(ctx, "book"),
                                                    ResourceLocationArgument.getResourceLocation(ctx, "entry"),
                                                    IntegerArgumentType.getInteger(ctx, "id"), ctx, true);
                                        }))
                                        .then(Commands.literal("un_lock").executes((ctx) -> {
                                            return lock(EntityArgument.getPlayer(ctx, "player"), ResourceLocationArgument.getResourceLocation(ctx, "book"),
                                                    ResourceLocationArgument.getResourceLocation(ctx, "entry"),
                                                    IntegerArgumentType.getInteger(ctx, "id"), ctx, false);
                                        }))
                                        .then(Commands.literal("hide").executes((ctx) -> {
                                            return hide(EntityArgument.getPlayer(ctx, "player"), ResourceLocationArgument.getResourceLocation(ctx, "book"),
                                                    ResourceLocationArgument.getResourceLocation(ctx, "entry"),
                                                    IntegerArgumentType.getInteger(ctx, "id"), ctx, true);
                                        }))
                                        .then(Commands.literal("un_hide").executes((ctx) -> {
                                            return hide(EntityArgument.getPlayer(ctx, "player"), ResourceLocationArgument.getResourceLocation(ctx, "book"),
                                                    ResourceLocationArgument.getResourceLocation(ctx, "entry"),
                                                    IntegerArgumentType.getInteger(ctx, "id"), ctx, false);
                                        }))
                                        .then(Commands.literal("complete").executes((ctx) -> {
                                            return complete(EntityArgument.getPlayer(ctx, "player"), ResourceLocationArgument.getResourceLocation(ctx, "book"),
                                                    ResourceLocationArgument.getResourceLocation(ctx, "entry"),
                                                    IntegerArgumentType.getInteger(ctx, "id"), ctx, true);
                                        }))
                                        .then(Commands.literal("reset").executes((ctx) -> {
                                            return reset(EntityArgument.getPlayer(ctx, "player"), ResourceLocationArgument.getResourceLocation(ctx, "book"),
                                                    ResourceLocationArgument.getResourceLocation(ctx, "entry"),
                                                    IntegerArgumentType.getInteger(ctx, "id"), ctx, false);
                                        }))

                                )))));
    }

    public static int lock(PlayerEntity player, ResourceLocation book, ResourceLocation entry, int id, CommandContext<CommandSource> source, boolean lock) throws CommandSyntaxException {
        try {
            if (player instanceof ServerPlayerEntity) {
                BookPage page1 = ItemModBook.getBook(ItemModBook.forBook(book)).contents.entries.get(entry).getPages().get(id);
                if (page1 instanceof PageBaseQuest) {
                    if (((PageBaseQuest) page1).getLock(player) != lock) {
                        ((PageBaseQuest) page1).setLock(player,lock);
                        new S2CBasePagePacket(book, entry, id, ((PageBaseQuest) page1).getStats(player),((PageBaseQuest) page1).getHide(player), ((PageBaseQuest) page1).getLock(player)).send((ServerPlayerEntity) player, TeamWorldSavedData.getTeamPlayers((ServerPlayerEntity) player));;
                    }
                }
            }
        } catch (Exception e) {
            player.sendMessage(new TranslationTextComponent("patchouliquests.command.failed"));
        }
        return 0;
    }

    public static int hide(PlayerEntity player, ResourceLocation book, ResourceLocation entry, int id, CommandContext<CommandSource> source, boolean hide) throws CommandSyntaxException {
        try {
            if (player instanceof ServerPlayerEntity) {
                BookPage page1 =  BookNBTHelper.getPage(ItemModBook.getBook(ItemModBook.forBook(book)).contents.entries.get(entry).getPages(),id);
                if (page1 instanceof PageBaseQuest) {
                    if (((PageBaseQuest) page1).getHide(player) != hide) {
                        ((PageBaseQuest) page1).setHide(player, hide);
                        new S2CBasePagePacket(book, entry, id, ((PageBaseQuest) page1).getStats(player),((PageBaseQuest) page1).getHide(player), ((PageBaseQuest) page1).getLock(player)).send((ServerPlayerEntity) player, TeamWorldSavedData.getTeamPlayers((ServerPlayerEntity) player));;
                    }
                }
            }
        } catch (Exception e) {
            player.sendMessage(new TranslationTextComponent("patchouliquests.command.failed").setStyle(new Style().setColor(TextFormatting.RED)));
        }
        return 0;
    }

    public static int complete(PlayerEntity player, ResourceLocation book, ResourceLocation entry, int id, CommandContext<CommandSource> source, boolean complete) throws CommandSyntaxException {
        try {
            if (player instanceof ServerPlayerEntity) {
                PageBaseQuest page1 =  BookNBTHelper.getPage(ItemModBook.getBook(ItemModBook.forBook(book)).contents.entries.get(entry).getPages(),id);
                System.out.println(page1);
                if (page1 != null) {
                        (page1).setStats(player,1);
                        new S2CBasePagePacket(book, entry, id, ((PageBaseQuest) page1).getStats(player),((PageBaseQuest) page1).getHide(player), ((PageBaseQuest) page1).getLock(player)).send((ServerPlayerEntity) player, TeamWorldSavedData.getTeamPlayers((ServerPlayerEntity) player));;
                    }
            }
        } catch (Exception e) {
            player.sendMessage(new TranslationTextComponent("patchouliquests.command.failed").setStyle(new Style().setColor(TextFormatting.RED)));
        }
        return 0;
    }

    public static int reset(PlayerEntity player, ResourceLocation book, ResourceLocation entry, int id, CommandContext<CommandSource> source, boolean complete) throws CommandSyntaxException {
        try {
            if (player instanceof ServerPlayerEntity) {
                PageBaseQuest page1 =  BookNBTHelper.getPage(ItemModBook.getBook(ItemModBook.forBook(book)).contents.entries.get(entry).getPages(),id);

                if (page1 != null) {
                        ((PageBaseQuest) page1).setStats(player, -1);
                        ((PageBaseQuest) page1).setHide(player,false);
                        ((PageBaseQuest) page1).setLock(player,false);
                        new S2CBasePagePacket(book, entry, id, ((PageBaseQuest) page1).getStats(player),((PageBaseQuest) page1).getHide(player), ((PageBaseQuest) page1).getLock(player)).send((ServerPlayerEntity) player, TeamWorldSavedData.getTeamPlayers((ServerPlayerEntity) player));;
                }
            }
        } catch (Exception e) {
            player.sendMessage(new TranslationTextComponent("patchouliquests.command.failed").setStyle(new Style().setColor(TextFormatting.RED)));
        }
        return 0;
    }
}