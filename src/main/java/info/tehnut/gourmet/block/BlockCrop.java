package info.tehnut.gourmet.block;

import com.google.common.collect.Lists;
import net.fabricmc.fabric.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import info.tehnut.gourmet.core.RegistrarGourmet;
import info.tehnut.gourmet.core.data.Harvest;
import info.tehnut.gourmet.core.util.IHarvestContainer;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.Parameters;

import java.util.List;
import java.util.Random;

public abstract class BlockCrop extends CropBlock implements IHarvestContainer {

    private final Harvest harvest;
    private Item produce;
    private Item seed;

    public BlockCrop(Harvest harvest) {
        super(FabricBlockSettings.copy(Blocks.WHEAT).build());

        this.harvest = harvest;
    }

    // Have to bypass BlockCrops randomTick() to handle light checks on our own
    @Override
    public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!harvest.getCropGrowth().checkLight(world.method_8624(pos.up(), 0)))
            return;

        int age = getCropAge(state);
        if (age >= getCropAgeMaximum())
            return;

        float growthChance = method_9830(this, world, pos); // getGrowthChance
        if (random.nextInt((int) (25.0F / growthChance) + 1) == 0) {
            world.setBlockState(pos, state.method_11572(getAgeProperty())); // cycleProperty
        }
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder lootContext) {
        List<ItemStack> drops = Lists.newArrayList();
        int age = getCropAge(state);
        ItemStack harvester = lootContext.get(Parameters.TOOL);
        int fortune = EnchantmentHelper.getLevel(Enchantments.FORTUNE, harvester);
        Random random = lootContext.getWorld().random;
        if (age >= getCropAgeMaximum()) {
            for (int i = 0; i < harvest.getCropGrowth().getMaxSeedDrop() + fortune; ++i)
                if (random.nextInt(2 * getCropAgeMaximum()) <= age)
                    drops.add(new ItemStack(getSeed()));

            for (int i = 0; i < harvest.getCropGrowth().getMaxProduceDrop() + fortune; ++i)
                if (random.nextInt(2 * getCropAgeMaximum()) <= age)
                    drops.add(new ItemStack(getCropItem()));
        } else {
            drops.add(new ItemStack(getSeed()));
        }

        return drops;
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.with(getAgeProperty());
    }

    @Override
    public int getCropAgeMaximum() {
        return getAgeProperty().getValues().size() - 1;
    }

    public ItemProvider getSeed() {
        return seed == null ? seed = RegistrarGourmet.getSeeds().get(harvest) : seed;
    }

    @Override
    protected ItemProvider getCropItem() {
        return produce == null ? produce = RegistrarGourmet.getEdibles().get(harvest) : produce;
    }

    @Override
    public boolean isFertilizable(BlockView view, BlockPos pos, BlockState state, boolean someBool) {
        return harvest.getCropGrowth().canFertilize() && !isValidState(state);
    }

    @Override
    public Harvest getHarvest() {
        return harvest;
    }

    @Override
    public abstract IntegerProperty getAgeProperty();
}
