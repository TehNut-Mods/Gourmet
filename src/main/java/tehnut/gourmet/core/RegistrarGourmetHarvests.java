package tehnut.gourmet.core;

import com.google.gson.reflect.TypeToken;
import joptsimple.internal.Strings;
import net.minecraft.init.MobEffects;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import tehnut.gourmet.Gourmet;
import tehnut.gourmet.core.data.*;
import tehnut.gourmet.core.util.DumbassHarvestXMLParser;
import tehnut.gourmet.core.util.GourmetLog;
import tehnut.gourmet.core.util.JsonUtil;
import tehnut.gourmet.core.util.loader.HarvestLoader;
import tehnut.gourmet.core.util.loader.IHarvestLoader;

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

    @HarvestLoader("bultin")
    public static final IHarvestLoader BUILTIN = harvests -> {
        harvests.accept(new Harvest.Builder("sliced_bread", 1, 0.2F).build());
        harvests.accept(new Harvest.Builder("toast", 2, 0.8F).build());
        harvests.accept(new Harvest.Builder("strawberry", 1, 0.1F).setGrowthType(GrowthType.BUSH).setBushGrowth(BushGrowth.DEFAULT).setAlwaysEdible().build());
        harvests.accept(new Harvest.Builder("blueberry", 1, 0.1F).setGrowthType(GrowthType.BUSH).setBushGrowth(BushGrowth.DEFAULT).setAlwaysEdible().build());
        harvests.accept(new Harvest.Builder("jam_strawberry", 5, 0.2F).setConsumptionStyle(ConsumeStyle.DRINK).addEffect(new EatenEffect(MobEffects.SPEED, 0, 100, 1.0D)).setAlwaysEdible().build());
        harvests.accept(new Harvest.Builder("jam_blueberry", 5, 0.2F).setConsumptionStyle(ConsumeStyle.DRINK).addEffect(new EatenEffect(MobEffects.SPEED, 0, 100, 1.0D)).setAlwaysEdible().build());
    };

    @HarvestLoader("json")
    public static final IHarvestLoader JSON_LOADER = harvests -> parseFiles(".json", f -> harvests.accept(JsonUtil.fromJson(TypeToken.get(Harvest.class), f)));

    @HarvestLoader("xml")
    public static final IHarvestLoader XML_LOADER = harvests -> parseFiles(".xml", f -> {
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(f, new DumbassHarvestXMLParser(harvests));
        } catch (Exception e) {
            e.printStackTrace();
        }
    });

    @HarvestLoader("remote")
    public static final IHarvestLoader REMOTE_LOADER = harvests -> {
        if (Strings.isNullOrEmpty(GourmetConfig.remote.remoteJson))
            return;

        GourmetLog.DEFAULT.info("Attempting remote connection to {}", GourmetConfig.remote.remoteJson);
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(GourmetConfig.remote.remoteJson).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("user-agent", "Gourmet Minecraft Mod");
            String response = IOUtils.toString(urlConnection.getInputStream(), StandardCharsets.UTF_8);
            JsonUtil.fromJson(new TypeToken<List<Harvest>>(){}, response).forEach(harvests);
        } catch (Exception e) {
            GourmetLog.FOOD_LOADER.error("Error loading remote harvests: {}", e.getMessage());
        }
    };

    private static void parseFiles(String suffix, Consumer<File> handler) {
        File harvestDir = new File(Loader.instance().getConfigDir(), Gourmet.MODID + "/harvest");
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
