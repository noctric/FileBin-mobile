package de.michael.filebinmobile.serialize;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import de.michael.filebinmobile.model.Upload;

public class UploadItemJsonDeserializer implements com.google.gson.JsonDeserializer<Upload> {

    @Override
    public Upload deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonUploadItem = json.getAsJsonObject();

        String filename = jsonUploadItem.get("filename").getAsString();
        String filesize = jsonUploadItem.get("filesize").getAsString();
        long date = jsonUploadItem.get("date").getAsLong();

        String mimetype = jsonUploadItem.get("mimetype").getAsString();
        String hash = jsonUploadItem.get("hash").getAsString();
        String id = jsonUploadItem.get("id").getAsString();
        String thumbnail = "";
        JsonElement thumbnailAsJson = jsonUploadItem.get("thumbnail");
        if (thumbnailAsJson != null) {
            thumbnail = thumbnailAsJson.getAsString();
        }

        Upload upload = new Upload(filename, filesize, date);
        upload.setId(id);
        upload.setHash(hash);
        upload.setMimeType(mimetype);
        upload.setThumbnail(thumbnail);

        return upload;
    }
}
