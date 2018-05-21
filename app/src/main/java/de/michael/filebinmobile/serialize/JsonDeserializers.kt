package de.michael.filebinmobile.serialize

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import de.michael.filebinmobile.model.MultiPasteUpload
import de.michael.filebinmobile.model.Upload
import java.lang.reflect.Type

const val PARAM_URL_ID = "url_id"
const val PARAM_DATE = "date"
const val PARAM_ITEMS = "items"

const val PARAM_FILE_NAME = "filename"
const val PARAM_FILE_SIZE = "filesize"
const val PARAM_MIME_TYPE = "mimetype"
const val PARAM_HASH = "hash"
const val PARAM_ID = "id"
const val PARAM_THUMBNAIL = "thumbnail"

class MultiPasteUploadDeserializer : JsonDeserializer<MultiPasteUpload> {

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): MultiPasteUpload {
        val multiPasteJsonObject = json!!.asJsonObject

        val urlId = multiPasteJsonObject.get(PARAM_URL_ID).asString
        val date = multiPasteJsonObject.get(PARAM_DATE).asLong

        val uploadItems = multiPasteJsonObject.get(urlId).asJsonObject.getAsJsonArray(PARAM_ITEMS)

        val idList = uploadItems.toList().map { it.asString }

        return MultiPasteUpload(urlId, date, idList)

    }

}

class UploadItemDeserializer : JsonDeserializer<Upload>{
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Upload {
        val uploadJsonObject = json!!.asJsonObject

        val fileName = uploadJsonObject.get(PARAM_FILE_NAME).asString
        val fileSize = uploadJsonObject.get(PARAM_FILE_SIZE).asString
        val date = uploadJsonObject.get(PARAM_DATE).asLong
        val mimeType = uploadJsonObject.get(PARAM_MIME_TYPE).asString
        val hash = uploadJsonObject.get(PARAM_HASH).asString
        val id = uploadJsonObject.get(PARAM_ID).asString
        val thumbnail = uploadJsonObject.get(PARAM_THUMBNAIL)?.asString ?: ""


        val uploadItem = Upload(
                uploadTitle = fileName,
                uploadSize = fileSize,
                uploadTimeStamp = date,
                mimeType = mimeType,
                hash = hash,
                id = id)

        if (thumbnail.isNotEmpty()) {
            uploadItem.thumbnail = thumbnail
        }

        return uploadItem
    }
}