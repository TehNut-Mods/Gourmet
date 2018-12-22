package info.tehnut.gourmet.block;

import com.google.common.collect.Lists;
import net.fabricmc.fabric.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.sortme.ItemScatterer;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import info.tehnut.gourmet.core.RegistrarGourmet;
import info.tehnut.gourmet.core.data.Harvest;
import info.tehnut.gourmet.core.util.IHarvestContainer;
import net.minecraft.world.loot.context.LootContext;

import java.util.List;
import java.util.Random;

public class BlockBerryBush extends PlantBlock implements IHarvestContainer {

    public static final Property<Integer> AGE = IntegerProperty.create("age", 0, 4);
    private static final VoxelShape AGE_0_SHAPE = VoxelShapes.cube(new BoundingBox(0.3125D, 0, 0.3125D, 0.6875D, 0.375D, 0.6875D));
    private static final VoxelShape AGE_1_SHAPE = VoxelShapes.cube(new BoundingBox(0.25D, 0, 0.25D, 0.75D, 0.5D, 0.75D));
    private static final VoxelShape AGE_2_SHAPE = VoxelShapes.cube(new BoundingBox(0.125D, 0, 0.125D, 0.875D, 0.8125D, 0.875D));

    private final Harvest harvest;
    private Item berryItem;

    public BlockBerryBush(Harvest harvest) {
        super(FabricBlockSettings.copy(Blocks.OAK_LEAVES).build());
        this.harvest = harvest;
    }

    @Override
    public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
        if (isTasty(state) && !world.isClient) {
            ItemScatterer.spawn(world, player.x, player.y + 1, player.z, new ItemStack(this::getBerryItem, Math.max(1, world.random.nextInt(harvest.getBushGrowth().getMaxProduceDrop() + 1))));
            world.setBlockState(pos, state.with(AGE, 3));
            return true;
        }

        return isTasty(state) || super.activate(state, world, pos, player, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder lootContext) {
        List<ItemStack> drops = Lists.newArrayList(new ItemStack(this));
        if (isTasty(state))
            drops.add(new ItemStack(this::getBerryItem, Math.max(1, lootContext.getWorld().random.nextInt(harvest.getBushGrowth().getMaxProduceDrop() + 1))));
        drops.addAll(super.getDroppedStacks(state, lootContext));
        return drops;
    }

    @Override
    public void randomTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);

        int lightLevel = world.method_8624(pos.up(), 0);
        if (!harvest.getBushGrowth().checkLight(lightLevel))
            return;

        if (!isMature(state)) {
            if (random.nextFloat() >= 0.85)
                world.setBlockState(pos, state.method_11572(AGE)); // cycleProperty
        } else if (!isTasty(state) && random.nextFloat() >= 0.95F) {
            world.setBlockState(pos, state.with(AGE, 4));
        }
    }

    @Override
    public VoxelShape getBoundingShape(BlockState state, BlockView view, BlockPos pos) {
        switch (state.get(AGE)) {
            case 0: return AGE_0_SHAPE;
            case 1: return AGE_1_SHAPE;
            case 2: return AGE_2_SHAPE;
            case 3: return VoxelShapes.fullCube();
            case 4: return VoxelShapes.fullCube();
        }

        return super.getBoundingShape(state, view, pos);
    }

    @Override
    public boolean canPlaceAt(BlockState state, ViewableWorld world, BlockPos pos) {
        BlockState down = world.getBlockState(pos.down());
        return down.getBlock() instanceof GrassBlock || (down.getBlock() instanceof BlockBerryBush && isMature(down));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, VerticalEntityPosition entityPosition) {
        return state.getBoundingShape(view, pos);
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.with(AGE);
    }

    public boolean isMature(BlockState state) {
        return state.get(AGE) >= 3;
    }

    public boolean isTasty(BlockState state) {
        return state.get(AGE) == 4;
    }

    public Item getBerryItem() {
        return berryItem == null ? berryItem = RegistrarGourmet.getEdibles().get(harvest) : berryItem;
    }

    @Override
    public Harvest getHarvest() {
        return harvest;
    }
}
