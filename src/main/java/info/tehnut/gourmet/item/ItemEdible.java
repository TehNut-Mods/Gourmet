package info.tehnut.gourmet.item;

import net.minecraft.client.item.TooltipOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.*;
import net.minecraft.world.World;
import info.tehnut.gourmet.core.data.ConsumeStyle;
import info.tehnut.gourmet.core.data.EatenEffect;
import info.tehnut.gourmet.core.data.Harvest;
import info.tehnut.gourmet.core.util.IHarvestContainer;
import info.tehnut.gourmet.core.util.StringUtil;

import java.util.List;

public class ItemEdible extends FoodItem implements IHarvestContainer {

    private final Harvest harvest;

    public ItemEdible(Harvest harvest) {
        super(0, 0, false, new Settings().itemGroup(ItemGroup.FOOD));

        this.harvest = harvest;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (player.canConsume(harvest.isAlwaysEdible()) && harvest.getConsumptionStyle() != ConsumeStyle.NONE) {
            player.setCurrentHand(hand);
            return new TypedActionResult<>(ActionResult.SUCCESS, stack);
        }
        return new TypedActionResult<>(ActionResult.FAILURE, stack);
    }

    @Override
    protected void onConsumed(ItemStack stack, World world, PlayerEntity player) {
        if (!world.isClient)
            for (EatenEffect effect : harvest.getEffects())
                if (effect.getStatusEffect() != null && world.random.nextDouble() <= effect.getChance())
                    player.addPotionEffect(new StatusEffectInstance(effect.getStatusEffect(), effect.getDuration(), effect.getAmplifier()));
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return harvest.getTimeToEat();
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return harvest.getConsumptionStyle().getAction();
    }

    @Override
    public int getHungerRestored(ItemStack stack) {
        return harvest.getHungerProvided();
    }

    @Override
    public float getSaturationModifier(ItemStack stack) {
        return harvest.getSaturationModifier();
    }

    @Override
    public void buildTooltip(ItemStack stack, World world, List<TextComponent> tooltip, TooltipOptions options) {
        for (EatenEffect effect : harvest.getEffects()) {
            TextComponent effectTooltip;
            if (effect.getStatusEffect() == null)
                continue;

            StatusEffectInstance statusEffect = new StatusEffectInstance(effect.getStatusEffect(), effect.getDuration(), effect.getAmplifier());

            String effectName = I18n.translate(statusEffect.getTranslationKey());
            String potency = effect.getAmplifier() == 0 ? "" : StringUtil.toRoman(effect.getAmplifier() + 1);
            String duration = StatusEffectUtil.durationToString(statusEffect, 1.0F);

            if (effect.getChance() >= 1.0D)
                effectTooltip = new TranslatableTextComponent("tooltip.gourmet.effect", effectName + " " + potency, duration);
            else
                effectTooltip = new TranslatableTextComponent("tooltip.gourmet.effect_with_chance", effectName + " " + potency, duration, StringUtil.doubleToPercent(effect.getChance()));

            tooltip.add(effectTooltip.setStyle(new Style().setColor(TextFormat.AQUA)));
        }

        super.buildTooltip(stack, world, tooltip, options);
    }

    @Override
    public Harvest getHarvest() {
        return harvest;
    }
}
