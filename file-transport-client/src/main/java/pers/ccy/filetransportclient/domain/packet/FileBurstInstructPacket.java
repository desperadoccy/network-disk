package pers.ccy.filetransportclient.domain.packet;

public class FileBurstInstructPacket {
    private Integer status;
    private String path;
    private Integer pos;
    private String targetPath;

    public FileBurstInstructPacket(Integer status) {
        this.status = status;
    }

    public FileBurstInstructPacket() {
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public FileBurstInstructPacket(Integer status, String path, Integer pos, String targetPath) {
        this.status = status;
        this.path = path;
        this.pos = pos;
        this.targetPath = targetPath;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getPos() {
        return pos;
    }

    public void setPos(Integer pos) {
        this.pos = pos;
    }
}
