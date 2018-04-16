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

    // params
    private static final String PARAM_USER_NAME = "username";
    private static final String PARAM_USER_PW = "password";
    private static final String PARAM_USER_ACCESS_LEVEL = "access_level";
    private static final String PARAM_USER_COMMENT = "comment";

    private static final String PARAM_RESPONSE_NEW_API_KEY = "new_key";


    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient();


    private NetworkManager() {
    }

    public static NetworkManager getInstance() {
        return INSTANCE;
    }

    /**
     * Makes a request to generate a new api key from the server.
     * @param username Required param.
     * @param password Required param.
     * @param serverAddr Required param.
     * @return A newly created api key or null if not successful.
     */
    public @Nullable String generateApiKey(@NonNull String username, @NonNull String password, @NonNull String serverAddr) {

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
    public String pasteUpload(@NonNull UserProfile user, @NonNull Server server, File[] files) {
        // TODO
        return null;
    }

    // method stub
    public ArrayList<Upload> loadUploadHistory(@NonNull UserProfile user, @NonNull Server server) {
        //TODO
        return null;
    }

    // helper
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
}
