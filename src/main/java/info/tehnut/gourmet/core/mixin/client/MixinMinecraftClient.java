package info.tehnut.gourmet.core.mixin.client;

import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import info.tehnut.gourmet.Gourmet;
import info.tehnut.gourmet.core.util.GourmetResourcePackCreator;
import net.fabricmc.loader.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackContainerManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Shadow
    @Final
    private ResourcePackContainerManager<ClientResourcePackContainer> resourcePackContainerManager;

    @Inject(method = {"reloadResources()V"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ReloadableResourceManager;reload(Ljava/util/List;)V", ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void reloadResources(CallbackInfo info, List<ResourcePack> list) {
        File configResources = new File(FabricLoader.INSTANCE.getConfigDirectory(), Gourmet.MODID + "/resource");
        if (!configResources.exists()) {
            configResources.mkdirs();

            // Print out the pack.mcmeta
            try (FileWriter writer = new FileWriter(new File(configResources, "pack.mcmeta"))) {
                JsonObject jsonObject = new JsonObject();
                JsonObject packProperty = new JsonObject();
                packProperty.addProperty("description", "Configured Gourmet Resources");
                packProperty.addProperty("pack_format", 3);
                jsonObject.add("pack", packProperty);

                JsonWriter jsonWriter = new JsonWriter(writer);
                jsonWriter.setIndent("  ");
                Streams.write(jsonObject, jsonWriter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        list.add(new GourmetResourcePackCreator.NamedDirectoryResourcePack(configResources, "Gourmet: Config"));
    }
}
