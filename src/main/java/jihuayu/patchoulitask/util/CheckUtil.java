package jihuayu.patchoulitask.util;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class CheckUtil {
    public static boolean checkTask(List<Ingredient> i, List<ItemStack> stacks, boolean consume,List<Integer> num) {
        int ok_num = 0;;
        List<Integer> copy = Lists.newCopyOnWriteArrayList(num);
        List<Integer> item_num = new ArrayList<>();
        for (int j = 0; j < i.size(); j++) {
            item_num.clear();
            System.out.println(num.get(j));
            for (int t = 0; t < stacks.size(); t++) {
                item_num.add(0);
                boolean ans = i.get(j).test(stacks.get(t));
                if (ans) {
                    System.out.println("-----------------------");
                    System.out.println(num.get(j) );
                    System.out.println("-----------------------");
                    if (num.get(j) < i.get(j).getMatchingStacks()[0].getCount()) {
                        if(item_num.get(t)<stacks.get(t).getCount()){
                            num.set(j, num.get(j) + 1);
                            item_num.set(t, item_num.get(t) + 1);
                            t--;
                        }
                    } else {
                        ok_num++;
                        break;
                    }
                }
            }
        }
        System.out.println(ok_num);
        System.out.println(i.size());
        if(consume){
            for (int j = 0; j < i.size(); j++) {
                item_num.clear();
                for (int t = 0; t < stacks.size(); t++) {
                    item_num.add(0);
                    boolean ans = i.get(j).test(stacks.get(t));
                    if (ans) {
                        if (copy.get(j) < i.get(j).getMatchingStacks()[0].getCount()) {
                            if(stacks.get(t).getCount()>0){
                                copy.set(j, copy.get(j) + 1);
                                stacks.get(t).setCount(stacks.get(t).getCount() - 1);
                                t--;
                            }
                        }
                    }
                }
            }

        }
        return ok_num == i.size();
    }
}
