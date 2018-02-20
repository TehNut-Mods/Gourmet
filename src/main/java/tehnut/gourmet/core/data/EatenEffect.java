package tehnut.gourmet.core.data;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import tehnut.gourmet.core.util.GourmetLog;

import java.lang.reflect.Type;

@JsonAdapter(EatenEffect.Serializer.class)
public final class EatenEffect {

    private final Potion potion;
    private final int amplifier;
    private final int duration;
    private final double chance;

    public EatenEffect(Potion potion, int amplifier, int duration, double chance) {
        this.potion = potion;
        this.amplifier = amplifier;
        this.duration = duration;
        this.chance = chance;
    }

    public Potion getPotion() {
        return potion;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public int getDuration() {
        return duration;
    }

    public double getChance() {
        return chance;
    }

    public static class Serializer implements JsonSerializer<EatenEffect>, JsonDeserializer<EatenEffect> {
        @Override
        public EatenEffect deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            ResourceLocation potionId = new ResourceLocation(json.getAsJsonObject().getAsJsonPrimitive("potion").getAsString());
            int amplifier = json.getAsJsonObject().has("amplifier") ? json.getAsJsonObject().getAsJsonPrimitive("amplifier").getAsInt() : 0;
            int duration = json.getAsJsonObject().has("duration") ? json.getAsJsonObject().getAsJsonPrimitive("duration").getAsInt() : 100;
            double chance = json.getAsJsonObject().has("chance") ? json.getAsJsonObject().getAsJsonPrimitive("duration").getAsDouble() : 1.0D;

            Potion potion = ForgeRegistries.POTIONS.getValue(potionId);
            if (potion == null) {
                GourmetLog.DEFAULT.error("Potion with ID " + potionId + " was requested, but it does not exist.");
                return null;
            }

            return new EatenEffect(potion, amplifier, duration, chance);
        }

        @Override
        public JsonElement serialize(EatenEffect src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("potion", src.potion.getRegistryName().toString());
            jsonObject.addProperty("amplifier", src.amplifier);
            jsonObject.addProperty("duration", src.duration);
            jsonObject.addProperty("chance", src.chance);
            return jsonObject;
        }
    }
}
