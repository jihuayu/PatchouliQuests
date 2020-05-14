package jihuayu.patchoulitask.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class CheckUtil {

    public static boolean checkTask(List<Ingredient> i, List<ItemStack> stacks, boolean consume) {
        int ok_num = 0;
        List<Integer> num = new ArrayList<>();
        List<Integer> item_num = new ArrayList<>();
        for (int j = 0; j < i.size(); j++) {
            num.add(0);
            item_num.clear();
            for (int t = 0; t < stacks.size(); t++) {
                item_num.add(0);
                boolean ans = i.get(j).test(stacks.get(t));
                if (ans) {
                    num.set(j, num.get(j) + 1);
                    item_num.set(t, item_num.get(t) + 1);
                    if (num.get(j) < i.get(j).getMatchingStacks()[0].getCount()) {
                        if(item_num.get(t)<stacks.get(t).getCount()){
                            t--;
                        }
                    } else {
                        ok_num++;
                        break;
                    }
                }
            }
        }
        if (ok_num == i.size()){
            if(consume){
                for (Ingredient ingredient : i) {
                    for (ItemStack stack : stacks) {
                        boolean ans = ingredient.test(stack);
                        if (ans) {
                            if (stack.getCount() > 0) {
                                stack.setCount(stack.getCount() - 1);
                            }
                        }
                    }
                }
            }

            return true;
        }

        return false;
    }
}
