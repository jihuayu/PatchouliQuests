package jihuayu.patchoulitask.comand;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import jihuayu.patchoulitask.worldstorage.TeamWorldSavedData;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

public class TeamCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> builder1 = Commands.literal("patchouli_quests");
        builder1.then(Commands.literal("team")
                .then(Commands.literal("create")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes((ctx) -> {
                                    String ans = TeamWorldSavedData.createTeam(ctx.getSource().asPlayer(), ctx.getArgument("name", String.class));
                                    if (ans != null) {
                                        ctx.getSource().asPlayer().sendMessage(new StringTextComponent(ans).setStyle(new Style().setColor(TextFormatting.RED)));
                                    }
                                    return 0;
                                })))
                .then(Commands.literal("invite").then(Commands.argument("name", StringArgumentType.string())
                        .executes((ctx) -> {
                            String ans = TeamWorldSavedData.createTeam(ctx.getSource().asPlayer(), ctx.getArgument("name", String.class));
                            if (ans != null) {
                                ctx.getSource().asPlayer().sendMessage(new StringTextComponent(ans).setStyle(new Style().setColor(TextFormatting.RED)));
                            }
                            return 0;
                        }))));
        dispatcher.register(builder1);
    }
}
