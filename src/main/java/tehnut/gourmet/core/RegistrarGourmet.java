package tehnut.gourmet.core;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tehnut.gourmet.Gourmet;
import tehnut.gourmet.block.BlockBerryBush;
import tehnut.gourmet.block.BlockCrop;
import tehnut.gourmet.core.data.Harvest;
import tehnut.gourmet.core.util.GourmetLog;
import tehnut.gourmet.core.util.ResourceUtil;
import tehnut.gourmet.core.util.SmeltingLoader;
import tehnut.gourmet.item.ItemEdible;
import tehnut.gourmet.item.ItemMundane;
import tehnut.gourmet.item.ItemSeed;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
@Mod.EventBusSubscriber(modid = Gourmet.MODID)
@GameRegistry.ObjectHolder(Gourmet.MODID)
public class RegistrarGourmet {

    private static final List<Harvest> HARVEST_INFO = Lists.newArrayList();
    private static final Map<String, Harvest> HARVEST_BY_NAME = Maps.newHashMap();
    private static final Map<Harvest, BlockCrop> CROPS = Maps.newHashMap();
    private static final Map<Harvest, BlockBerryBush> BERRY_BUSHES = Maps.newHashMap();
    private static final Map<Harvest, ItemEdible> EDIBLES = Maps.newHashMap();
    private static final Map<Harvest, ItemSeed> SEEDS = Maps.newHashMap();

    public static final Item CUTTING_BOARD = Items.AIR;
    public static final Item SKILLET = Items.AIR;
    public static final Item EMPTY_JAR = Items.AIR;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        Stopwatch stopwatch = Stopwatch.createStarted();

        for (Harvest harvest : HARVEST_INFO) {
            switch (harvest.getGrowthType()) {
                case CROP: {
                    Block crop = new BlockCrop(harvest).setRegistryName("crop_" + harvest.getSimpleName());
                    CROPS.put(harvest, (BlockCrop) crop);
                    event.getRegistry().register(crop);
                    break;
                }
                case BUSH: {
                    Block bush = new BlockBerryBush(harvest).setRegistryName("bush_" + harvest.getSimpleName());
                    BERRY_BUSHES.put(harvest, (BlockBerryBush) bush);
                    event.getRegistry().register(bush);
                    break;
                }
                case TREE: {
                    // TODO - Trees
                }
            }
        }

        GourmetLog.DEBUG.info("Block registry completed in {}", stopwatch.stop());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        Stopwatch stopwatch = Stopwatch.createStarted();

        for (Harvest harvest : HARVEST_INFO) {
            Item edible = new ItemEdible(harvest).setRegistryName("food_" + harvest.getSimpleName());
            EDIBLES.put(harvest, (ItemEdible) edible);
            event.getRegistry().register(edible);

            switch (harvest.getGrowthType()) {
                case CROP: {
                    Item seed = new ItemSeed(harvest).setRegistryName("seed_" + harvest.getSimpleName());
                    SEEDS.put(harvest, (ItemSeed) seed);
                    event.getRegistry().register(seed);
                    break;
                }
                case BUSH: {
                    event.getRegistry().register(new ItemBlock(BERRY_BUSHES.get(harvest)).setRegistryName("bush_" + harvest.getSimpleName()));
                    break;
                }
                case TREE: {
                    // TODO - Trees
                }
            }
        }

        event.getRegistry().register(new ItemMundane.CraftingReturned("cutting_board").setRegistryName("cutting_board"));
        event.getRegistry().register(new ItemMundane.CraftingReturned("skillet").setRegistryName("skillet"));
        event.getRegistry().register(new ItemMundane("empty_jar").setRegistryName("empty_jar"));

        GourmetLog.DEBUG.info("Item registry completed in {}", stopwatch.stop());
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        GourmetCallbackHandler.handlePostCallback();

        List<SmeltingLoader.SmeltingRecipe> smeltingRecipes = Lists.newArrayList();
        SmeltingLoader.gatherRecipes(smeltingRecipes::add);
        for (SmeltingLoader.SmeltingRecipe recipe : smeltingRecipes)
            GameRegistry.addSmelting(recipe.getInput(), recipe.getOutput(), recipe.getExperience());

        GourmetLog.DEBUG.info("Recipe registry completed in {}", stopwatch.stop());
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        Stopwatch stopwatch = Stopwatch.createStarted();

        for (ItemFood food : EDIBLES.values())
            ModelLoader.setCustomModelResourceLocation(food, 0, new ModelResourceLocation(ResourceUtil.addContext(food.getRegistryName(), "food/"), "inventory"));

        for (ItemSeed seed : SEEDS.values())
            ModelLoader.setCustomModelResourceLocation(seed, 0, new ModelResourceLocation(ResourceUtil.addContext(seed.getRegistryName(), "seed/"), "inventory"));

        for (BlockBerryBush bush : BERRY_BUSHES.values())
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(bush), 0, new ModelResourceLocation(bush.getRegistryName(), "age=4"));

        ModelLoader.setCustomModelResourceLocation(CUTTING_BOARD, 0, new ModelResourceLocation(ResourceUtil.addContext(CUTTING_BOARD.getRegistryName(), "utensil/"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(SKILLET, 0, new ModelResourceLocation(ResourceUtil.addContext(SKILLET.getRegistryName(), "utensil/"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(EMPTY_JAR, 0, new ModelResourceLocation(ResourceUtil.addContext(EMPTY_JAR.getRegistryName(), "utensil/"), "inventory"));

        GourmetLog.DEBUG.info("Model registry completed in {}", stopwatch.stop());
    }

    public static void addHarvest(Harvest harvest) {
        HARVEST_INFO.add(harvest);
        HARVEST_BY_NAME.put(harvest.getSimpleName(), harvest);
    }

    @Nullable
    public static Harvest getByName(String name) {
        return HARVEST_BY_NAME.get(name);
    }

    public static List<Harvest> getHarvests() {
        return HARVEST_INFO;
    }

    public static Map<Harvest, BlockCrop> getCrops() {
        return CROPS;
    }

    public static Map<Harvest, BlockBerryBush> getBerryBushes() {
        return BERRY_BUSHES;
    }

    public static Map<Harvest, ItemEdible> getEdibles() {
        return EDIBLES;
    }

    public static Map<Harvest, ItemSeed> getSeeds() {
        return SEEDS;
    }
}
