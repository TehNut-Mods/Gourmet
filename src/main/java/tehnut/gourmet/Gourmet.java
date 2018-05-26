package tehnut.gourmet;

import com.google.common.collect.Lists;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import tehnut.gourmet.core.GourmetCallbackHandler;
import tehnut.gourmet.core.RegistrarGourmet;
import tehnut.gourmet.core.util.GourmetLog;
import tehnut.gourmet.core.util.loader.HarvestLoader;
import tehnut.gourmet.core.util.loader.HarvestLoaderWrapper;
import tehnut.gourmet.proxy.IProxy;

import java.util.List;

@Mod(modid = Gourmet.MODID, name = Gourmet.NAME, version = Gourmet.VERSION)
public class Gourmet {

    public static final String MODID = "gourmet";
    public static final String NAME = "Gourmet";
    public static final String VERSION = "@VERSION@";
    public static final boolean DEV_MODE = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
    public static final List<HarvestLoaderWrapper> HARVEST_LOADERS = Lists.newArrayList();
    public static final CreativeTabs TAB_GOURMET = new CreativeTabs(MODID) {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(RegistrarGourmet.CUTTING_BOARD);
        }
    };

    @SidedProxy(clientSide = "tehnut.gourmet.proxy.ClientProxy", serverSide = "tehnut.gourmet.proxy.ServerProxy")
    public static IProxy PROXY;

    @Mod.EventHandler
    public void construction(FMLConstructionEvent event) {
        try {
            GourmetCallbackHandler.getBlockAddWrapper().wrapParent();
            GourmetCallbackHandler.getItemAddWrapper().wrapParent();
        } catch (Exception e) {
            GourmetLog.DEFAULT.warn("Failed to wrap AddCallback. Mods that replace our objects may not work properly.");
        }

        PROXY.construction();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        PROXY.preInit();

        HARVEST_LOADERS.addAll(HarvestLoader.Gather.gather(event.getAsmData()));
        for (HarvestLoaderWrapper loader : HARVEST_LOADERS) {
            GourmetLog.FOOD_LOADER.info("Loading harvests from {}", loader);
            loader.getLoader().gatherHarvests(RegistrarGourmet.getHarvestInfo()::add);
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        PROXY.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        PROXY.postInit();
    }

    @Mod.EventHandler
    public void registryRemap(FMLModIdMappingEvent event) {
        GourmetCallbackHandler.handlePostCallback();
    }
}
