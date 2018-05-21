package tehnut.gourmet.core;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.registries.IForgeRegistryEntry;
import tehnut.gourmet.Gourmet;
import tehnut.gourmet.block.BlockBerryBush;
import tehnut.gourmet.block.BlockCrop;
import tehnut.gourmet.core.data.Harvest;
import tehnut.gourmet.core.util.GourmetLog;
import tehnut.gourmet.core.util.IHarvestContainer;
import tehnut.gourmet.core.util.RegistryAddCallbackWrapper;
import tehnut.gourmet.item.ItemEdible;
import tehnut.gourmet.item.ItemSeed;

import java.util.Map;

public class GourmetCallbackHandler {

    private static final Map<Harvest, Block> HARVEST_TO_NEW_BLOCK = Maps.newHashMap();
    private static final Map<Harvest, Item> HARVEST_TO_NEW_ITEM = Maps.newHashMap();

    private static final RegistryAddCallbackWrapper<Block> BLOCK_ADD_WRAPPER = new RegistryAddCallbackWrapper<>(Block.class, (n, o) -> {
        ActionResult<Harvest> success = isHarvestOverride(n, o);
        if (success.getType() != EnumActionResult.SUCCESS)
            return;

        HARVEST_TO_NEW_BLOCK.put(success.getResult(), n);
    }, () -> {
        GourmetLog.DEBUG.info("Handling {} block replacements", HARVEST_TO_NEW_BLOCK.size());
        for (Map.Entry<Harvest, Block> entry : HARVEST_TO_NEW_BLOCK.entrySet()) {
            Block block = entry.getValue();
            if (block instanceof BlockCrop)
                RegistrarGourmet.getCrops().put(entry.getKey(), (BlockCrop) block);
            else if (block instanceof BlockBerryBush)
                RegistrarGourmet.getBerryBushes().put(entry.getKey(), (BlockBerryBush) block);
        }

        HARVEST_TO_NEW_BLOCK.clear();
    });

    private static final RegistryAddCallbackWrapper<Item> ITEM_ADD_WRAPPER = new RegistryAddCallbackWrapper<>(Item.class, (n, o) -> {
        ActionResult<Harvest> success = isHarvestOverride(n, o);
        if (success.getType() != EnumActionResult.SUCCESS)
            return;

        HARVEST_TO_NEW_ITEM.put(success.getResult(), n);
    }, () -> {
        GourmetLog.DEBUG.info("Handling {} item replacements", HARVEST_TO_NEW_ITEM.size());
        for (Map.Entry<Harvest, Item> entry : HARVEST_TO_NEW_ITEM.entrySet()) {
            Item item = entry.getValue();
            if (item instanceof ItemEdible)
                RegistrarGourmet.getEdibles().put(entry.getKey(), (ItemEdible) item);
            else if (item instanceof ItemSeed)
                RegistrarGourmet.getSeeds().put(entry.getKey(), (ItemSeed) item);
        }

        HARVEST_TO_NEW_ITEM.clear();
    });

    public static void handlePostCallback() {
        BLOCK_ADD_WRAPPER.getPostCallback().run();
        ITEM_ADD_WRAPPER.getPostCallback().run();
    }

    public static <T extends IForgeRegistryEntry<T>> ActionResult<Harvest> isHarvestOverride(T newObject, T oldObject) {
        if (oldObject == null)
            return ActionResult.newResult(EnumActionResult.FAIL, null); // not overriding shit

        if (!newObject.getRegistryName().getResourceDomain().equals(Gourmet.MODID))
            return ActionResult.newResult(EnumActionResult.FAIL, null); // our shit is safe

        if (!newObject.getRegistryName().equals(oldObject.getRegistryName()))
            return ActionResult.newResult(EnumActionResult.FAIL, null); // not overriding shit

        // Make sure the replacement and the old one are both harvesty thingies
        if (newObject instanceof IHarvestContainer && oldObject instanceof IHarvestContainer) {
            IHarvestContainer newOne = (IHarvestContainer) newObject;
            IHarvestContainer oldOne = (IHarvestContainer) oldObject;

            // please don't
            if (!newOne.getHarvest().equals(oldOne.getHarvest()))
                throw new RuntimeException("Attempted to override a Harvest based item with a different Harvest type.");

            // Map the Harvest to it's new value
            GourmetLog.DEBUG.info("Adding override for {} ({})", newOne.getHarvest().getSimpleName(), newObject.getRegistryName());
            return ActionResult.newResult(EnumActionResult.SUCCESS, newOne.getHarvest());
        }

        return ActionResult.newResult(EnumActionResult.FAIL, null);
    }

    public static RegistryAddCallbackWrapper<Block> getBlockAddWrapper() {
        return BLOCK_ADD_WRAPPER;
    }

    public static RegistryAddCallbackWrapper<Item> getItemAddWrapper() {
        return ITEM_ADD_WRAPPER;
    }
}
