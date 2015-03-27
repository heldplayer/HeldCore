package net.specialattack.forge.core.client.resources.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.client.resources.data.BaseMetadataSectionSerializer;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.Validate;

public class TextureMetadataSectionSerializer extends BaseMetadataSectionSerializer {

    @Override
    public TextureMetadataSection deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        TextureMetadataSection result = new TextureMetadataSection();
        result.textureWidth = JsonUtils.getJsonObjectIntegerFieldValueOrDefault(object, "width", 128);
        result.textureHeight = JsonUtils.getJsonObjectIntegerFieldValueOrDefault(object, "height", 128);
        if (object.has("repeat")) {
            JsonObject repeat = object.getAsJsonObject("repeat");

            int borderLeft = JsonUtils.getJsonObjectIntegerFieldValueOrDefault(repeat, "border-left", -1);
            int borderTop = JsonUtils.getJsonObjectIntegerFieldValueOrDefault(repeat, "border-top", -1);
            int borderRight = JsonUtils.getJsonObjectIntegerFieldValueOrDefault(repeat, "border-right", -1);
            int borderBottom = JsonUtils.getJsonObjectIntegerFieldValueOrDefault(repeat, "border-bottom", -1);

            if (borderLeft != -1 || borderTop != -1 || borderRight != -1 || borderBottom != -1) {
                Validate.inclusiveBetween(0, Integer.MAX_VALUE, borderLeft, "Invalid border left");
                Validate.inclusiveBetween(0, Integer.MAX_VALUE, borderTop, "Invalid border top");
                Validate.inclusiveBetween(0, Integer.MAX_VALUE, borderRight, "Invalid border right");
                Validate.inclusiveBetween(0, Integer.MAX_VALUE, borderBottom, "Invalid border bottom");

                result.repeat = new TextureMetadataSection.Repeat(borderLeft, borderTop, borderRight, borderBottom);
            } else {
                result.repeat = new TextureMetadataSection.Repeat(0, 0, 0, 0);
            }
        }
        return result;
    }

    @Override
    public String getSectionName() {
        return "spacore:texture";
    }

}
