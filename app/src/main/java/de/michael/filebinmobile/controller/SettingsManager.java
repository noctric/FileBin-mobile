package de.michael.filebinmobile.controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import de.michael.filebinmobile.model.PostInfo;
import de.michael.filebinmobile.model.Server;

public class SettingsManager {

    private static final SettingsManager INSTANCE = new SettingsManager();

    private static final String KEY_SERVER_LIST = "de.michael.filebin.SERVER_NAMES";
    private static final String KEY_SELECTED_POSTINFO = "de.michael.filebin.SELECTED_POSTINFO";

    public static SettingsManager getInstance() {
        return INSTANCE;
    }

    private SettingsManager() {
    }

    /**
     * Using a database management system to save servers and associated profiles would be a bit
     * of an overkill. This is because in probably 99.9% of the cases a user will not have more than
     * one account on 1-2 different servers. So for now we use shared preferences and serialization
     * as json strings to store our information privately on the device.
     *
     * @param server the server to be saved
     * @return true on success
     */
    public boolean addServer(Server server, Activity activity) {

        Gson gson = new Gson();
        String serializedServerInfo = gson.toJson(server);

        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Set<String> serializedServerSet = preferences.getStringSet(KEY_SERVER_LIST, null);

        // so apparently we have to create a new Set object to actually update sp
        HashSet<String> updatedServerSet;

        if (serializedServerSet == null) {
            // create a new set and add it
            updatedServerSet = new HashSet<>();
        } else {
            updatedServerSet = new HashSet<>(serializedServerSet);

        }

        updatedServerSet.add(serializedServerInfo);

        editor.putStringSet(KEY_SERVER_LIST, updatedServerSet);
        editor.apply();

        return true;
    }

    public ArrayList<Server> getServerList(Activity activity) {

        Gson gson = new Gson();

        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        Set<String> serverListSet = preferences.getStringSet(KEY_SERVER_LIST, null);

        ArrayList<Server> serverList = new ArrayList<>();

        if (serverListSet != null) {
            for (String serializedServerInfo : serverListSet) {
                Server server = gson.fromJson(serializedServerInfo, Server.class);
                serverList.add(server);
            }
        }

        return serverList;
    }

    public void deleteServer(Server server, Activity activity) {
        Gson gson = new Gson();
        String serializedServerInfo = gson.toJson(server);

        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        Set<String> serverListSet = preferences.getStringSet(KEY_SERVER_LIST, null);

        // so apparently we have to create a new Set object to actually update sp
        HashSet<String> updatedServerSet;

        if (serverListSet != null) {
            updatedServerSet = new HashSet<>(serverListSet);
            updatedServerSet.remove(serializedServerInfo);
        } else {
            updatedServerSet = new HashSet<>();
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(KEY_SERVER_LIST, updatedServerSet);

        editor.apply();
    }

    public void setPostInfo(@Nullable PostInfo postInfo, @NonNull Activity activity) {
        Gson gson = new Gson();
        String serializedPostInfo = gson.toJson(postInfo);

        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(KEY_SELECTED_POSTINFO, serializedPostInfo);
        editor.apply();
    }

    public @Nullable
    PostInfo getPostInfo(@NonNull Activity activity) {
        Gson gson = new Gson();

        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);

        String serializedPostInfo = preferences.getString(KEY_SELECTED_POSTINFO, null);

        return gson.fromJson(serializedPostInfo, PostInfo.class);

    }
}