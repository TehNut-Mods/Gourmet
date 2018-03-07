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
import tehnut.gourmet.core.util.loader.HarvestLoader;
import tehnut.gourmet.core.util.loader.IHarvestLoader;
import tehnut.gourmet.core.util.JsonUtil;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileFilter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RegistrarGourmetHarvests {

    @HarvestLoader
    public static final IHarvestLoader BUILTIN = harvests -> {
        harvests.accept(new Harvest.Builder("sliced_bread", 1, 0.2F).build());
        harvests.accept(new Harvest.Builder("toast", 2, 0.8F).build());
        harvests.accept(new Harvest.Builder("strawberry", 1, 0.1F).setGrowthType(GrowthType.BUSH).setBushGrowth(BushGrowth.DEFAULT).setAlwaysEdible().build());
        harvests.accept(new Harvest.Builder("blueberry", 1, 0.1F).setGrowthType(GrowthType.BUSH).setBushGrowth(BushGrowth.DEFAULT).setAlwaysEdible().build());
        harvests.accept(new Harvest.Builder("jam_strawberry", 5, 0.2F).setConsumptionStyle(ConsumeStyle.DRINK).addEffect(new EatenEffect(MobEffects.SPEED, 0, 100, 1.0D)).setAlwaysEdible().build());
        harvests.accept(new Harvest.Builder("jam_blueberry", 5, 0.2F).setConsumptionStyle(ConsumeStyle.DRINK).addEffect(new EatenEffect(MobEffects.SPEED, 0, 100, 1.0D)).setAlwaysEdible().build());
    };

    @HarvestLoader
    public static final IHarvestLoader JSON_LOADER = harvests -> {
        File harvestDir = new File(Loader.instance().getConfigDir(), Gourmet.MODID + "/harvest");
        if (!harvestDir.exists()) {
            harvestDir.mkdirs();
            return;
        }

        File[] jsonFiles = harvestDir.listFiles((FileFilter) FileFilterUtils.suffixFileFilter(".json"));
        if (jsonFiles == null)
            return;

        for (File file : jsonFiles)
            harvests.accept(JsonUtil.fromJson(TypeToken.get(Harvest.class), file));
    };

    @HarvestLoader
    public static final IHarvestLoader XML_LOADER = harvests -> {
        File harvestDir = new File(Loader.instance().getConfigDir(), Gourmet.MODID + "/harvest");
        if (!harvestDir.exists()) {
            harvestDir.mkdirs();
            return;
        }

        File[] xmlFiles = harvestDir.listFiles((FileFilter) FileFilterUtils.suffixFileFilter(".xml"));
        if (xmlFiles == null)
            return;

        for (File file : xmlFiles) {
            try {
                SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
                parser.parse(file, new DumbassHarvestXMLParser(harvests));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @HarvestLoader
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
}
