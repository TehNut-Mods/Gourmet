package info.tehnut.gourmet.core.mixin;


import info.tehnut.gourmet.Gourmet;
import info.tehnut.gourmet.core.util.GourmetResourcePackCreator;
import net.fabricmc.loader.FabricLoader;
import net.minecraft.resource.ResourcePackContainer;
import net.minecraft.resource.ResourcePackContainerManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
    @Shadow
    @Final
    private ResourcePackContainerManager<ResourcePackContainer> field_4595;

    @Inject(method = "method_3800",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourcePackContainerManager;addCreator(Lnet/minecraft/resource/ResourcePackCreator;)V", ordinal = 1)
    )
    public void method_3800(File file, LevelProperties properties, CallbackInfo info) {
        File dataDir = new File(FabricLoader.INSTANCE.getConfigDirectory(), Gourmet.MODID + "/resource/data/gourmet");
        if (!dataDir.exists())
            dataDir.mkdirs();

        this.field_4595.addCreator(new GourmetResourcePackCreator(dataDir.getParentFile().getParentFile()));
    }
}
