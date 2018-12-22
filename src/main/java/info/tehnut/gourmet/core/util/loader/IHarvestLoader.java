package info.tehnut.gourmet.core.util.loader;

import info.tehnut.gourmet.core.data.Harvest;

import java.util.function.Consumer;

public interface IHarvestLoader {

    void gatherHarvests(Consumer<Harvest> harvests);
}
