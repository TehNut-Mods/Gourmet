package info.tehnut.gourmet.core.util;

import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourcePackContainer;
import net.minecraft.resource.ResourcePackCreator;

import java.io.File;
import java.util.Map;

public class GourmetResourcePackCreator implements ResourcePackCreator {

    private final File directory;

    public GourmetResourcePackCreator(File directory) {
        this.directory = directory;
    }

    @Override
    public <T extends ResourcePackContainer> void registerContainer(Map<String, T> packs, ResourcePackContainer.Factory<T> factory) {
        if (!directory.exists())
            directory.mkdirs();

        String name = "Gourmet: Config";
        T container = ResourcePackContainer.of("file/" + name, false, () -> new NamedDirectoryResourcePack(directory, name), factory, ResourcePackContainer.SortingDirection.TOP);
        if (container != null)
            packs.put(name, container);
    }

    public static class NamedDirectoryResourcePack extends DirectoryResourcePack {

        private final String name;

        public NamedDirectoryResourcePack(File directory, String name) {
            super(directory);

            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
