package info.tehnut.gourmet;

import com.google.gson.reflect.TypeToken;
import info.tehnut.gourmet.core.GourmetConfig;
import info.tehnut.gourmet.core.util.JsonUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.FabricLoader;

import java.io.File;

public class Gourmet implements ModInitializer {

    public static final String MODID = "gourmet";
    public static final String NAME = "Gourmet";
    public static final String VERSION = "@VERSION@";
    public static final GourmetConfig CONFIG = JsonUtil.fromJson(TypeToken.get(GourmetConfig.class), new File(FabricLoader.INSTANCE.getConfigDirectory(), MODID + "/" + MODID + ".json"), new GourmetConfig());

    @Override
    public void onInitialize() {

    }
}
