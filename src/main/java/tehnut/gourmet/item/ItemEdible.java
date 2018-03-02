package tehnut.gourmet.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import tehnut.gourmet.Gourmet;
import tehnut.gourmet.core.data.ConsumeStyle;
import tehnut.gourmet.core.data.EatenEffect;
import tehnut.gourmet.core.data.Harvest;
import tehnut.gourmet.core.util.IHarvestContainer;
import tehnut.gourmet.core.util.StringUtil;

import javax.annotation.Nullable;
import java.util.List;

public class ItemEdible extends ItemFood implements IHarvestContainer {

    private final Harvest harvest;

    public ItemEdible(Harvest harvest) {
        super(0, 0, false);

        this.harvest = harvest;

        setUnlocalizedName(Gourmet.MODID + ".food_" + harvest.getSimpleName());
        setCreativeTab(Gourmet.TAB_GOURMET);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.canEat(harvest.isAlwaysEdible()) && harvest.getConsumptionStyle() != ConsumeStyle.NONE) {
            player.setActiveHand(hand);
            return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
        }
        return ActionResult.newResult(EnumActionResult.FAIL, stack);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase living) {
        stack.shrink(1);
        if (living instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) living;
            player.getFoodStats().addStats(this, stack);
            world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
            onFoodEaten(stack, world, player);
        }
        return stack;
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote)
            for (EatenEffect effect : harvest.getEffects())
                if (world.rand.nextDouble() <= effect.getChance())
                    player.addPotionEffect(new PotionEffect(effect.getPotion(), effect.getDuration(), effect.getAmplifier()));
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return harvest.getTimeToEat();
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return harvest.getConsumptionStyle().getAction();
    }

    @Override
    public int getHealAmount(ItemStack stack) {
        return harvest.getHungerProvided();
    }

    @Override
    public float getSaturationModifier(ItemStack stack) {
        return harvest.getSaturationModifier();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        for (EatenEffect effect : harvest.getEffects()) {
            String effectTooltip;
            PotionEffect potionEffect = new PotionEffect(effect.getPotion(), effect.getDuration(), effect.getAmplifier());

            String effectName = I18n.format(potionEffect.getEffectName());
            String potency = effect.getAmplifier() == 0 ? "" : StringUtil.toRoman(effect.getAmplifier() + 1);
            String duration = Potion.getPotionDurationString(potionEffect, 1.0F);

            if (effect.getChance() >= 1.0D)
                effectTooltip = I18n.format("tooltip.gourmet.effect", effectName + " " + potency, duration);
            else
                effectTooltip = I18n.format("tooltip.gourmet.effect.withchance", effectName + " " + potency, duration, StringUtil.doubleToPercent(effect.getChance()));

            tooltip.add(TextFormatting.AQUA + effectTooltip);
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public Harvest getHarvest() {
        return harvest;
    }
}
