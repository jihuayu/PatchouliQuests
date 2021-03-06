package jihuayu.patchoulitask.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import jihuayu.patchoulitask.ModMain;
import jihuayu.patchoulitask.old.task.BaseTaskPage;
import jihuayu.patchoulitask.old.task.CollectTaskPage;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

public class BookHelper {
    public static void complete(ServerPlayerEntity player, BaseTaskPage i) {
        if (!(i).finishCmd.isEmpty()) {
            for (String pp : (i).finishCmd) {
                String cmd = "";
                if (pp.startsWith("/")){
                    cmd = pp.substring(1);
                }
                if (cmd.startsWith("#")){
                    cmd = cmd.substring(1);
                }
                try {
                    ModMain.COMMANDS.execute(cmd, new CommandSource(player, new Vec3d(player.getX(), player.getY(), player.getZ())
                            , player.getPitchYaw(),
                            (ServerWorld) player.world, 99, "", new StringTextComponent(""), player.server, player));
                } catch (CommandSyntaxException e) {
                    ModMain.LOGGER.error(String.format("command %s excute failed!", ((CollectTaskPage) i).finishCmd));
                }
            }

        }
    }
}
