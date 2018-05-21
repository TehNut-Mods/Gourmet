package tehnut.gourmet.core;

import net.minecraftforge.common.config.Config;
import tehnut.gourmet.Gourmet;

@Config(modid = Gourmet.MODID, name = Gourmet.MODID + "/" + Gourmet.MODID, category = "")
public class GourmetConfig {

    @Config.Comment({"Toggles for various different logging methods. Useful for debugging. Not useful for, well, anything else really."})
    public static ConfigLogging logging = new ConfigLogging();
    @Config.Comment({"Settings for anything related to a remote connection controlled by Gourmet."})
    public static ConfigRemote remote = new ConfigRemote();

    public static class ConfigLogging {
        @Config.Comment({"Enables the verbose logging which includes things like timings."})
        public boolean enableDebugLogging = false;
        @Config.Comment({"Outputs all information about the currently loaded foods."})
        public boolean enableFoodLoaderLogging = false;
    }

    public static class ConfigRemote {
        @Config.Comment({"The URL to a raw JSON file which a list of Harvests to load.", "This would usually be provided to you by a server.", "An empty string will make no remote queries."})
        public String remoteJson = "";
    }
}
