package jihuayu.patchoulitask.util;

import jihuayu.patchoulitask.task.CollectTaskPage;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.List;

public class CheckUtil {

    public static boolean checkTask(List<Ingredient> i, List<ItemStack> stacks,boolean consume){
        int ok_num = 0;
        for (Ingredient j : ((CollectTaskPage) i).items) {
            for (ItemStack t : stacks) {
                boolean ans = j.test(t);
                if (ans) {
                    ok_num++;
                    break;
                }
            }

        }
        if (ok_num == ((CollectTaskPage) i).items.size()) {
            if (consume) {
                for (Ingredient j : ((CollectTaskPage) i).items) {
                    for (ItemStack t : stacks) {
                        boolean ans = j.test(t);
                        if (ans) {
                            t.setCount(t.getCount() - j.getMatchingStacks()[0].getCount());
                            break;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
}
