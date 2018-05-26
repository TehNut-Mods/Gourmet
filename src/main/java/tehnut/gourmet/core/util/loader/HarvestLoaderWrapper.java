package tehnut.gourmet.core.util.loader;

public class HarvestLoaderWrapper {

    private final String name;
    private final IHarvestLoader loader;

    public HarvestLoaderWrapper(String name, IHarvestLoader loader) {
        this.name = name;
        this.loader = loader;
    }

    public String getName() {
        return name;
    }

    public IHarvestLoader getLoader() {
        return loader;
    }

    @Override
    public String toString() {
        return name + "@" + loader.getClass().getCanonicalName();
    }
}
