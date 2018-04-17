package de.michael.filebinmobile.model;

import java.util.ArrayList;
import java.util.Objects;

public class Server {

    private String addr, name;
    private int maxFilesPerRequest;
    private long uploadMaxSize, requestMaxSize, maxInputVars;
    private ArrayList<UserProfile> userProfiles = new ArrayList<>();

    public Server(String name, String address) {
        this.name = name;
        this.addr = address;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxFilesPerRequest() {
        return maxFilesPerRequest;
    }

    public void setMaxFilesPerRequest(int maxFilesPerRequest) {
        this.maxFilesPerRequest = maxFilesPerRequest;
    }

    public long getUploadMaxSize() {
        return uploadMaxSize;
    }

    public void setUploadMaxSize(long uploadMaxSize) {
        this.uploadMaxSize = uploadMaxSize;
    }

    public long getRequestMaxSize() {
        return requestMaxSize;
    }

    public void setRequestMaxSize(long requestMaxSize) {
        this.requestMaxSize = requestMaxSize;
    }

    public long getMaxInputVars() {
        return maxInputVars;
    }

    public void setMaxInputVars(long maxInputVars) {
        this.maxInputVars = maxInputVars;
    }

    public ArrayList<UserProfile> getUserProfiles() {
        return userProfiles;
    }

    public void addUserProfile(UserProfile profile) {
        if (!this.userProfiles.contains(profile)) {
            this.userProfiles.add(profile);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Server server = (Server) o;
        return Objects.equals(addr, server.addr) &&
                Objects.equals(name, server.name) &&
                Objects.equals(userProfiles, server.userProfiles);
    }

    @Override
    public int hashCode() {

        return Objects.hash(addr, name, userProfiles);
    }
}
