package tehnut.gourmet.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tehnut.gourmet.Gourmet;

public class ItemMundane extends Item {

    public ItemMundane(String name) {
        setTranslationKey(Gourmet.MODID + "." + name);
        setCreativeTab(Gourmet.TAB_GOURMET);
    }

    public static class CraftingReturned extends ItemMundane {

        public CraftingReturned(String name) {
            super(name);

            setMaxStackSize(1);
        }

        @Override
        public ItemStack getContainerItem(ItemStack itemStack) {
            return new ItemStack(this);
        }
    }
}
