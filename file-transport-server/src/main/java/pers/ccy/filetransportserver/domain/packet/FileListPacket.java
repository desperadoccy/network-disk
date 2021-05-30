package pers.ccy.filetransportserver.domain.packet;

import pers.ccy.filetransportserver.domain.MyFile;

import java.util.List;

public class FileListPacket  {
    List<MyFile> fileList;

    public FileListPacket(List<MyFile> fileList) {
        this.fileList = fileList;
    }

    public FileListPacket() {
    }

    public List<MyFile> getFileList() {
        return fileList;
    }

    public void setFileList(List<MyFile> fileList) {
        this.fileList = fileList;
    }
}
