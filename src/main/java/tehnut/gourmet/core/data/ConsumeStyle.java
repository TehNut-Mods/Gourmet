package tehnut.gourmet.core.data;

import net.minecraft.item.EnumAction;

public enum ConsumeStyle {
    NONE(null),
    EAT(EnumAction.EAT),
    DRINK(EnumAction.DRINK),
//    INJECT, :thonk:
    ;

    private final EnumAction action;

    ConsumeStyle(EnumAction action) {
        this.action = action;
    }

    public EnumAction getAction() {
        return action;
    }
}
