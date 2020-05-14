package jihuayu.patchoulitask.util;

import jihuayu.patchoulitask.compat.JEIIntegration;
import mezz.jei.api.recipe.IFocus;
import net.minecraftforge.fml.ModList;

public class JEIUtil {
    public static void showRecipes(Object object)
    {
        if (ModList.get().isLoaded("jei"))
            JEIIntegration.runtime.getRecipesGui().show(JEIIntegration.runtime.getRecipeManager().createFocus(IFocus.Mode.OUTPUT, object));
    }
}
