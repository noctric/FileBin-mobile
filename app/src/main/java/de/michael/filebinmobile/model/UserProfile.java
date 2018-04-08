package de.michael.filebinmobile.model;

public class UserProfile {

    private String usrName, apiKey;

    public UserProfile(String usrName, String apiKey) {
        this.usrName = usrName;
        this.apiKey = apiKey;
    }

    public String getUsrName() {
        return usrName;
    }

    public String getApiKey() {
        return apiKey;
    }
}
