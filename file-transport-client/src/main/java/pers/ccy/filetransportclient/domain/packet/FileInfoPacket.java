package pers.ccy.filetransportclient.domain.packet;

public class FileInfoPacket {
    private String filePath;
    private String fileName;
    private Long fileSize;
    private String targetPath;

    public FileInfoPacket(String filePath, String fileName, Long fileSize, String targetPath) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.targetPath = targetPath;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public FileInfoPacket() {
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
}
