package pers.ccy.filetransportclient.domain.packet;

public class FileBurstDataPacket {
    private String path;
    private String targetPath;
    private String fileName;
    private Integer beginPos;
    private Integer endPos;
    private byte[] bytes;
    private Integer status;
    private Integer checkNum;

    public FileBurstDataPacket() {
    }

    public FileBurstDataPacket(String path, String targetPath, String fileName, Integer beginPos, Integer endPos, byte[] bytes, Integer status, Integer checkNum) {
        this.path = path;
        this.targetPath = targetPath;
        this.fileName = fileName;
        this.beginPos = beginPos;
        this.endPos = endPos;
        this.bytes = bytes;
        this.status = status;
        this.checkNum = checkNum;
    }

    public FileBurstDataPacket(Integer status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getBeginPos() {
        return beginPos;
    }

    public void setBeginPos(Integer beginPos) {
        this.beginPos = beginPos;
    }

    public Integer getEndPos() {
        return endPos;
    }

    public void setEndPos(Integer endPos) {
        this.endPos = endPos;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCheckNum() {
        return checkNum;
    }

    public void setCheckNum(Integer checkNum) {
        this.checkNum = checkNum;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }
}
