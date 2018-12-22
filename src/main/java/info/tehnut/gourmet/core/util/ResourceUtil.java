package info.tehnut.gourmet.core.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.resource.FileResourcePackCreator;
import net.minecraft.resource.ResourcePackContainerManager;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ResourceUtil {

    public static Set<Path> gatherResources(String home, String following, Predicate<Path> predicate) {
        FileSystem fileSystem = null;
        try {
            URL url = ResourceUtil.class.getResource(home);
            if (url != null) {
                URI uri = url.toURI();
                Path path;
                if (uri.getScheme().equals("file")) {
                    path = Paths.get(ResourceUtil.class.getResource(home + "/" + following).toURI());
                } else {
                    if (!uri.getScheme().equals("jar")) {
                        GourmetLog.DEFAULT.error("Unsupported URI scheme {}", uri.getScheme());
                        return Collections.emptySet();
                    }

                    fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                    path = fileSystem.getPath(home + "/" + following);
                }

                return Files.walk(path).filter(predicate).collect(Collectors.toSet());
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fileSystem);
        }

        return Collections.emptySet();
    }

    public static Set<Path> gatherResources(String home, String following) {
        return gatherResources(home, following, p -> true);
    }

    public static Identifier addContext(Identifier rl, String context) {
        return new Identifier(rl.getNamespace(), context + rl.getPath());
    }

    @Environment(EnvType.CLIENT)
    public static void injectDirectoryAsResource(File resourceDir) {
        if (!resourceDir.exists() || !resourceDir.isDirectory())
            return;

        ResourcePackContainerManager<ClientResourcePackContainer> containers = MinecraftClient.getInstance().method_1520();
        containers.addCreator(new FileResourcePackCreator(resourceDir));

        System.out.println("doot");
    }
}
