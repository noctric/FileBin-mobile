package de.michael.filebinmobile.controller;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import de.michael.filebinmobile.model.MultiPasteUpload;
import de.michael.filebinmobile.model.PostInfo;
import de.michael.filebinmobile.model.Server;
import de.michael.filebinmobile.model.Upload;
import de.michael.filebinmobile.model.UserProfile;
import de.michael.filebinmobile.serialize.MultiPasteUploadDeserializer;
import de.michael.filebinmobile.serialize.UploadItemJsonDeserializer;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkManager {

    private static final NetworkManager INSTANCE = new NetworkManager();

    // Documentation available at https://github.com/Bluewind/filebin/blob/master/doc/api/

    // region CONSTANTS
    // Endpoints
    private static final String ENDPOINT_USER_CREATE_API_KEY = "user/create_apikey";
    private static final String ENDPOINT_USER_DELETE_API_KEY = "user/delete_apikey";
    private static final String ENDPOINT_USER_LIST_API_KEYS = "user/apikeys";

    private static final String ENDPOINT_FILE_GET_CONFIG = "file/get_config";
    private static final String ENDPOINT_FILE_UPLOAD = "file/upload";
    private static final String ENDPOINT_FILE_HISTORY = "file/history";
    private static final String ENDPOINT_FILE_DELETE = "file/delete";
    private static final String ENDPOINT_FILE_CREATE_MULTIPASTE = "file/create_multipaste";

    // params
    private static final String PARAM_USER_NAME = "username";
    private static final String PARAM_USER_PW = "password";
    private static final String PARAM_APIKEY = "apikey";
    private static final String PARAM_USER_ACCESS_LEVEL = "access_level";
    private static final String PARAM_USER_COMMENT = "comment";

    private static final String PARAM_RESPONSE_NEW_API_KEY = "new_key";
    private static final String PARAM_RESPONSE_URLS = "urls";
    private static final String PARAM_RESPONSE_IDS = "ids";

    private static final String PARAM_RESPONSE_HISTORY_ITEMS = "items";
    private static final String PARAM_RESPONSE_HISTORY_MULT_ITEMS = "multipaste_items";

    private static final String PARAM_RESPONSE_MAX_UPL_SIZE = "upload_max_size";
    private static final String PARAM_RESPONSE_MAX_FILES_REQ = "max_files_per_request";
    private static final String PARAM_RESPONSE_MAX_IN_VARS = "max_input_vars";
    private static final String PARAM_RESPONSE_MAX_SIZE_REQ = "request_max_size";


    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private static final MediaType IMAGE_PNG
            = MediaType.parse("image/png; charset=utf-8");

    private static final MediaType IMAGE_JPG
            = MediaType.parse("image/jpg; charset=utf-8");

    private static final MediaType IMAGE_JPEG
            = MediaType.parse("image/jpeg; charset=utf-8");

    private static final MediaType TEXT
            = MediaType.parse("text/x-markdown; charset=utf-8");

    private static final MediaType VIDEO_MP4
            = MediaType.parse("video/mp4");
    // endregion

    private OkHttpClient client = new OkHttpClient();

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(Upload.class, new UploadItemJsonDeserializer())
            .registerTypeAdapter(MultiPasteUpload.class, new MultiPasteUploadDeserializer()).create();


    private NetworkManager() {
    }

    public static NetworkManager getInstance() {
        return INSTANCE;
    }

    /**
     * Makes a request to generate a new api key from the server.
     *
     * @param username   Required param.
     * @param password   Required param.
     * @param serverAddr Required param.
     * @return A newly created api key or null if not successful.
     */
    public @Nullable
    String generateApiKey(@NonNull String username, @NonNull String password, @NonNull String serverAddr) {

        // TODO tidy this up :)
        String apiVersion = "v2.1.0";
        String url = serverAddr + "/api/" + apiVersion + "/" + ENDPOINT_USER_CREATE_API_KEY;

        RequestBody body = new FormBody.Builder()
                .add(PARAM_USER_NAME, username)
                .add(PARAM_USER_PW, password)
                .add(PARAM_USER_ACCESS_LEVEL, "apikey")
                .add(PARAM_USER_COMMENT, "FileBin mobile for Android: " + username + " on " + serverAddr)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                JSONObject responseData = new JSONObject(response.body().string()).getJSONObject("data");

                if (responseData != null) {
                    return responseData.getString(PARAM_RESPONSE_NEW_API_KEY);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Posts a file (or set of files) to a given filebin server
     *
     * @param user   user profile posting the file(s)
     * @param server server which holds the user profile
     * @param files  files to be posted by user
     * @return upload url(s) in json format
     */
    public String pasteUploadFiles(@NonNull UserProfile user, @NonNull Server server, File[] files) {

        String apiVersion = "v2.1.0";
        String url = server.getAddr() + "/api/" + apiVersion + "/" + ENDPOINT_FILE_UPLOAD;


        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (File f : files) {

            String fileExtension = getFileExtension(f.getAbsolutePath());
            MediaType mediaType = null;

            if (fileExtension != null) {

                if (fileExtension.equals("png")) {
                    mediaType = IMAGE_PNG;
                } else if (fileExtension.equals("jpg")) {
                    mediaType = IMAGE_JPG;
                } else if (fileExtension.equals("jpeg")) {
                    mediaType = IMAGE_JPEG;
                } else if (fileExtension.equals("mp4")) {
                    mediaType = VIDEO_MP4;
                }

            }

            RequestBody requestBody = RequestBody.create(mediaType, f);
            multipartBuilder.addFormDataPart("file[]", f.getName(), requestBody);
        }


        Request request = new Request.Builder()
                .url(url)
                .post(multipartBuilder.build())
                .build();

        try {
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                JSONObject responseData = new JSONObject(response.body().string()).getJSONObject("data");

                if (responseData != null) {
                    return responseData.getJSONArray(PARAM_RESPONSE_URLS).toString();
                }
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }


        return null;
    }

    /**
     * Reads a specific server's configuration. Contains values that might differ between
     * installations
     *
     * @param server The server model who's information will be updated
     */
    public void updateServerInfo(@NonNull Server server) {

        String apiVersion = "v2.1.0";
        String url = server.getAddr() + "/api/" + apiVersion + "/" + ENDPOINT_FILE_GET_CONFIG;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {

                JSONObject responseData = new JSONObject(response.body().string()).getJSONObject("data");

                if (responseData != null) {

                    // update our server info
                    server.setUploadMaxSize(responseData.getInt(PARAM_RESPONSE_MAX_UPL_SIZE));
                    server.setMaxFilesPerRequest(responseData.getInt(PARAM_RESPONSE_MAX_FILES_REQ));
                    server.setMaxInputVars(responseData.getInt(PARAM_RESPONSE_MAX_IN_VARS));
                    server.setRequestMaxSize(responseData.getInt(PARAM_RESPONSE_MAX_SIZE_REQ));

                }

            }


        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

    }

    // method stub
    public String createMultiPaste(@NonNull UserProfile user, @NonNull Server server, File[] files) {
        // TODO
        return null;
    }

    /**
     * Requests a user's upload history from a given server
     *
     * @param user   User profile who's history we want to load
     * @param server Server which holds the user profile
     * @return a list of uploads from the past
     */
    public ArrayList<Upload> loadUploadHistory(@NonNull UserProfile user, @NonNull Server server) {
        //TODO differentiate between multi paste and normal upload

        // TODO tidy this up :)
        String apiVersion = "v2.1.0";
        String url = server.getAddr() + "/api/" + apiVersion + "/" + ENDPOINT_FILE_HISTORY;

        RequestBody body = new FormBody.Builder()
                .add(PARAM_APIKEY, user.getApiKey())
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                JSONObject responseData = new JSONObject(response.body().string()).getJSONObject("data");

                if (responseData != null) {

                    //JSONArray items = responseData.getJSONArray(PARAM_RESPONSE_HISTORY_ITEMS);
                    //JSONArray multipasteItems = responseData.getJSONArray(PARAM_RESPONSE_HISTORY_MULT_ITEMS);

                    JSONObject nestedJsonUploadItems = responseData.getJSONObject(PARAM_RESPONSE_HISTORY_ITEMS);

                    ArrayList<Upload> uploadHistory = new ArrayList<>();
                    for (JSONObject item : getJSONObjectsWithoutKey(nestedJsonUploadItems)) {
                        uploadHistory.add(gson.fromJson(item.toString(), Upload.class));
                    }

                    return uploadHistory;
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }


        return null;
    }

    // method stub
    public Boolean deleteUploads(PostInfo postInfo, ArrayList<Upload> uploads) {

        Server server = postInfo.getServer();
        UserProfile userProfile = postInfo.getUserProfile();

        // TODO tidy this up :)
        String apiVersion = "v2.1.0";
        String url = server.getAddr() + "/api/" + apiVersion + "/" + ENDPOINT_FILE_DELETE;

        // build our request body
        FormBody.Builder requestBuilder = new FormBody.Builder()
                .add(PARAM_APIKEY, userProfile.getApiKey());

        int index = 0;

        for (Upload upload : uploads) {
            requestBuilder.add("ids[" + index + "]", upload.getId());
            index++;

        }

        FormBody formBody = requestBuilder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        try {

            Response response = client.newCall(request).execute();

            // TODO add some deeper logic do determine successful deletion
            return response.isSuccessful();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    // region helper -------------------------------------------------------------------------------

    /**
     * Parses a json object from the response data.
     *
     * @param response Received response model
     * @return JSONObject with the responses data
     */
    private @Nullable
    JSONObject getResponseData(Response response) {

        // TODO null checks
        try {
            return new JSONObject(response.body().string()).getJSONObject("data");
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Tries to parse and return the file extension
     *
     * @param filePath
     * @return
     */
    private @Nullable
    String getFileExtension(String filePath) {

        //TODO we should take more edge cases into account (e.g. file has no extension)
        int i = filePath.lastIndexOf('.');

        // we assume our file has an extension
        return filePath.substring(i + 1);

    }

    /**
     * helper method to retrieve json objects nested in another json object without knowing it's key
     * see https://stackoverflow.com/questions/16646346/get-json-object-without-param-name-in-android
     *
     * @param items the json object containing the nested items with unknown keys
     * @return a list of nested json objects, only on first level
     * @throws JSONException
     */
    private ArrayList<JSONObject> getJSONObjectsWithoutKey(JSONObject items) throws JSONException {

        ArrayList<JSONObject> jsonObjects = new ArrayList<>();

        //get list of keys
        Iterator<String> keys = items.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            jsonObjects.add(items.getJSONObject(key));
        }

        return jsonObjects;

    }
    //endregion
}
