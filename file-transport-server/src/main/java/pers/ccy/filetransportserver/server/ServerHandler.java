package pers.ccy.filetransportserver.server;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import pers.ccy.filetransportserver.controller.MainViewController;
import pers.ccy.filetransportserver.domain.Constants;
import pers.ccy.filetransportserver.domain.MyFile;
import pers.ccy.filetransportserver.domain.packet.*;
import pers.ccy.filetransportserver.utils.CacheUtil;
import pers.ccy.filetransportserver.utils.FileUtil;
import pers.ccy.filetransportserver.utils.MsgUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private TextArea console;
    private String baseUrl;
    private TableView view_files;
    private MainViewController control;
    private AnchorPane dir_pane;

    public ServerHandler(TextArea console, String baseUrl, TableView view_files, MainViewController control, AnchorPane dir_pane) {
        this.console = console;
        this.baseUrl = baseUrl;
        this.view_files = view_files;
        this.control = control;
        this.dir_pane = dir_pane;
    }

    /**
     * 当客户端主动连接服务端的连接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        console.appendText("\n===========================================\n");
        console.appendText("连接报告信息：有一客户端连接到本服务端。channelId：" + channel.id() + "\n");
        console.appendText("连接报告IP:" + channel.localAddress().getHostString() + "\n");
        console.appendText("连接报告Port:" + channel.localAddress().getPort()+"\n");
        console.appendText("连接报告完毕\n");
        console.appendText("===========================================\n");
    }

    /**
     * 当客户端主动断开服务端的连接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        console.appendText("客户端断开连接" + ctx.channel().localAddress().toString() + "\n");
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FileTransferProtocol)) return;

        FileTransferProtocol fileTransferProtocol = (FileTransferProtocol) msg;
        switch (fileTransferProtocol.getTransType()) {
            //发送文件请求
            case 0:
                FileInfoPacket fileInfoPacket = JSON.parseObject(fileTransferProtocol.getTransObj().toString(),FileInfoPacket.class);
                FileBurstInstructPacket oldPacket = CacheUtil.burstDataMap.get(fileInfoPacket.getFileName());
                if (oldPacket != null) {
                    if (oldPacket.getStatus() == Constants.FileStatus.COMPLETE) {
                        CacheUtil.burstDataMap.remove(fileInfoPacket.getFileName());
                    }
                    Platform.runLater(()->console.appendText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 接收客户端传输文件请求[断点续传]。" + JSON.toJSONString(oldPacket) + "\n"));
                    ctx.writeAndFlush(MsgUtil.buildTransferInstruct(oldPacket));
                    break;
                }
                FileTransferProtocol protocol = MsgUtil.buildTransferInstruct(Constants.FileStatus.BEGIN, fileInfoPacket.getFilePath(), 0, fileInfoPacket.getTargetPath());
                Platform.runLater(()->console.appendText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 接收客户端传输文件请求。" + JSON.toJSONString(fileInfoPacket) + "\n"));
                ctx.writeAndFlush(protocol);
                break;
            case 1:
                FileBurstInstructPacket fileBurstInstruct = JSON.parseObject(fileTransferProtocol.getTransObj().toString(),FileBurstInstructPacket.class);
                //Constants.FileStatus ｛0开始、1中间、2结尾、3完成｝
                if (Constants.FileStatus.COMPLETE == fileBurstInstruct.getStatus()) {
                    Platform.runLater(()->console.appendText("传输完成\n"));
                    break;
                }
                FileBurstDataPacket fileBurstData = FileUtil.readFile(fileBurstInstruct.getPath(), fileBurstInstruct.getPos(), fileBurstInstruct.getTargetPath());
                ctx.writeAndFlush(MsgUtil.buildTransferData(fileBurstData));
                Platform.runLater(()->console.appendText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 服务端传输文件信息。 FILE：" + fileBurstData.getFileName() + " SIZE(byte)：" + (fileBurstData.getEndPos() - fileBurstData.getBeginPos())+"\n"));
                break;
            //文件传输数据
            case 2:
                FileBurstDataPacket fileBurstDataReceive = JSON.parseObject(fileTransferProtocol.getTransObj().toString(), FileBurstDataPacket.class);
                FileBurstInstructPacket fileBurstInstructReceive = FileUtil.writeFile(fileBurstDataReceive);

                //保存断点续传信息
                CacheUtil.burstDataMap.put(fileBurstDataReceive.getFileName(), fileBurstInstructReceive);

                ctx.writeAndFlush(MsgUtil.buildTransferInstruct(fileBurstInstructReceive));
                Platform.runLater(()->console.appendText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 接收客户端传输文件数据。" + JSON.toJSONString(fileBurstDataReceive) + "\n"));

                //传输完成删除断点信息
                if (fileBurstInstructReceive.getStatus() == Constants.FileStatus.COMPLETE) {
                    CacheUtil.burstDataMap.remove(fileBurstDataReceive.getFileName());
                }
                break;
            //文件夹list请求
            case 3:
                FileListRequestPacket listPacket = JSON.parseObject(fileTransferProtocol.getTransObj().toString(), FileListRequestPacket.class);
                FileListPacket fileListPacket;
                if (listPacket.getDirPath().length() > 0){
                    fileListPacket = FileUtil.listFile(listPacket.getDirPath());
                }else {
                    fileListPacket = FileUtil.listFile(baseUrl);
                }
                Platform.runLater(()->console.appendText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 接收客户端查看文件列表请求" + JSON.toJSONString(listPacket) + "\n"));
                ctx.writeAndFlush(MsgUtil.buildDirList(fileListPacket));
                break;
            case 5:
                FileUploadPacket fileUploadPacket = JSON.parseObject(fileTransferProtocol.getTransObj().toString(), FileUploadPacket.class);
                File file = new File(fileUploadPacket.getFilePath());
                FileTransferProtocol packet = MsgUtil.buildRequestTransferFile(fileUploadPacket.getFilePath(), file.getName(), file.length(), fileUploadPacket.getTargetPath());
                ctx.writeAndFlush(packet);
                break;
            default:
                break;
        }
    }
}