package pers.ccy.filetransportserver.utils;

import pers.ccy.filetransportserver.domain.Constants;
import pers.ccy.filetransportserver.domain.packet.*;
import pers.ccy.filetransportserver.server.FileTransferProtocol;

public class MsgUtil {
    public static FileTransferProtocol buildRequestTransferFile(String fileUrl, String fileName, Long fileSize, String targetPath) {
        FileInfoPacket packet = new FileInfoPacket();
        packet.setFileName(fileName);
        packet.setFileSize(fileSize);
        packet.setFilePath(fileUrl);
        packet.setTargetPath(targetPath);
        FileTransferProtocol fileTransferProtocol = new FileTransferProtocol();
        fileTransferProtocol.setTransType(0);
        fileTransferProtocol.setTransObj(packet);
        return fileTransferProtocol;
    }

    public static FileTransferProtocol buildTransferInstruct(Integer status, String clientFileUrl, Integer readPosition, String targetPath) {
        FileBurstInstructPacket packet = new FileBurstInstructPacket();
        packet.setStatus(status);
        packet.setPath(clientFileUrl);
        packet.setPos(readPosition);
        packet.setTargetPath(targetPath);
        FileTransferProtocol fileTransferProtocol = new FileTransferProtocol();
        fileTransferProtocol.setTransType(Constants.TransferType.INSTRUCT); //0传输文件'请求'、1文件传输'指令'、2文件传输'数据'
        fileTransferProtocol.setTransObj(packet);
        return fileTransferProtocol;
    }

    public static FileTransferProtocol buildTransferInstruct(FileBurstInstructPacket fileBurstInstruct) {
        FileTransferProtocol fileTransferProtocol = new FileTransferProtocol();
        fileTransferProtocol.setTransType(Constants.TransferType.INSTRUCT);  //0传输文件'请求'、1文件传输'指令'、2文件传输'数据'
        fileTransferProtocol.setTransObj(fileBurstInstruct);
        return fileTransferProtocol;
    }

    public static FileTransferProtocol buildTransferData(FileBurstDataPacket fileBurstData) {
        FileTransferProtocol fileTransferProtocol = new FileTransferProtocol();
        fileTransferProtocol.setTransType(Constants.TransferType.DATA); //0传输文件'请求'、1文件传输'指令'、2文件传输'数据'
        fileTransferProtocol.setTransObj(fileBurstData);
        return fileTransferProtocol;
    }

    public static FileTransferProtocol buildDirList(FileListPacket fileListPacket) {
        FileTransferProtocol fileTransferProtocol = new FileTransferProtocol();
        fileTransferProtocol.setTransType(Constants.TransferType.LIST_RES);
        fileTransferProtocol.setTransObj(fileListPacket);
        return fileTransferProtocol;
    }

    public static FileTransferProtocol buildDirListRequest(String path) {
        FileTransferProtocol fileTransferProtocol = new FileTransferProtocol();
        fileTransferProtocol.setTransType(Constants.TransferType.LIST_REQ);
        fileTransferProtocol.setTransObj(new FileListRequestPacket(path));
        return fileTransferProtocol;
    }
}
