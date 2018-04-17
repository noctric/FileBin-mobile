package de.michael.filebinmobile.serialize;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import de.michael.filebinmobile.model.MultiPasteUpload;

public class MultiPasteUploadDeserializer implements com.google.gson.JsonDeserializer<MultiPasteUpload> {

    @Override
    public MultiPasteUpload deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        
        return null;
    }

}
