package com.kael.bean;

import java.io.Serializable;

/**
 * Created by kael on 29/08/2016.
 */
public class Servidor implements Serializable{
    private int codServer;
    private String nameServer;
    private String host;
    private int port;
    private String user;
    private String password;
    private boolean SSLConnection;
    private boolean SSLCertificate;

    public int getCodServer() {
        return codServer;
    }

    public void setCodServer(int codServer) {
        this.codServer = codServer;
    }

    public String getNameServer() {
        return nameServer;
    }

    public void setNameServer(String nameServer) {
        this.nameServer = nameServer;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSSLConnection() {
        return SSLConnection;
    }

    public void setSSLConnection(boolean SSLConnection) {
        this.SSLConnection = SSLConnection;
    }

    public boolean isSSLCertificate() {
        return SSLCertificate;
    }

    public void setSSLCertificate(boolean SSLCertificate) {
        this.SSLCertificate = SSLCertificate;
    }

    @Override
    public String toString() {
        return nameServer;
    }
}
