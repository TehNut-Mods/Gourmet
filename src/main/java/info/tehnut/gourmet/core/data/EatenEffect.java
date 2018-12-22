package info.tehnut.gourmet.core.data;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import info.tehnut.gourmet.core.util.GourmetLog;
import info.tehnut.gourmet.core.util.RegistryGetter;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.lang.reflect.Type;

@JsonAdapter(EatenEffect.Serializer.class)
public final class EatenEffect {

    private final RegistryGetter<StatusEffect> statusEffect;
    private final int amplifier;
    private final int duration;
    private final double chance;

    public EatenEffect(Identifier registryName, int amplifier, int duration, double chance) {
        this.statusEffect = new RegistryGetter<>(registryName, StatusEffect.class, Registry.STATUS_EFFECT);
        this.amplifier = amplifier;
        this.duration = duration;
        this.chance = chance;
    }

    public EatenEffect(StatusEffect statusEffect, int amplifier, int duration, double chance) {
        this(Registry.STATUS_EFFECT.getId(statusEffect), amplifier, duration, chance);
    }

    public StatusEffect getStatusEffect() {
        StatusEffect value = statusEffect.getValue();
        if (value == null)
            GourmetLog.DEFAULT.error("StatusEffect with ID {} was requested from registry but does not exist.", statusEffect.getRegistryName());
        return statusEffect.getValue();
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }

    public static class Serializer implements JsonSerializer<EatenEffect>, JsonDeserializer<EatenEffect> {
        @Override
        public EatenEffect deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject json = element.getAsJsonObject();
            Identifier potionId = new Identifier(json.getAsJsonPrimitive("potion").getAsString());
            int amplifier = json.has("amplifier") ? json.getAsJsonPrimitive("amplifier").getAsInt() : 0;
            int duration = json.has("duration") ? json.getAsJsonPrimitive("duration").getAsInt() : 100;
            double chance = json.has("chance") ? json.getAsJsonPrimitive("chance").getAsDouble() : 1.0D;

            return new EatenEffect(potionId, amplifier, duration, chance);
        }

        @Override
        public JsonElement serialize(EatenEffect src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("statusEffect", Registry.STATUS_EFFECT.getId(src.statusEffect.getValue()).toString());
            jsonObject.addProperty("amplifier", src.amplifier);
            jsonObject.addProperty("duration", src.duration);
            jsonObject.addProperty("chance", src.chance);
            return jsonObject;
        }
    }
}
