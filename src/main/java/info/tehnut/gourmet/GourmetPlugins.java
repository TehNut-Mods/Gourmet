package info.tehnut.gourmet;

import com.google.common.collect.Lists;
import info.tehnut.gourmet.core.RegistrarGourmet;
import info.tehnut.gourmet.core.util.GourmetLog;
import info.tehnut.gourmet.core.util.loader.HarvestLoaderWrapper;
import info.tehnut.gourmet.core.util.loader.IHarvestLoader;
import info.tehnut.pluginloader.LoaderCreator;
import info.tehnut.pluginloader.PluginLoaderBuilder;
import info.tehnut.pluginloader.ValidationStrategy;
import net.fabricmc.loader.language.LanguageAdapter;

import java.util.List;

public class GourmetPlugins implements LoaderCreator {

    private static final List<HarvestLoaderWrapper> LOADERS = Lists.newArrayList();

    @Override
    public void createLoaders() {
        new PluginLoaderBuilder(Gourmet.MODID)
                .withValidator(ValidationStrategy.instanceOf(IHarvestLoader.class))
                .withInitializer((aClass, container) -> {
                    try {
                        IHarvestLoader loader = (IHarvestLoader) container.getOwner().getAdapter().createInstance(aClass, new LanguageAdapter.Options());
                        LOADERS.add(new HarvestLoaderWrapper(container.getInfo().getId(), loader));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .withPostCall(() -> {
                    LOADERS.forEach(loader -> {
                        GourmetLog.FOOD_LOADER.info("Loading harvests from {}", loader);
                        loader.getLoader().gatherHarvests(RegistrarGourmet::addHarvest);
                    });

                    RegistrarGourmet.registerBlocks();
                    RegistrarGourmet.registerItems();
                })
                .build();
    }
}
