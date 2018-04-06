package de.michael.filebinmobile.model;

import java.util.ArrayList;

public class Server {

    private String addr, name;
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
}
