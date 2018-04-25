package de.michael.filebinmobile.model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserProfile)) return false;
        UserProfile that = (UserProfile) o;
        return Objects.equals(getUsrName(), that.getUsrName()) &&
                Objects.equals(getApiKey(), that.getApiKey());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getUsrName(), getApiKey());
    }
}
