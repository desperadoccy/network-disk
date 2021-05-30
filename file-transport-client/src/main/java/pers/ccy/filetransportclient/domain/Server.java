package pers.ccy.filetransportclient.domain;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.net.ServerSocket;

public class Server {
    private StringProperty serverName;
    private StringProperty userName;
    private StringProperty password;
    private StringProperty ip;
    private StringProperty port;

    public Server(){
        this(null,null,null,null,null);
    }

    public Server(String serverName, String userName, String password, String ip, String port){
        this.serverName = new SimpleStringProperty(serverName);
        this.userName = new SimpleStringProperty(userName);
        this.password = new SimpleStringProperty(password);
        this.ip = new SimpleStringProperty(ip);
        this.port = new SimpleStringProperty(port);
    }

    public StringProperty serverNameProperty(){
        return serverName;
    }

    public String getServerName() {
        return serverName.get();
    }

    public void setServerName(String serverName) {
        this.serverName.set(serverName);
    }

    public String getUserName() {
        return userName.get();
    }

    public void setUserName(String userName) {
        this.userName.set(userName);
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public String getIp() {
        return ip.get();
    }

    public void setIp(String ip) {
        this.ip.set(ip);
    }

    public String getPort() {
        return port.get();
    }

    public void setPort(String port) {
        this.port.set(port);
    }


    @Override
    public String toString() {
        return "Server{" +
                "serverName='" + serverName + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                '}';
    }
}
