package pers.ccy.filetransportclient.domain;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.text.SimpleDateFormat;

public class MyFile {
    private StringProperty fileName;
    private StringProperty updateTime;
    private StringProperty size;
    private StringProperty type;
    private String url;

    public MyFile(String fileName, Long updateTime, Long size, String type, String url) {
        this.fileName = new SimpleStringProperty(fileName);
        this.updateTime = new SimpleStringProperty(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(updateTime));
        size = size % 1024 == 0 ? size / 1024 : size / 1024 + 1;
        this.size = new SimpleStringProperty(size.toString() + " kb");
        this.type = new SimpleStringProperty(type);
        this.url = url;
    }

    public MyFile() {
        this.fileName = new SimpleStringProperty();
        this.updateTime = new SimpleStringProperty();
        this.size = new SimpleStringProperty();
        this.type = new SimpleStringProperty();
    }

    public String getFileName() {
        return fileName.get();
    }

    public StringProperty fileNameProperty() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName.set(fileName);
    }

    public String getUpdateTime() {
        return updateTime.get();
    }

    public StringProperty updateTimeProperty() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime.set(updateTime);
    }

    public String getSize() {
        return size.get();
    }

    public StringProperty sizeProperty() {
        return size;
    }

    public void setSize(String size) {
        this.size.set(size);
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
