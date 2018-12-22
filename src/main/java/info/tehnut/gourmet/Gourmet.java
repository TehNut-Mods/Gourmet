package info.tehnut.gourmet;

import com.google.gson.reflect.TypeToken;
import info.tehnut.gourmet.core.GourmetConfig;
import info.tehnut.gourmet.core.RegistrarGourmet;
import info.tehnut.gourmet.core.RegistrarGourmetHarvests;
import info.tehnut.gourmet.core.util.GourmetLog;
import info.tehnut.gourmet.core.util.JsonUtil;
import info.tehnut.gourmet.core.util.loader.HarvestLoader;
import info.tehnut.gourmet.core.util.loader.HarvestLoaderWrapper;
import info.tehnut.gourmet.core.util.loader.IHarvestLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.FabricLoader;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;

public class Gourmet implements ModInitializer {

    public static final String MODID = "gourmet";
    public static final String NAME = "Gourmet";
    public static final String VERSION = "@VERSION@";
    public static final GourmetConfig CONFIG = JsonUtil.fromJson(TypeToken.get(GourmetConfig.class), new File(FabricLoader.INSTANCE.getConfigDirectory(), MODID + "/" + MODID + ".json"), new GourmetConfig());

    @Override
    public void onInitialize() {
        // TODO Allow detection of other mods' loaders
        Arrays.stream(RegistrarGourmetHarvests.class.getFields())
                .filter(field -> {
                    if (!Modifier.isStatic(field.getModifiers()))
                        return false;

                    if (!field.isAnnotationPresent(HarvestLoader.class))
                        return false;

                    HarvestLoader annotation = field.getAnnotation(HarvestLoader.class);
                    return annotation.requiredMod().isEmpty() || FabricLoader.INSTANCE.isModLoaded(annotation.requiredMod());
                })
                .map(field -> {
                    try {
                        return new HarvestLoaderWrapper(field.getAnnotation(HarvestLoader.class).value(), (IHarvestLoader) field.get(null));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .forEach(loader -> {
                    GourmetLog.FOOD_LOADER.info("Loading harvests from {}", loader);
                    loader.getLoader().gatherHarvests(RegistrarGourmet::addHarvest);
                });

        RegistrarGourmet.registerBlocks();
        RegistrarGourmet.registerItems();
    }
}
