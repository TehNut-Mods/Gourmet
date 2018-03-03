package tehnut.gourmet.proxy;

import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tehnut.gourmet.Gourmet;
import tehnut.gourmet.core.util.ResourceUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@SideOnly(Side.CLIENT)
public class ClientProxy implements IProxy {

    @Override
    public void construction() {
        File configResources = new File(Loader.instance().getConfigDir(), Gourmet.MODID + "/resource");
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

        ResourceUtil.injectDirectoryAsResource(configResources);
    }
}
