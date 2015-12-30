package net.specialattack.forge.core.client.resources.data;

import com.google.gson.*;
import java.lang.reflect.Type;
import net.minecraft.client.resources.data.BaseMetadataSectionSerializer;

public class ShaderMetadataSectionSerializer extends BaseMetadataSectionSerializer<ShaderMetadataSection> {

    @Override
    public ShaderMetadataSection deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        ShaderMetadataSection result = new ShaderMetadataSection();
        if (object.has("uniforms")) {
            JsonArray uniforms = object.getAsJsonArray("uniforms");

            for (JsonElement element : uniforms) {
                result.uniforms.add(element.getAsString());
            }
        }
        if (object.has("attributes")) {
            JsonArray attributes = object.getAsJsonArray("attributes");

            for (JsonElement element : attributes) {
                result.attributes.add(element.getAsString());
            }
        }
        return result;
    }

    @Override
    public String getSectionName() {
        return "spacore:shader";
    }
}
