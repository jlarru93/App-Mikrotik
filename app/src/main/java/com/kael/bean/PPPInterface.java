package com.kael.bean;

/**
 * Created by kael on 24/08/2016.
 */
public class PPPInterface {
    private String id;
    private String remoteAdress;
    private String name;
    private String uptime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRemoteAdress() {
        return remoteAdress;
    }

    public void setRemoteAdress(String remoteAdress) {
        this.remoteAdress = remoteAdress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }
}
