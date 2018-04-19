package de.michael.filebinmobile.model;

import android.support.annotation.Nullable;

/**
 * Wrapper class serving as a union of both server and user information, needed to make requests
 * to a remote filebin installation
 */
public class PostInfo {

    private UserProfile userProfile;
    private Server server;

    public PostInfo(@Nullable UserProfile userProfile, Server server) {
        this.userProfile = userProfile;
        this.server = server;
    }

    public @Nullable
    UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }
}
