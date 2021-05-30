package pers.ccy.filetransportclient.utils;


import pers.ccy.filetransportclient.domain.Constants;
import pers.ccy.filetransportclient.domain.MyFile;
import pers.ccy.filetransportclient.domain.packet.FileBurstDataPacket;
import pers.ccy.filetransportclient.domain.packet.FileBurstInstructPacket;
import pers.ccy.filetransportclient.domain.packet.FileListPacket;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class FileUtil {

    public static FileBurstDataPacket readFile(String fileUrl, Integer readPosition, String targetPath) throws IOException {
        File file = new File(fileUrl);
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");//r: 只读模式 rw:读写模式
        randomAccessFile.seek(readPosition);
        byte[] bytes = new byte[256];
        int readSize = randomAccessFile.read(bytes);
        if (readSize <= 0) {
            randomAccessFile.close();
            return new FileBurstDataPacket(Constants.FileStatus.COMPLETE);//Constants.FileStatus ｛0开始、1中间、2结尾、3完成｝
        }
        Integer checkNum = 0;
        for (int i = 0; i < bytes.length; i++) {
            checkNum = (checkNum + bytes[i]) % 256;
        }
        FileBurstDataPacket fileInfo = new FileBurstDataPacket();
        fileInfo.setPath(fileUrl);
        fileInfo.setFileName(file.getName());
        fileInfo.setBeginPos(readPosition);
        fileInfo.setEndPos(readPosition + readSize);
        fileInfo.setCheckNum(checkNum);
        fileInfo.setTargetPath(targetPath);
        if (readSize < 256) {
            byte[] copy = new byte[readSize];
            System.arraycopy(bytes, 0, copy, 0, readSize);
            fileInfo.setBytes(copy);
            fileInfo.setStatus(Constants.FileStatus.END);
        } else {
            fileInfo.setBytes(bytes);
            fileInfo.setStatus(Constants.FileStatus.CENTER);
        }
        randomAccessFile.close();
        return fileInfo;
    }

    public static FileBurstInstructPacket writeFile(FileBurstDataPacket fileBurstData) throws IOException {

        if (Constants.FileStatus.COMPLETE == fileBurstData.getStatus()) {
            return new FileBurstInstructPacket(Constants.FileStatus.COMPLETE); //Constants.FileStatus ｛0开始、1中间、2结尾、3完成｝
        }

        byte[] bytes = fileBurstData.getBytes();
//        Integer checkNum = 0;
//        for (int i = 0; i < bytes.length; i++) {
//            checkNum = (checkNum + bytes[i]) % 256;
//        }
        File file = new File(fileBurstData.getTargetPath()+"/"+fileBurstData.getFileName());
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");//r: 只读模式 rw:读写模式
        randomAccessFile.seek(fileBurstData.getBeginPos());      //移动文件记录指针的位置,
        randomAccessFile.write(bytes);        //调用了seek（start）方法，是指把文件的记录指针定位到start字节的位置。也就是说程序将从start字节开始写数据
        randomAccessFile.close();

        if (Constants.FileStatus.END == fileBurstData.getStatus()) {
            return new FileBurstInstructPacket(Constants.FileStatus.COMPLETE); //Constants.FileStatus ｛0开始、1中间、2结尾、3完成｝
        }

        //文件分片传输指令
        FileBurstInstructPacket fileBurstInstruct = new FileBurstInstructPacket();
        fileBurstInstruct.setStatus(Constants.FileStatus.CENTER);            //Constants.FileStatus ｛0开始、1中间、2结尾、3完成｝
        fileBurstInstruct.setPath(fileBurstData.getPath());      //客户端文件URL
        fileBurstInstruct.setPos(fileBurstData.getEndPos() + 1);    //读取位置
        fileBurstInstruct.setTargetPath(fileBurstData.getTargetPath());

        return fileBurstInstruct;
    }

    public static FileListPacket listFile(String path) {
        File fileFolder = new File(path);
        ArrayList<MyFile> myFiles = new ArrayList<>();
        File[] files = fileFolder.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                myFiles.add(new MyFile(files[i].getName(), files[i].lastModified(), files[i].length(), "file", files[i].getAbsolutePath()));
            }
            if (files[i].isDirectory()) {
                myFiles.add(new MyFile(files[i].getName(), files[i].lastModified(), files[i].length(), "dir", files[i].getAbsolutePath()));
            }
        }
        FileListPacket fileListPacket = new FileListPacket();
        fileListPacket.setFileList(myFiles);
        return fileListPacket;
    }
}