package de.michael.filebinmobile.serialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import de.michael.filebinmobile.model.MultiPasteUpload;

public class MultiPasteUploadDeserializer implements com.google.gson.JsonDeserializer<MultiPasteUpload> {

    @Override
    public MultiPasteUpload deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject multiPasteJsonItem = json.getAsJsonObject();

        String url_id = multiPasteJsonItem.get("url_id").getAsString();
        long date = multiPasteJsonItem.get("date").getAsLong();

        JsonArray singleUploadItems = multiPasteJsonItem.get(url_id).getAsJsonObject()
                .getAsJsonArray("items");

        String[] itemIds = new String[singleUploadItems.size()];

        for (int i = 0; i < singleUploadItems.size(); i++) {
            // TODO hmmmm
            itemIds[i] = singleUploadItems.get(i).getAsString();
        }

        return new MultiPasteUpload(url_id, date, itemIds);
    }

}
