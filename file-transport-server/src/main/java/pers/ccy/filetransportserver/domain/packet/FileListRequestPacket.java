package pers.ccy.filetransportserver.domain.packet;

public class FileListRequestPacket {
    String dirPath;

    public FileListRequestPacket(String dirPath) {
        this.dirPath = dirPath;
    }

    public FileListRequestPacket() {
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }
}
