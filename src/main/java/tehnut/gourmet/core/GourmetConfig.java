package tehnut.gourmet.core;

import net.minecraftforge.common.config.Config;
import tehnut.gourmet.Gourmet;

@Config(modid = Gourmet.MODID, name = Gourmet.MODID + "/" + Gourmet.MODID, category = "")
public class GourmetConfig {

    @Config.Comment("Toggles for various different logging methods. Useful for debugging. Not useful for, well, anything else really.")
    public static ConfigLogging logging = new ConfigLogging();

    public static class ConfigLogging {
        @Config.Comment({ "Enables the verbose logging which includes things like timings." })
        public boolean enableDebugLogging = false;
        @Config.Comment({ "Outputs all information about the currently loaded foods." })
        public boolean enableFoodLoaderLogging = false;
    }
}
