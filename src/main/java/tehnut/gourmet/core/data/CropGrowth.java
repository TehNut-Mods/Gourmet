package tehnut.gourmet.core.data;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.lang.reflect.Type;
import java.util.function.Predicate;

@JsonAdapter(CropGrowth.Serializer.class)
public final class CropGrowth {

    public static final CropGrowth DEFAULT = new CropGrowth(7, true, 9);

    private final int stages;
    private final boolean canFertilize;
    private final Predicate<Integer> lightCheck;
    // For serialization purposes
    private transient final int minLight;
    private transient final int maxLight;

    public CropGrowth(int stages, boolean canFertilize, int minLight, int maxLight) {
        this.stages = stages;
        this.canFertilize = canFertilize;
        this.lightCheck = light -> light >= minLight && light <= maxLight;
        this.minLight = minLight;
        this.maxLight = maxLight;
    }

    public CropGrowth(int stages, boolean canFertilize, int minLight) {
        this(stages, canFertilize, minLight, 15);
    }

    public int getStages() {
        return stages;
    }

    public boolean canFertilize() {
        return canFertilize;
    }

    public boolean checkLight(int lightLevel) {
        return lightCheck.test(lightLevel);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }

    public static class Serializer implements JsonSerializer<CropGrowth>, JsonDeserializer<CropGrowth> {
        @Override
        public CropGrowth deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject json = element.getAsJsonObject();
            int stages = json.has("stages") ? json.getAsJsonPrimitive("stages").getAsInt() : 7;
            boolean canFertilize = !json.has("canFertilize") || json.getAsJsonPrimitive("canFertilize").getAsBoolean();
            int minLight = json.has("minLight") ? json.getAsJsonPrimitive("minLight").getAsInt() : 8;
            int maxLight = json.has("maxLight") ? json.getAsJsonPrimitive("maxLight").getAsInt() : 15;

            return new CropGrowth(stages, canFertilize, minLight, maxLight);
        }

        @Override
        public JsonElement serialize(CropGrowth src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("stages", src.stages);
            jsonObject.addProperty("canFertilize", src.canFertilize);
            jsonObject.addProperty("minLight", src.minLight);
            jsonObject.addProperty("maxLight", src.maxLight);
            return jsonObject;
        }
    }
}
