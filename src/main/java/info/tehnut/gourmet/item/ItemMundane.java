package info.tehnut.gourmet.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import info.tehnut.gourmet.Gourmet;

public class ItemMundane extends Item {

    public ItemMundane(Settings settings) {
        super(settings);
    }

    public ItemMundane() {
        this(new Settings().itemGroup(ItemGroup.MISC));
    }

    public static class CraftingReturned extends ItemMundane {

        public CraftingReturned(Settings settings) {
            super(settings);
        }

        public CraftingReturned() {
            // FIXME - Recipe remainder
            super(new Settings().itemGroup(ItemGroup.MISC).stackSize(1));
        }
    }
}
