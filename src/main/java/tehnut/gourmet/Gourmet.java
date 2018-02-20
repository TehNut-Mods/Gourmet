package tehnut.gourmet;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import tehnut.gourmet.core.RegistrarGourmet;
import tehnut.gourmet.core.RegistrarGourmetHarvests;
import tehnut.gourmet.proxy.IProxy;

@Mod(modid = Gourmet.MODID, name = Gourmet.NAME, version = Gourmet.VERSION)
public class Gourmet {

    public static final String MODID = "gourmet";
    public static final String NAME = "Gourmet";
    public static final String VERSION = "@VERSION@";
    public static final boolean DEV_MODE = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
    public static final CreativeTabs TAB_GOURMET = new CreativeTabs(MODID) {
        private Item display = Items.WHEAT;
        @Override
        public ItemStack getTabIconItem() {
            if (display == Items.WHEAT)
                display = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "food_sliced_bread"));
            return new ItemStack(display);
        }
    };

    @SidedProxy(clientSide = "tehnut.gourmet.proxy.ClientProxy", serverSide = "tehnut.gourmet.proxy.ServerProxy")
    public static IProxy PROXY;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        PROXY.preInit();

        RegistrarGourmetHarvests.gatherBuiltin(RegistrarGourmet.getHarvestInfo());
        RegistrarGourmetHarvests.gatherConfigured(RegistrarGourmet.getHarvestInfo());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        PROXY.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        PROXY.postInit();
    }
}
