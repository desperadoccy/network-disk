package pers.ccy.filetransportclient.domain.packet;

public class FileListRequestPacket {
    String dirPath;

    public FileListRequestPacket() {
    }

    public FileListRequestPacket(String dirPath) {
        this.dirPath = dirPath;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    @Override
    public String toString() {
        return "FileListRequestPacket{" +
                "dirPath='" + dirPath + '\'' +
                '}';
    }
}
