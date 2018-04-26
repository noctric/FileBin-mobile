package de.michael.filebinmobile.model;

import android.support.annotation.Nullable;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostInfo)) return false;
        PostInfo postInfo = (PostInfo) o;
        return Objects.equals(getUserProfile(), postInfo.getUserProfile()) &&
                Objects.equals(getServer(), postInfo.getServer());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getUserProfile(), getServer());
    }
}
