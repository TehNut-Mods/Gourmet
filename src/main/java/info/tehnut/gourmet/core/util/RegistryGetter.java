package info.tehnut.gourmet.core.util;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegistryGetter<T> {

    private final Identifier registryName;
    private final Class<T> type;
    private final Registry<T> registry;
    private T value;

    public RegistryGetter(Identifier registryName, Class<T> type, Registry<T> registry) {
        this.registryName = registryName;
        this.type = type;
        this.registry = registry;
    }

    public T getValue() {
        return value == null ? value = registry.get(registryName) : value;
    }

    public Identifier getRegistryName() {
        return registryName;
    }

    public Registry<T> getRegistry() {
        return registry;
    }

    @Override
    public String toString() {
        return type.getSimpleName() + "@" + registryName;
    }
}
