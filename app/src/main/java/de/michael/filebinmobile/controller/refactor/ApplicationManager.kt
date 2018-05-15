package de.michael.filebinmobile.controller.refactor

import com.google.gson.GsonBuilder
import de.michael.filebinmobile.model.MultiPasteUpload
import de.michael.filebinmobile.model.Server
import de.michael.filebinmobile.model.Upload
import de.michael.filebinmobile.model.UserProfile
import de.michael.filebinmobile.serialize.MultiPasteUploadDeserializer
import de.michael.filebinmobile.serialize.UploadItemJsonDeserializer
import de.michael.filebinmobile.util.FileChooserUtil
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit


// region CONSTANTS
// Endpoints
private const val ACCESS_API = "/api/"

private const val ENDPOINT_USER_CREATE_API_KEY = "user/create_apikey"
private const val ENDPOINT_USER_DELETE_API_KEY = "user/delete_apikey"
private const val ENDPOINT_USER_LIST_API_KEYS = "user/apikeys"

private const val ENDPOINT_FILE_GET_CONFIG = "file/get_config"
private const val ENDPOINT_FILE_UPLOAD = "file/upload"
private const val ENDPOINT_FILE_HISTORY = "file/history"
private const val ENDPOINT_FILE_DELETE = "file/delete"
private const val ENDPOINT_FILE_CREATE_MULTIPASTE = "file/create_multipaste"

// params
private const val PARAM_USER_NAME = "username"
private const val PARAM_USER_PW = "password"
private const val PARAM_APIKEY = "apikey"
private const val PARAM_USER_ACCESS_LEVEL = "access_level"
private const val PARAM_USER_COMMENT = "comment"

private const val PARAM_RESPONSE_NEW_API_KEY = "new_key"
private const val PARAM_RESPONSE_URLS = "urls"
private const val PARAM_RESPONSE_IDS = "ids"

private const val PARAM_RESPONSE_HISTORY_ITEMS = "items"
private const val PARAM_RESPONSE_HISTORY_MULT_ITEMS = "multipaste_items"

private const val PARAM_RESPONSE_MAX_UPL_SIZE = "upload_max_size"
private const val PARAM_RESPONSE_MAX_FILES_REQ = "max_files_per_request"
private const val PARAM_RESPONSE_MAX_IN_VARS = "max_input_vars"
private const val PARAM_RESPONSE_MAX_SIZE_REQ = "request_max_size"

private val API_VERSIONS = arrayOf("/api/v2.1.1", "/api/v2.1.0", "/api/v2.0.0", "/api/v1.4.0",
        "/api/v1.3.0", "/api/v1.2.0", "/api/v1.0.0")
// endregion

object NetworkManager {
    private val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()

    private val gson = GsonBuilder()
            .registerTypeAdapter(Upload::class.java, UploadItemJsonDeserializer())
            .registerTypeAdapter(MultiPasteUpload::class.java, MultiPasteUploadDeserializer()).create()


    private fun buildAndExecuteRequest(url: String, body: RequestBody,
                                       onError: (String) -> Unit = {}): JSONObject? {

        // build our post request
        val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

        val response = client.newCall(request).execute()

        if (response.isSuccessful) {

            val responseString = response.body()?.string()

            if (!responseString.isNullOrEmpty()) {

                val responseBodyAsJson = JSONObject(responseString)

                if (responseBodyAsJson.isErrorResponse()) {

                    //TODO this is currently not safe if any api changes occur, e.g. var name changes
                    onError(responseBodyAsJson.getErrorMessage())
                    return null

                } else {

                    return responseBodyAsJson.getDataObject()

                }
            }
        }

        onError("Request returned ${response.code()}")
        return null
    }

    fun generateApiKey(userName: String, password: String, serverAddr: String,
                       onError: (String) -> Unit = {}): String? {
        val url = "$serverAddr${getLatestApiVersion()}/$ENDPOINT_USER_CREATE_API_KEY"

        val formBody = FormBody.Builder()
                .add(PARAM_USER_NAME, userName)
                .add(PARAM_USER_PW, password)
                .add(PARAM_USER_ACCESS_LEVEL, "apikey")
                .add(PARAM_USER_COMMENT, "FileBin mobile for Android: $userName on $serverAddr")
                .build()

        return buildAndExecuteRequest(url, formBody, onError)?.getString(PARAM_RESPONSE_NEW_API_KEY)
    }

    fun pasteUploadFiles(user: UserProfile, server: Server, files: Array<File>,
                         onError: (String) -> Unit = {}): List<String> {
        val url = "${server.addr}${getLatestApiVersion()}/$ENDPOINT_FILE_UPLOAD"

        val multipartBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(PARAM_APIKEY, user.apiKey)

        for (i in 0 until files.size) {

            val mimeType = FileChooserUtil.getMimeType(files[i])
            val mediaType = MediaType.parse("$mimeType; charset=utf-8")

            val requestBody = RequestBody.create(mediaType, files[i])

            multipartBuilder.addFormDataPart("file[$i]", files[i].name, requestBody)
        }

        val multipartBody = multipartBuilder.build()

        val jsonArray = buildAndExecuteRequest(url, multipartBody, onError)?.getJSONArray(PARAM_RESPONSE_URLS)
        return jsonArray?.iterator<String>()?.asSequence()?.toList() ?: emptyList()
    }
}

private fun getLatestApiVersion(): String = API_VERSIONS[1]

fun JSONObject?.isErrorResponse(): Boolean =
// using smart cast from nullable JSONObject to not null JSONObject after null check
        this != null && this.isNull("status") && this.getString("status") == "error"

fun JSONObject.getErrorMessage(): String = this.getString("message")

fun JSONObject.getDataObject(): JSONObject = this.getJSONObject("data")

// create an iterator for JSONArrays
operator fun <T> JSONArray.iterator(): Iterator<T> =
        (0 until this.length()).asSequence().map { get(it) as T }.iterator()