package info.tehnut.gourmet.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;
import info.tehnut.gourmet.core.RegistrarGourmet;
import info.tehnut.gourmet.core.data.Harvest;
import info.tehnut.gourmet.core.util.IHarvestContainer;

public class ItemSeed extends Item implements IHarvestContainer {

    private final Harvest harvest;
    private Block crop;

    public ItemSeed(Harvest harvest) {
        super(new Settings().itemGroup(ItemGroup.MISC));
        this.harvest = harvest;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState state = context.getWorld().getBlockState(context.getPos());
        ItemStack held = context.getItemStack();

        if (held.isEmpty())
            return ActionResult.PASS;

        if (context.getFacing() != Direction.UP)
            return ActionResult.PASS;

        if (context.getPlayer() != null && !context.getPlayer().canPlaceBlock(context.getPos(), context.getFacing(), held))
            return ActionResult.PASS;

        if (!(state.getBlock() instanceof FarmlandBlock)) // TODO generalize?
            return ActionResult.PASS;

        if (!context.getWorld().isAir(context.getPos().up()))
            return ActionResult.PASS;

        context.getWorld().setBlockState(context.getPos().up(), getCrop().getDefaultState());
        held.subtractAmount(1);
        return ActionResult.SUCCESS;
    }

    public Block getCrop() {
        return crop == null ? crop = RegistrarGourmet.getCrops().get(harvest) : crop;
    }

    @Override
    public Harvest getHarvest() {
        return harvest;
    }
}
