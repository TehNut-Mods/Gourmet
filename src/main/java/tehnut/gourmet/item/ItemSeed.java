package tehnut.gourmet.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import tehnut.gourmet.Gourmet;
import tehnut.gourmet.core.RegistrarGourmet;
import tehnut.gourmet.core.data.Harvest;
import tehnut.gourmet.core.util.IHarvestContainer;

public class ItemSeed extends Item implements IPlantable, IHarvestContainer {

    private final Harvest harvest;
    private Block crop;

    public ItemSeed(Harvest harvest) {
        this.harvest = harvest;

        setTranslationKey(Gourmet.MODID + ":seed_" + harvest.getSimpleName());
        setCreativeTab(Gourmet.TAB_GOURMET);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = world.getBlockState(pos);
        ItemStack held = player.getHeldItem(hand);

        if (held.isEmpty())
            return EnumActionResult.PASS;

        if (facing != EnumFacing.UP)
            return EnumActionResult.PASS;

        if (!player.canPlayerEdit(pos, facing, held))
            return EnumActionResult.PASS;

        if (!state.getBlock().canSustainPlant(state, world, pos, facing, this))
            return EnumActionResult.PASS;

        if (!world.isAirBlock(pos.up()))
            return EnumActionResult.PASS;

        world.setBlockState(pos.up(), getPlant(world, pos));
        held.shrink(1);
        return EnumActionResult.SUCCESS;
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return EnumPlantType.Crop;
    }

    @Override
    public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
        return getCrop().getDefaultState();
    }

    public Block getCrop() {
        return crop == null ? crop = RegistrarGourmet.getCrops().get(harvest) : crop;
    }

    @Override
    public Harvest getHarvest() {
        return harvest;
    }
}
