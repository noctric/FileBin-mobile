package de.michael.filebinmobile.controller;

import java.io.File;

import de.michael.filebinmobile.model.Server;
import de.michael.filebinmobile.model.UserProfile;

public class NetworkManager {

    private static final NetworkManager INSTANCE = new NetworkManager();

    private NetworkManager() {
    }

    public static NetworkManager getInstance() {
        return INSTANCE;
    }

    //method stub
    public String generateApiKey(String username, String password, String serverAddr) {
        // TODO
        return null;
    }

    // method stub
    public String pasteUpload(UserProfile user, Server server, File[] files) {
        // TODO
        return null;
    }
}
