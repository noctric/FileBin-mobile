package de.michael.filebinmobile.controller;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.michael.filebinmobile.model.Server;
import de.michael.filebinmobile.model.Upload;
import de.michael.filebinmobile.model.UserProfile;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkManager {

    private static final NetworkManager INSTANCE = new NetworkManager();

    // Documentation available at https://github.com/Bluewind/filebin/blob/master/doc/api/

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
    private static final String PARAM_USER_ACCESS_LEVEL = "access_level";
    private static final String PARAM_USER_COMMENT = "comment";

    private static final String PARAM_RESPONSE_NEW_API_KEY = "new_key";
    private static final String PARAM_RESPONSE_URLS = "urls";
    private static final String PARAM_RESPONSE_IDS = "ids";


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

    private OkHttpClient client = new OkHttpClient();


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
                JSONObject responseData = getResponseData(response);

                if (responseData != null) {
                    return responseData.getString(PARAM_RESPONSE_NEW_API_KEY);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    // method stub
    public String pasteUploadFile(@NonNull UserProfile user, @NonNull Server server, File file) {

        String apiVersion = "v2.1.0";
        String url = server.getAddr() + "/api/" + apiVersion + "/" + ENDPOINT_FILE_UPLOAD;

        String fileExtension = getFileExtension(file.getAbsolutePath());
        MediaType mediaType = null;

        // necessary for later, leave it for now
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

        RequestBody requestBody = RequestBody.create(mediaType, file);

        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                JSONObject responseData = getResponseData(response);

                if (responseData != null) {
                    return responseData.getJSONArray(PARAM_RESPONSE_IDS).toString();
                }
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }


        return null;
    }

    public String createMultiPaste(@NonNull UserProfile user, @NonNull Server server, File[] files) {
        // TODO
        return null;
    }

    // method stub
    public ArrayList<Upload> loadUploadHistory(@NonNull UserProfile user, @NonNull Server server) {
        //TODO
        return null;
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
        String[] pathAndExt = filePath.split("\\.");
        int l = pathAndExt.length;

        if (l > 1) {
            return pathAndExt[l - 1];
        }

        return null;
    }
    //endregion
}
