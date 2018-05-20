package tehnut.gourmet.core.util;

import com.google.common.collect.Maps;
import net.minecraft.util.ResourceLocation;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import tehnut.gourmet.core.data.*;

import java.util.Map;
import java.util.function.Consumer;

public class DumbassHarvestXMLParser extends DefaultHandler {

    private final Consumer<Harvest> consumer;
    private final Map<String, Object> dataStorage = Maps.newHashMap();
    private Harvest.Builder builder = null;
    private String available = null;


    public DumbassHarvestXMLParser(Consumer<Harvest> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        switch (qName) {
            case "harvest": {
                String name = attributes.getValue("name");
                int hungerProvided = Integer.valueOf(attributes.getValue("hungerProvided"));
                float saturationModifier = Float.valueOf(attributes.getValue("saturationModifier"));
                builder = new Harvest.Builder(name, hungerProvided, saturationModifier);
                return;
            }
            case "effect": {
                dataStorage.put("effect_amplifier", Integer.parseInt(attributes.getValue("amplifier")));
                dataStorage.put("effect_duration", Integer.parseInt(attributes.getValue("duration")));
                dataStorage.put("effect_chance", Double.parseDouble(attributes.getValue("chance")));
            }
        }

        available = qName;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName) {
            case "harvest": {
                consumer.accept(builder.build());
                break;
            }
            case "cropGrowth": {
                int maxProduceDrop = (int) dataStorage.get("growth_maxProduceDrop");
                int maxSeedDrop = (int) dataStorage.get("growth_maxSeedDrop");
                int stages = (int) dataStorage.get("growth_stages");
                boolean canFertilize = (boolean) dataStorage.get("growth_fertilize");
                int minLight = (int) dataStorage.get("growth_minLight");
                int maxLight = (int) dataStorage.getOrDefault("growth_maxLight", 15);

                builder.setCropGrowth(new CropGrowth(maxProduceDrop, maxSeedDrop, stages, canFertilize, minLight, maxLight));
                break;
            }
            case "bushGrowth": {
                int maxProduceDrop = (int) dataStorage.get("growth_maxProduceDrop");
                int minLight = (int) dataStorage.get("growth_minLight");
                int maxLight = (int) dataStorage.getOrDefault("growth_maxLight", 15);

                builder.setBushGrowth(new BushGrowth(maxProduceDrop, minLight, maxLight));
                break;
            }
            case "effect": {
                ResourceLocation potion = (ResourceLocation) dataStorage.get("effect_potion");
                int amplifier = (int) dataStorage.get("effect_amplifier");
                int duration = (int) dataStorage.get("effect_duration");
                double chance = (double) dataStorage.get("effect_chance");

                builder.addEffect(new EatenEffect(potion, amplifier, duration, chance));
                break;
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String value = new String(ch, start, length).trim();
        if (available == null || value.equals("\\n") || value.isEmpty())
            return;

        switch (available) {
            case "growthType": {
                builder.setGrowthType(GrowthType.valueOf(value));
                break;
            }
            // Growths
            case "maxProduceDrop": {
                dataStorage.put("growth_maxProduceDrop", Integer.parseInt(value));
                break;
            }
            case "maxSeedDrop": {
                dataStorage.put("growth_maxSeedDrop", Integer.parseInt(value));
                break;
            }
            case "stages": {
                dataStorage.put("growth_stages", Integer.parseInt(value));
                break;
            }
            case "canFertilize": {
                dataStorage.put("growth_fertilize", Boolean.parseBoolean(value));
                break;
            }
            case "minLight": {
                dataStorage.put("growth_minLight", Integer.parseInt(value));
                break;
            }
            case "maxLight": {
                dataStorage.put("growth_maxLight", Integer.parseInt(value));
                break;
            }
            // Consumption
            case "consumptionStyle": {
                builder.setConsumptionStyle(ConsumeStyle.valueOf(value));
                break;
            }
            // Always edible
            case "alwaysEdible": {
                builder.setAlwaysEdible();
                break;
            }
            // Time to eat
            case "timeToEat": {
                builder.setTimeToEat(Integer.valueOf(value));
                break;
            }
            // Ore dict names
            case "oreDict": {
                builder.addOreDictionaryNames(value);
                break;
            }
            // Effects
            case "effect": {
                dataStorage.put("effect_potion", new ResourceLocation(value));
                break;
            }
        }
    }
}
