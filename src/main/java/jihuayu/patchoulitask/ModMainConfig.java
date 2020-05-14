package jihuayu.patchoulitask;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EventBusSubscriber(bus = Bus.MOD)
public final class ModMainConfig {
    static final ForgeConfigSpec spec;

    public static List<ItemStack> items = new ArrayList<>();
    private static ForgeConfigSpec.ConfigValue<List<? extends String>> itemsCfg;

    static {
        final Pair<ModMainConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ModMainConfig::new);
        spec = specPair.getRight();
    }

    private ModMainConfig(ForgeConfigSpec.Builder builder) {
        itemsCfg = builder
                .comment("The items will give.","such as apple#1.Will give 1 apple to player.")
                .defineList("item", new ArrayList<>(), (i) -> true);
    }

    public static void refresh() {
        items.clear();

        List<? extends String> x = itemsCfg.get();
        String pattern = "^(.+?)(\\{.+})??(\\*[0-9]+)??$";
        Pattern r = Pattern.compile(pattern);
        for (String i : x) {
            Matcher m = r.matcher(i);
            if (m.find()) {
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(m.group(1)));
                if (item == null || item.equals(Items.AIR)) {
                    ModMain.LOGGER.error(String.format("Cloud not found %s", m.group(1)));
                    continue;
                }
                String nbt = m.group(2);
                String num = m.group(3);
                if (nbt == null) {
                    nbt = "{}";
                }
                if (num == null) {
                    num = "*1";
                }
                try {
                    CompoundNBT tags = JsonToNBT.getTagFromJson(nbt);
                    System.out.println(tags.toString());
                    int n = Integer.parseInt(num.substring(1));
                    ItemStack is = new ItemStack(item, n);
                    is.setTag(tags);
                    items.add(is);
                } catch (CommandSyntaxException | NumberFormatException e) {
                    ModMain.LOGGER.error(String.format("Cloud not parse nbt %s", m.group(0)));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onFileChange(ModConfig.Reloading event) {
        ((CommentedFileConfig) event.getConfig().getConfigData()).load();
        refresh();

    }
}