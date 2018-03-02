package tehnut.gourmet.core.util;

import tehnut.gourmet.core.data.Harvest;

import javax.annotation.Nonnull;

public interface IHarvestContainer {

    @Nonnull
    Harvest getHarvest();
}
