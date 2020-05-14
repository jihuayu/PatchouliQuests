package jihuayu.patchoulitask.compat;
import jihuayu.patchoulitask.ModMain;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.util.ResourceLocation;

/**
 * @author LatvianModder
 */
@JeiPlugin
public class JEIIntegration implements IModPlugin{
    private static final ResourceLocation UID = new ResourceLocation(ModMain.MOD_ID, "jei");
    public static IJeiRuntime runtime;

    @Override
    public void onRuntimeAvailable(IJeiRuntime r)
    {
        runtime = r;
    }

    @Override
    public ResourceLocation getPluginUid()
    {
        return UID;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration r)
    {
    }

    @Override
    public void registerRecipes(IRecipeRegistration r)
    {
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration r)
    {
    }
}
