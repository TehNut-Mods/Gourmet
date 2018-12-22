package info.tehnut.gourmet.core.data;

import com.google.common.collect.Sets;
import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import info.tehnut.gourmet.core.util.GourmetLog;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

@JsonAdapter(Harvest.Serializer.class)
public final class Harvest {

    private final int hungerProvided;
    private final float saturationModifier;
    private final String simpleName;
    private final GrowthType growthType;
    private final ConsumeStyle consumptionStyle;
    private final boolean alwaysEdible;
    private final int timeToEat;
    private final String[] oreDictionaryNames;
    private final EatenEffect[] effects;
    // Only exists if growthType is CROP
    private final CropGrowth cropGrowth;
    // Only exists if growthType is BUSH
    private final BushGrowth bushGrowth;

    private Harvest(int hungerProvided, float saturationModifier, String simpleName, GrowthType growthType, ConsumeStyle consumptionStyle, boolean alwaysEdible, int timeToEat, String[] oreDictionaryNames, EatenEffect[] effects, CropGrowth cropGrowth, BushGrowth bushGrowth) {
        this.hungerProvided = hungerProvided;
        this.saturationModifier = saturationModifier;
        this.simpleName = simpleName;
        this.growthType = growthType;
        this.consumptionStyle = consumptionStyle;
        this.alwaysEdible = alwaysEdible;
        this.timeToEat = timeToEat;
        this.oreDictionaryNames = oreDictionaryNames;
        this.effects = effects;
        this.cropGrowth = cropGrowth;
        this.bushGrowth = bushGrowth;
    }

    public int getHungerProvided() {
        return hungerProvided;
    }

    public float getSaturationModifier() {
        return saturationModifier;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public GrowthType getGrowthType() {
        return growthType;
    }

    public ConsumeStyle getConsumptionStyle() {
        return consumptionStyle;
    }

    public boolean isAlwaysEdible() {
        return alwaysEdible;
    }

    public int getTimeToEat() {
        return timeToEat;
    }

    public String[] getOreDictionaryNames() {
        return oreDictionaryNames;
    }

    public EatenEffect[] getEffects() {
        return effects;
    }

    public CropGrowth getCropGrowth() {
        return cropGrowth;
    }

    public BushGrowth getBushGrowth() {
        return bushGrowth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Harvest)) return false;

        Harvest harvest = (Harvest) o;

        return simpleName.equals(harvest.simpleName);
    }

    @Override
    public int hashCode() {
        return simpleName.hashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }

    public static final class Builder {

        private final int hungerProvided;
        private final float saturationModifier;
        private final String simpleName;
        private final Set<String> oreDictionaryNames = Sets.newHashSet();
        private final Set<EatenEffect> effects = Sets.newHashSet();
        private GrowthType growthType = GrowthType.NONE;
        private ConsumeStyle consumptionStyle = ConsumeStyle.EAT;
        private boolean alwaysEdible = false;
        private int timeToEat = 32;
        private CropGrowth cropGrowth;
        private BushGrowth bushGrowth;

        public Builder(String simpleName, int hungerProvided, float saturationModifier) {
            this.simpleName = simpleName;
            this.hungerProvided = hungerProvided;
            this.saturationModifier = saturationModifier;
        }

        public Builder setGrowthType(GrowthType growthType) {
            this.growthType = growthType;
            return this;
        }

        public Builder setConsumptionStyle(ConsumeStyle consumptionStyle) {
            this.consumptionStyle = consumptionStyle;
            return this;
        }

        public Builder setAlwaysEdible() {
            this.alwaysEdible = true;
            return this;
        }

        public Builder setTimeToEat(int timeToEat) {
            this.timeToEat = timeToEat;
            return this;
        }

        public Builder addOreDictionaryNames(String... oreDictionaryNames) {
            Collections.addAll(this.oreDictionaryNames, oreDictionaryNames);
            return this;
        }

        public Builder addEffect(EatenEffect... effects) {
            Collections.addAll(this.effects, effects);
            return this;
        }

        public Builder setCropGrowth(CropGrowth cropGrowth) {
            this.cropGrowth = cropGrowth;
            return this;
        }

        public Builder setBushGrowth(BushGrowth bushGrowth) {
            this.bushGrowth = bushGrowth;
            return this;
        }

        public Harvest build() {

            switch (growthType) {
                case CROP:
                    if (cropGrowth == null)
                        cropGrowth = CropGrowth.DEFAULT;
                case BUSH:
                    if (bushGrowth == null)
                        bushGrowth = BushGrowth.DEFAULT;
            }

            Harvest harvest = new Harvest(hungerProvided, saturationModifier, simpleName, growthType, consumptionStyle, alwaysEdible, timeToEat, oreDictionaryNames.toArray(new String[0]), effects.toArray(new EatenEffect[0]), cropGrowth, bushGrowth);
            GourmetLog.FOOD_LOADER.info("Constructed as {}", ToStringBuilder.reflectionToString(harvest, ToStringStyle.NO_CLASS_NAME_STYLE));
            return harvest;
        }
    }

    public static class Serializer implements JsonSerializer<Harvest>, JsonDeserializer<Harvest> {
        @Override
        public Harvest deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject json = element.getAsJsonObject();
            String simpleName = json.getAsJsonPrimitive("name").getAsString();
            int hungerProvided = json.getAsJsonPrimitive("hungerProvided").getAsInt();
            float saturationModifier = json.getAsJsonPrimitive("saturationModifier").getAsFloat();

            Builder builder = new Builder(simpleName, hungerProvided, saturationModifier);

            if (json.has("growthType"))
                builder.setGrowthType(GrowthType.valueOf(json.getAsJsonPrimitive("growthType").getAsString()));

            switch (builder.growthType) {
                case NONE:
                    break;
                case CROP: {
                    if (!json.has("cropGrowth")) {
                        builder.setCropGrowth(CropGrowth.DEFAULT);
                        break;
                    }

                    builder.setCropGrowth(context.deserialize(json.get("cropGrowth"), CropGrowth.class));
                }
                case BUSH: {
                    if (!json.has("bushGrowth")) {
                        builder.setBushGrowth(BushGrowth.DEFAULT);
                        break;
                    }

                    builder.setBushGrowth(context.deserialize(json.get("bushGrowth"), BushGrowth.class));
                }
            }

            if (json.has("consumptionStyle"))
                builder.setConsumptionStyle(ConsumeStyle.valueOf(json.getAsJsonPrimitive("consumptionStyle").getAsString()));

            if (json.has("alwaysEdible") && json.getAsJsonPrimitive("alwaysEdible").getAsBoolean())
                builder.setAlwaysEdible();

            if (json.has("timeToEat"))
                builder.setTimeToEat(json.getAsJsonPrimitive("timeToEat").getAsInt());

            if (json.has("oreDictionaryNames"))
                builder.addOreDictionaryNames(context.deserialize(json.get("oreDictionaryNames"), String[].class));

            if (json.has("effects"))
                builder.addEffect(context.deserialize(json.get("effects"), EatenEffect[].class));

            return builder.build();
        }

        @Override
        public JsonElement serialize(Harvest src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("name", src.simpleName);
            json.addProperty("hungerProvided", src.hungerProvided);
            json.addProperty("saturationModifier", src.saturationModifier);

            json.addProperty("growthType", src.growthType.name());
            switch (src.growthType) {
                case NONE:
                    break;
                case CROP: {
                    if (src.cropGrowth != CropGrowth.DEFAULT)
                        json.add("cropGrowth", context.serialize(src.cropGrowth));
                    break;
                }
                case BUSH: {
                    if (src.bushGrowth != BushGrowth.DEFAULT)
                        json.add("bushGrowth", context.serialize(src.bushGrowth));
                    break;
                }
            }

            json.addProperty("consumptionStyle", src.consumptionStyle.name());
            json.addProperty("alwaysEdible", src.alwaysEdible);
            json.addProperty("timeToEat", src.timeToEat);
            json.add("oreDictionaryNames", context.serialize(src.oreDictionaryNames));
            json.add("effects", context.serialize(src.effects));

            return json;
        }
    }
}
