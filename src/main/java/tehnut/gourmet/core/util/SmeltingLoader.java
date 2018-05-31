package tehnut.gourmet.core.util;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;

public class SmeltingLoader {

    public static void gatherRecipes(Consumer<SmeltingRecipe> smelting) {
        Set<Path> jsons = ResourceUtil.gatherResources("/assets/gourmet", "smelting", p -> FilenameUtils.getExtension(p.toFile().getName()).equals("json"));
        for (Path recipeJson : jsons) {
            try {
                String json = IOUtils.toString(Files.newBufferedReader(recipeJson));
                smelting.accept(JsonUtil.fromJson(TypeToken.get(SmeltingRecipe.class), json));
            } catch (Exception e) {
                GourmetLog.DEFAULT.error("Error loading smelting recipe at {}", recipeJson);
            }
        }
    }

    public static class SmeltingRecipe {

        @JsonAdapter(SerializerItemStack.class)
        private final ItemStack input;
        @JsonAdapter(SerializerItemStack.class)
        private final ItemStack output;
        private final float experience;

        public SmeltingRecipe(ItemStack input, ItemStack output, float experience) {
            this.input = input;
            this.output = output;
            this.experience = experience;
        }

        public ItemStack getInput() {
            return input;
        }

        public ItemStack getOutput() {
            return output;
        }

        public float getExperience() {
            return experience;
        }

        public static class SerializerItemStack implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
            @Override
            public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(json.getAsJsonObject().getAsJsonPrimitive("item").getAsString()));
                int count = 1;
                if (json.getAsJsonObject().has("count"))
                    count = json.getAsJsonObject().get("count").getAsInt();
                int meta = 0;
                if (json.getAsJsonObject().has("data"))
                    meta = json.getAsJsonObject().getAsJsonPrimitive("data").getAsInt();

                return new ItemStack(item, count, meta);
            }

            @Override
            public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("item", src.getItem().getRegistryName().toString());
                if (src.getCount() > 1)
                    jsonObject.addProperty("count", src.getCount());
                if (src.getMetadata() != 0)
                    jsonObject.addProperty("data", src.getMetadata());
                return jsonObject;
            }
        }
    }
}
