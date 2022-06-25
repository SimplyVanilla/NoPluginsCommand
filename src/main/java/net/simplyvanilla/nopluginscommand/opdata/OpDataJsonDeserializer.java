package net.simplyvanilla.nopluginscommand.opdata;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.UUID;

public class OpDataJsonDeserializer implements JsonDeserializer<OpData> {

    @Override
    public OpData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        return new OpData(
            UUID.fromString(obj.get("uuid").getAsString()),
            obj.get("name").getAsString(),
            obj.get("level").getAsInt(),
            obj.get("bypassesPlayerLimit").getAsBoolean()
        );
    }

}
