package info.tehnut.gourmet.core;

import com.google.gson.reflect.TypeToken;
import info.tehnut.gourmet.Gourmet;
import info.tehnut.gourmet.core.data.*;
import info.tehnut.gourmet.core.util.DumbassHarvestXMLParser;
import info.tehnut.gourmet.core.util.GourmetLog;
import info.tehnut.gourmet.core.util.JsonUtil;
import info.tehnut.gourmet.core.util.loader.IHarvestLoader;
import joptsimple.internal.Strings;
import net.fabricmc.loader.FabricLoader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileFilter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

public class RegistrarGourmetHarvests {

    public static class HarvestLoaderInternal implements IHarvestLoader {
        @Override
        public void gatherHarvests(Consumer<Harvest> harvests) {

        }
    }

    public static class HarvestLoaderJson implements IHarvestLoader {
        @Override
        public void gatherHarvests(Consumer<Harvest> harvests) {
            parseFiles(".json", f -> harvests.accept(JsonUtil.fromJson(TypeToken.get(Harvest.class), f)));
        }
    }

    public static class HarvestLoaderXml implements IHarvestLoader {
        @Override
        public void gatherHarvests(Consumer<Harvest> harvests) {
            parseFiles(".xml", f -> {
                try {
                    SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
                    parser.parse(f, new DumbassHarvestXMLParser(harvests));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static class HarvestLoaderRemote implements IHarvestLoader {
        @Override
        public void gatherHarvests(Consumer<Harvest> harvests) {
            if (Strings.isNullOrEmpty(Gourmet.CONFIG.getRemote().getRemoteJson()))
                return;

            GourmetLog.DEFAULT.info("Attempting remote connection to {}", Gourmet.CONFIG.getRemote().getRemoteJson());
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(Gourmet.CONFIG.getRemote().getRemoteJson()).openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("user-agent", "Gourmet Minecraft Mod");
                String response = IOUtils.toString(urlConnection.getInputStream(), StandardCharsets.UTF_8);
                JsonUtil.fromJson(new TypeToken<List<Harvest>>(){}, response).forEach(harvests);
            } catch (Exception e) {
                GourmetLog.FOOD_LOADER.error("Error loading remote harvests: {}", e.getMessage());
            }
        }
    }

    private static void parseFiles(String suffix, Consumer<File> handler) {
        File harvestDir = new File(FabricLoader.INSTANCE.getConfigDirectory(), Gourmet.MODID + "/harvest");
        if (!harvestDir.exists()) {
            harvestDir.mkdirs();
            return;
        }

        File[] files = harvestDir.listFiles((FileFilter) FileFilterUtils.suffixFileFilter(suffix));
        if (files == null)
            return;

        for (File file : files)
            handler.accept(file);
    }
}
