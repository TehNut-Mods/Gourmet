package tehnut.gourmet.core;

import com.google.gson.reflect.TypeToken;
import net.minecraft.init.MobEffects;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.io.filefilter.FileFilterUtils;
import tehnut.gourmet.Gourmet;
import tehnut.gourmet.core.data.*;
import tehnut.gourmet.core.util.loader.HarvestLoader;
import tehnut.gourmet.core.util.loader.IHarvestLoader;
import tehnut.gourmet.core.util.JsonUtil;

import java.io.File;
import java.io.FileFilter;

public class RegistrarGourmetHarvests {

    @HarvestLoader
    public static final IHarvestLoader BUILTIN = harvests -> {
        harvests.add(new Harvest.Builder("sliced_bread", 1, 0.2F).build());
        harvests.add(new Harvest.Builder("toast", 2, 0.8F).build());
        harvests.add(new Harvest.Builder("strawberry", 1, 0.1F).setGrowthType(GrowthType.BUSH).setBushGrowth(BushGrowth.DEFAULT).setAlwaysEdible().build());
        harvests.add(new Harvest.Builder("blueberry", 1, 0.1F).setGrowthType(GrowthType.BUSH).setBushGrowth(BushGrowth.DEFAULT).setAlwaysEdible().build());
        harvests.add(new Harvest.Builder("jam_strawberry", 5, 0.2F).setConsumptionStyle(ConsumeStyle.DRINK).addEffect(new EatenEffect(MobEffects.SPEED, 0, 100, 1.0D)).setAlwaysEdible().build());
        harvests.add(new Harvest.Builder("jam_blueberry", 5, 0.2F).setConsumptionStyle(ConsumeStyle.DRINK).addEffect(new EatenEffect(MobEffects.SPEED, 0, 100, 1.0D)).setAlwaysEdible().build());
    };

    @HarvestLoader
    public static final IHarvestLoader JSON_LOADER = harvests -> {
        File harvestDir = new File(Loader.instance().getConfigDir(), Gourmet.MODID + "/harvest");
        if (!harvestDir.exists()) {
            harvestDir.mkdirs();
            return;
        }

        File[] jsonFiles = harvestDir.listFiles((FileFilter) FileFilterUtils.suffixFileFilter(".json"));
        if (jsonFiles == null)
            return;

        for (File file : jsonFiles)
            harvests.add(JsonUtil.fromJson(TypeToken.get(Harvest.class), file));
    };
}
