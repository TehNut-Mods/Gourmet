package tehnut.gourmet.core.data;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.lang.reflect.Type;
import java.util.function.Predicate;

@JsonAdapter(BushGrowth.Serializer.class)
public class BushGrowth {

    private final int maxProduce;
    private final Predicate<Integer> lightCheck;
    // For serialization purposes
    private final transient int minLight;
    private final transient int maxLight;

    public BushGrowth(int maxProduce, int minLight, int maxLight) {
        this.maxProduce = maxProduce;
        this.lightCheck = light -> light >= minLight && light <= maxLight;
        this.minLight = minLight;
        this.maxLight = maxLight;
    }

    public int getMaxProduce() {
        return maxProduce;
    }

    public boolean checkLight(int lightLevel) {
        return lightCheck.test(lightLevel);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
                .append("maxProduce", maxProduce)
                .append("lightCheck", lightCheck)
                .append("minLight", minLight)
                .append("maxLight", maxLight)
                .toString();
    }

    public static class Serializer implements JsonSerializer<BushGrowth>, JsonDeserializer<BushGrowth> {
        @Override
        public BushGrowth deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            int maxProduce = json.getAsJsonObject().has("maxProduce") ? json.getAsJsonObject().getAsJsonPrimitive("maxProduce").getAsInt() : 15;
            int minLight = json.getAsJsonObject().has("minLight") ? json.getAsJsonObject().getAsJsonPrimitive("minLight").getAsInt() : 8;
            int maxLight = json.getAsJsonObject().has("maxLight") ? json.getAsJsonObject().getAsJsonPrimitive("maxLight").getAsInt() : 15;
            return new BushGrowth(maxProduce, minLight, maxLight);
        }

        @Override
        public JsonElement serialize(BushGrowth src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("maxProduce", src.maxProduce);
            object.addProperty("minLight", src.minLight);
            object.addProperty("maxLight", src.maxLight);
            return object;
        }
    }
}
