package tehnut.gourmet.core;

import net.minecraft.init.MobEffects;
import tehnut.gourmet.core.data.ConsumeStyle;
import tehnut.gourmet.core.data.EatenEffect;
import tehnut.gourmet.core.data.GrowthType;
import tehnut.gourmet.core.data.Harvest;

import java.util.List;

public class RegistrarGourmetHarvests {

    public static void gatherBuiltin(List<Harvest> harvests) {
        harvests.add(new Harvest.Builder("sliced_bread", 1, 0.2F).build());
        harvests.add(new Harvest.Builder("toast", 2, 0.8F).build());
        harvests.add(new Harvest.Builder("strawberry", 1, 0.1F).setGrowthType(GrowthType.BUSH).setAlwaysEdible().build());
        harvests.add(new Harvest.Builder("blueberry", 1, 0.1F).setGrowthType(GrowthType.BUSH).setAlwaysEdible().build());
        harvests.add(new Harvest.Builder("jam_strawberry", 5, 0.2F).setConsumptionStyle(ConsumeStyle.DRINK).addEffect(new EatenEffect(MobEffects.SPEED, 0, 100, 1.0D)).setAlwaysEdible().build());
        harvests.add(new Harvest.Builder("jam_blueberry", 5, 0.2F).setConsumptionStyle(ConsumeStyle.DRINK).addEffect(new EatenEffect(MobEffects.SPEED, 0, 100, 1.0D)).setAlwaysEdible().build());
    }

    public static void gatherConfigured(List<Harvest> harvests) {
        // TODO load harvests from config/gourmet/harvest
    }
}
