package info.tehnut.gourmet.core.data;


import net.minecraft.util.UseAction;

public enum ConsumeStyle {
    NONE(null),
    EAT(UseAction.EAT),
    DRINK(UseAction.DRINK),
//    INJECT, :thonk:
    ;

    private final UseAction action;

    ConsumeStyle(UseAction action) {
        this.action = action;
    }

    public UseAction getAction() {
        return action;
    }
}
