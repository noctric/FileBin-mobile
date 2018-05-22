package de.michael.filebinmobile.controller

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import de.michael.filebinmobile.model.MultiPasteUpload
import de.michael.filebinmobile.model.Server
import de.michael.filebinmobile.model.Upload
import de.michael.filebinmobile.model.UserProfile
import de.michael.filebinmobile.serialize.MultiPasteUploadDeserializer
import de.michael.filebinmobile.serialize.UploadItemDeserializer
import de.michael.filebinmobile.util.FileUtil
import okhttp3.*
import okio.Okio
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit


// region CONSTANTS
// region Endpoints
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
// region preference keys
private const val KEY_STORE_PREFS = "de.michael.filebin.prefs"
private const val KEY_SERVER_LIST = "de.michael.filebin.SERVER_NAMES"
private const val KEY_SELECTED_POSTINFO = "de.michael.filebin.SELECTED_POSTINFO"
private const val KEY_APP_LAUNCHED_BEFORE = "de.michael.filebin.FIRST_APP_LAUNCH"
// endregion
// endregion

private val gson = GsonBuilder()
        .registerTypeAdapter(Upload::class.java, UploadItemDeserializer())
        .registerTypeAdapter(MultiPasteUpload::class.java, MultiPasteUploadDeserializer()).create()

object NetworkManager {
    private val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()

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

    fun pasteUploadFiles(user: UserProfile, server: Server, files: List<File>,
                         onError: (String) -> Unit = {}): List<String> {
        val url = "${server.address}${getLatestApiVersion()}/$ENDPOINT_FILE_UPLOAD"

        val multipartBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(PARAM_APIKEY, user.apiKey)

        for (i in 0 until files.size) {

            val mimeType: String? = FileUtil.getMimeType(files[i])
            val mediaType = MediaType.parse("${mimeType ?: "text/plain"}; charset=utf-8")

            val requestBody = RequestBody.create(mediaType, files[i])

            multipartBuilder.addFormDataPart("file[$i]", files[i].name, requestBody)
        }

        val multipartBody = multipartBuilder.build()

        val jsonArray = buildAndExecuteRequest(url, multipartBody, onError)?.getJSONArray(PARAM_RESPONSE_URLS)
        return jsonArray?.iterator<String>()?.asSequence()?.toList() ?: emptyList()
    }

    fun downloadFile(fileUrl: String, filePath: String, onError: (String) -> Unit = {}): File? {

        val request = Request.Builder()
                .url(fileUrl)
                .get()
                .build()

        val response = client.newCall(request).execute()

        if (response.isSuccessful) {

            val downloadedFile = File(filePath)

            response.body()!!.source().use { bufferedSource ->
                val bufferedSink = Okio.buffer(Okio.sink(downloadedFile))
                bufferedSink.writeAll(bufferedSource)
                bufferedSink.close()
            }

            return downloadedFile
        }

        onError("Error while downloading file")
        return null
    }

    fun Server.updateServerInfo(onError: (String) -> Unit = {}) {
        val url = "${this.address}${getLatestApiVersion()}/$ENDPOINT_FILE_GET_CONFIG"

        val request = Request.Builder()
                .url(url)
                .build()

        val response = client.newCall(request).execute()

        if (response.isSuccessful) {

            val dataObject = JSONObject(response.body()?.string()).getDataObject()

            if (dataObject.isErrorResponse()) {

                onError(dataObject.getErrorMessage())

            } else {

                this.uploadMaxSize = dataObject.getInt(PARAM_RESPONSE_MAX_UPL_SIZE)
                this.maxFilesPerRequest = dataObject.getInt(PARAM_RESPONSE_MAX_FILES_REQ)
                this.maxInputVars = dataObject.getInt(PARAM_RESPONSE_MAX_IN_VARS)
                this.requestMaxSize = dataObject.getInt(PARAM_RESPONSE_MAX_SIZE_REQ)

            }

        } else {

            onError("Response with code ${response.code()}")

        }

    }

    fun loadUploadHistory(user: UserProfile, server: Server, onError: (String) -> Unit = {}): List<Upload>? {
        val url = "${server.address}${getLatestApiVersion()}/$ENDPOINT_FILE_HISTORY"

        val body = FormBody.Builder()
                .add(PARAM_APIKEY, user.apiKey)
                .build()

        val response = buildAndExecuteRequest(url, body, onError)

        val historyItems = response?.getJSONObject(PARAM_RESPONSE_HISTORY_ITEMS)?.getObjectsWithoutKey()

        return historyItems?.map { gson.fromJson(it.toString(), Upload::class.java) }?.sortedByDescending { it.uploadTimeStamp }
    }

    fun deleteUploads(server: Server, uploads: List<Upload>, onError: (String) -> Unit = {}): Boolean {

        val url = "${server.address}${getLatestApiVersion()}/$ENDPOINT_FILE_DELETE"

        val formBodyBuilder = FormBody.Builder()
                .add(PARAM_APIKEY, server.userProfile!!.apiKey)

        for (i in 0 until uploads.size) {
            formBodyBuilder.add("ids[$i]", uploads[i].id)
        }

        val response = buildAndExecuteRequest(url, formBodyBuilder.build(), onError)

        return response?.isErrorResponse()?.not() ?: false

    }
}

object SettingsManager {

    private fun getAppPreferences(context: Context): SharedPreferences =
            context.getSharedPreferences(KEY_STORE_PREFS, Context.MODE_PRIVATE)

    fun hasLaunchedBefore(context: Context): Boolean =
            getAppPreferences(context).getBoolean(KEY_APP_LAUNCHED_BEFORE, false)

    fun setHasLaunchedBefore(context: Context, hasLaunchedBefore: Boolean) =
            getAppPreferences(context)
                    .edit()
                    .putBoolean(KEY_APP_LAUNCHED_BEFORE, hasLaunchedBefore)
                    .apply()

    fun addServer(context: Context, server: Server) {
        val mutableSet = getAppPreferences(context)
                .getStringSet(KEY_SERVER_LIST, emptySet())
                .toMutableSet()

        mutableSet.add(gson.toJson(server))

        getAppPreferences(context)
                .edit()
                .putStringSet(KEY_SERVER_LIST, mutableSet)
                .apply()
    }

    fun getServerList(context: Context): List<Server> {
        val serverSet = getAppPreferences(context).getStringSet(KEY_SERVER_LIST, emptySet())

        return serverSet
                .map { gson.fromJson(it, Server::class.java) }
                .toList()
    }

    fun deleteServer(context: Context, server: Server) {

        val mutableSet = getAppPreferences(context)
                .getStringSet(KEY_SERVER_LIST, emptySet())
                .toMutableSet()

        mutableSet.remove(gson.toJson(server))

        getAppPreferences(context)
                .edit()
                .putStringSet(KEY_SERVER_LIST, mutableSet)
                .apply()
    }

    fun setPostInfo(context: Context, server: Server?) =
            getAppPreferences(context)
                    .edit()
                    .putString(KEY_SELECTED_POSTINFO, gson.toJson(server))
                    .apply()

    fun getPostInfo(context: Context): Server? {
        val serializedPostInfo = getAppPreferences(context).getString(KEY_SELECTED_POSTINFO, null)

        return gson.fromJson(serializedPostInfo, Server::class.java)
    }
}

//region helpers
private fun getLatestApiVersion(): String = API_VERSIONS[1]

fun JSONObject?.isErrorResponse(): Boolean {
    // using smart cast from nullable JSONObject to not null JSONObject after null check
    if (this == null) return true
    return this.has("status") && this.getString("status") == "error"
}

fun JSONObject.getErrorMessage(): String = this.getString("message")

fun JSONObject.getDataObject(): JSONObject = this.getJSONObject("data")

// create an iterator for JSONArrays
operator fun <T> JSONArray.iterator(): Iterator<T> =
        (0 until this.length()).asSequence().map { get(it) as T }.iterator()

fun JSONObject.getObjectsWithoutKey(): List<JSONObject> {
    val jsonObjects = mutableListOf<JSONObject>()

    for (key in this.keys()) {
        jsonObjects.add(this.getJSONObject(key))
    }

    return jsonObjects

}
//endregion