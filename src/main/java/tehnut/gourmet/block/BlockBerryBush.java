package tehnut.gourmet.block;

import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.ItemHandlerHelper;
import tehnut.gourmet.Gourmet;
import tehnut.gourmet.core.data.Harvest;
import tehnut.gourmet.core.util.IHarvestContainer;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockBerryBush extends BlockBush implements IHarvestContainer {

    public static final IProperty<Integer> AGE = PropertyInteger.create("age", 0, 4);
    private static final AxisAlignedBB AGE_0_AABB = new AxisAlignedBB(0.3125D, 0, 0.3125D, 0.6875D, 0.375D, 0.6875D);
    private static final AxisAlignedBB AGE_1_AABB = new AxisAlignedBB(0.25D, 0, 0.25D, 0.75D, 0.5D, 0.75D);
    private static final AxisAlignedBB AGE_2_AABB = new AxisAlignedBB(0.125D, 0, 0.125D, 0.875D, 0.8125D, 0.875D);

    private final Harvest harvest;
    private Item berryItem;

    public BlockBerryBush(Harvest harvest) {
        this.harvest = harvest;

        setCreativeTab(Gourmet.TAB_GOURMET);
        setUnlocalizedName(Gourmet.MODID + ".bush_" + harvest.getSimpleName());
        setDefaultState(getBlockState().getBaseState().withProperty(AGE, 0));
        setSoundType(SoundType.PLANT);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (isTasty(state) && !world.isRemote) {
            if (berryItem == null)
                berryItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(Gourmet.MODID, "food_" + harvest.getSimpleName()));

            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(berryItem, Math.max(1, world.rand.nextInt(4))));
            world.setBlockState(pos, state.withProperty(AGE, 3));
            return true;
        }

        return isTasty(state) || super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(this);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(world, pos, state, rand);

        if (!harvest.getBushGrowth().checkLight(world.getLightFromNeighbors(pos.up())))
            return;

        if (!isMature(state)) {
            if(rand.nextFloat() >= 0.85)
                world.setBlockState(pos, state.cycleProperty(AGE));
        } else if (!isTasty(state) && rand.nextFloat() >= 0.95F) {
            world.setBlockState(pos, state.withProperty(AGE, 4));
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(AGE)) {
            case 0: return AGE_0_AABB;
            case 1: return AGE_1_AABB;
            case 2: return AGE_2_AABB;
            case 3: return FULL_BLOCK_AABB;
            case 4: return FULL_BLOCK_AABB;
        }
        return super.getBoundingBox(state, source, pos);
    }

    @Override
    public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
        IBlockState down = world.getBlockState(pos.down());
        return down.getBlock() instanceof BlockGrass || (down.getBlock() instanceof BlockBerryBush && isMature(down));
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        IBlockState down = world.getBlockState(pos.down());
        return down.getBlock() instanceof BlockGrass || (down.getBlock() instanceof BlockBerryBush && isMature(down));
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return blockState.getBoundingBox(worldIn, pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(AGE, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(AGE);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(AGE).build();
    }

    public boolean isMature(IBlockState state) {
        return state.getValue(AGE) >= 3;
    }

    public boolean isTasty(IBlockState state) {
        return state.getValue(AGE) == 4;
    }

    @Override
    public Harvest getHarvest() {
        return harvest;
    }
}
