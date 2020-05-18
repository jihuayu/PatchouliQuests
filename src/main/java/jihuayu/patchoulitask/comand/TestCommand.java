package jihuayu.patchoulitask.comand;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.nbt.CompoundNBT;

public class TestCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> builder1 = Commands.literal("test");
        builder1.then(Commands.literal("1").executes((i->{
                    CompoundNBT cmp = new CompoundNBT();
                    cmp.putString("a","a");
                    i.getSource().asPlayer().getPersistentData().put("a",cmp);
                    i.getSource().asPlayer().getPersistentData().put("b",cmp);
                    return 0;
                })));
        builder1.then(Commands.literal("2").executes((i->{
            i.getSource().asPlayer().getPersistentData().getCompound("a").putString("a","c");
            return 0;
        })));
        builder1.then(Commands.literal("3").executes((i->{
            System.out.println(i.getSource().asPlayer().getPersistentData().getCompound("a").getString("a"));
            System.out.println(i.getSource().asPlayer().getPersistentData().getCompound("b").getString("a"));
            return 0;
        })));
        dispatcher.register(builder1);

    }

}
