package tehnut.gourmet.core.util;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RegistryGetter<T extends IForgeRegistryEntry<T>> {

    private final ResourceLocation registryName;
    private final IForgeRegistry<T> registry;
    private T value;

    public RegistryGetter(ResourceLocation registryName, IForgeRegistry<T> registry) {
        this.registryName = registryName;
        this.registry = registry;
    }

    public T getValue() {
        return value == null ? value = registry.getValue(registryName) : value;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public IForgeRegistry<T> getRegistry() {
        return registry;
    }

    @Override
    public String toString() {
        return registry.getRegistrySuperType().getSimpleName() + "@" + registryName;
    }
}
