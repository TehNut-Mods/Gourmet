package info.tehnut.gourmet.core;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import info.tehnut.gourmet.Gourmet;
import info.tehnut.gourmet.block.BlockBerryBush;
import info.tehnut.gourmet.block.BlockCrop;
import info.tehnut.gourmet.core.data.Harvest;
import info.tehnut.gourmet.core.util.GourmetLog;
import info.tehnut.gourmet.core.util.ResourceUtil;
import info.tehnut.gourmet.item.ItemEdible;
import info.tehnut.gourmet.item.ItemMundane;
import info.tehnut.gourmet.item.ItemSeed;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.block.BlockItem;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Map;

public class RegistrarGourmet {

    private static final List<Harvest> HARVEST_INFO = Lists.newArrayList();
    private static final Map<String, Harvest> HARVEST_BY_NAME = Maps.newHashMap();
    private static final Map<Harvest, BlockCrop> CROPS = Maps.newHashMap();
    private static final Map<Harvest, BlockBerryBush> BERRY_BUSHES = Maps.newHashMap();
    private static final Map<Harvest, ItemEdible> EDIBLES = Maps.newHashMap();
    private static final Map<Harvest, ItemSeed> SEEDS = Maps.newHashMap();
    private static final Map<Integer, IntegerProperty> AGE_PROP_CACHE = Maps.newHashMap();

    public static final Item CUTTING_BOARD = new ItemMundane.CraftingReturned();
    public static final Item SKILLET = new ItemMundane.CraftingReturned();
    public static final Item EMPTY_JAR = new ItemMundane();

    public static void registerBlocks() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Registry<Block> registry = Registry.BLOCK;

        for (Harvest harvest : HARVEST_INFO) {
            switch (harvest.getGrowthType()) {
                case CROP: {
                    BlockCrop crop = new BlockCrop(harvest) {
                        @Override
                        public IntegerProperty getAgeProperty() {
                            return AGE_PROP_CACHE.computeIfAbsent(harvest.getCropGrowth().getStages(), i -> IntegerProperty.create("age", 0, harvest.getCropGrowth().getStages()));
                        }
                    };
                    CROPS.put(harvest, crop);
                    Registry.register(registry, new Identifier(Gourmet.MODID, "crop_" + harvest.getSimpleName()), crop);
                    break;
                }
                case BUSH: {
                    BlockBerryBush bush = new BlockBerryBush(harvest);
                    BERRY_BUSHES.put(harvest, bush);
                    Registry.register(registry, new Identifier(Gourmet.MODID, "bush_" + harvest.getSimpleName()), bush);
                    break;
                }
                case TREE: {
                    // TODO - Trees
                }
            }
        }

        GourmetLog.DEBUG.info("Block registry completed in {}", stopwatch.stop());
    }

    public static void registerItems() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Registry<Item> registry = Registry.ITEM;

        for (Harvest harvest : HARVEST_INFO) {
            ItemEdible edible = new ItemEdible(harvest);
            EDIBLES.put(harvest, edible);
            Registry.register(registry, new Identifier(Gourmet.MODID, "food_" + harvest.getSimpleName()), edible);

            switch (harvest.getGrowthType()) {
                case CROP: {
                    ItemSeed seed = new ItemSeed(harvest);
                    SEEDS.put(harvest, seed);
                    Registry.register(registry, new Identifier(Gourmet.MODID, "seed_" + harvest.getSimpleName()), seed);
                    break;
                }
                case BUSH: {
                    BlockItem itemBlock = new BlockItem(BERRY_BUSHES.get(harvest), new Item.Settings().itemGroup(ItemGroup.FOOD));
                    Registry.register(registry, new Identifier(Gourmet.MODID, "bush_" + harvest.getSimpleName()), itemBlock);
                    itemBlock.registerBlockItemMap(Item.BLOCK_ITEM_MAP, itemBlock);
                    break;
                }
                case TREE: {
                    // TODO - Trees
                }
            }
        }

        Registry.register(registry, new Identifier(Gourmet.MODID, "cutting_board"), CUTTING_BOARD);
        Registry.register(registry, new Identifier(Gourmet.MODID, "skillet"), SKILLET);
        Registry.register(registry, new Identifier(Gourmet.MODID, "empty_jar"), EMPTY_JAR);

        GourmetLog.DEBUG.info("Item registry completed in {}", stopwatch.stop());
    }

    @Environment(EnvType.CLIENT)
    public static void registerModels(ItemModels models) {
        Stopwatch stopwatch = Stopwatch.createStarted();

        for (ItemEdible food : EDIBLES.values())
            models.putModel(food, new ModelIdentifier(ResourceUtil.addContext(getRegistryName(food), "food/"), "inventory"));

        for (ItemSeed seed : SEEDS.values())
            models.putModel(seed, new ModelIdentifier(ResourceUtil.addContext(getRegistryName(seed), "seed/"), "inventory"));

        for (BlockBerryBush bush : BERRY_BUSHES.values())
            models.putModel(bush.getItem(), new ModelIdentifier(getRegistryName(bush), "age=4"));

        models.putModel(CUTTING_BOARD, new ModelIdentifier(ResourceUtil.addContext(getRegistryName(CUTTING_BOARD), "utensil/"), "inventory"));
        models.putModel(SKILLET, new ModelIdentifier(ResourceUtil.addContext(getRegistryName(SKILLET), "utensil/"), "inventory"));
        models.putModel(EMPTY_JAR, new ModelIdentifier(ResourceUtil.addContext(getRegistryName(EMPTY_JAR), "utensil/"), "inventory"));

        GourmetLog.DEBUG.info("Model registry completed in {}", stopwatch.stop());
    }

    public static void addHarvest(Harvest harvest) {
        HARVEST_INFO.add(harvest);
        HARVEST_BY_NAME.put(harvest.getSimpleName(), harvest);
    }

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

    private static Identifier getRegistryName(Object object) {
        if (object instanceof Item)
            return Registry.ITEM.getId((Item) object);
        else if (object instanceof Block)
            return Registry.BLOCK.getId((Block) object);

        return new Identifier("air");
    }
}
