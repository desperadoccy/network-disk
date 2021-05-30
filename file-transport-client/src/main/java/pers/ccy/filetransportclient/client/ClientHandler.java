package pers.ccy.filetransportclient.client;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import pers.ccy.filetransportclient.controller.MainViewController;
import pers.ccy.filetransportclient.domain.Constants;
import pers.ccy.filetransportclient.domain.MyFile;
import pers.ccy.filetransportclient.domain.packet.FileBurstDataPacket;
import pers.ccy.filetransportclient.domain.packet.FileBurstInstructPacket;
import pers.ccy.filetransportclient.domain.packet.FileInfoPacket;
import pers.ccy.filetransportclient.domain.packet.FileListPacket;
import pers.ccy.filetransportclient.utils.CacheUtil;
import pers.ccy.filetransportclient.utils.FileUtil;
import pers.ccy.filetransportclient.utils.MsgUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private TextArea console;
    private TableView view_files;
    private FileClient client;
    private AnchorPane dir_pane;
    private MainViewController control;

    public ClientHandler(TextArea console, TableView view_files, FileClient client, AnchorPane dir_pane, MainViewController control) {
        this.console = console;
        this.view_files = view_files;
        this.client = client;
        this.dir_pane = dir_pane;
        this.control = control;
    }

    /**
     * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        console.appendText("\n===========================================\n");
        console.appendText("连接报告信息：本客户端连接到服务端。channelId：" + channel.id() + "\n");
        console.appendText("连接报告IP:" + channel.localAddress().getHostString() + "\n");
        console.appendText("连接报告Port:" + channel.localAddress().getPort()+"\n");
        console.appendText("连接报告完毕\n");
        console.appendText("===========================================\n");
    }

    /**
     * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        console.appendText("断开连接" + ctx.channel().localAddress().toString() + "\n");
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //数据格式验证
        if (!(msg instanceof FileTransferProtocol)) return;

        FileTransferProtocol fileTransferProtocol = (FileTransferProtocol) msg;
        //0传输文件'请求'、1文件传输'指令'、2文件传输'数据'
        switch (fileTransferProtocol.getTransType()) {
            //发送文件请求
            case 0:
                FileInfoPacket fileInfoPacket = JSON.parseObject(fileTransferProtocol.getTransObj().toString(),FileInfoPacket.class);
                FileBurstInstructPacket oldPacket = CacheUtil.burstDataMap.get(fileInfoPacket.getFileName());
                if (oldPacket != null) {
                    if (oldPacket.getStatus() == Constants.FileStatus.COMPLETE) {
                        CacheUtil.burstDataMap.remove(fileInfoPacket.getFileName());
                    }
                    Platform.runLater(()->console.appendText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 接收服务端发送文件请求[断点续传]。" + JSON.toJSONString(oldPacket) + "\n"));
                    ctx.writeAndFlush(MsgUtil.buildTransferInstruct(oldPacket));
                    break;
                }
                FileTransferProtocol protocol = MsgUtil.buildTransferInstruct(Constants.FileStatus.BEGIN, fileInfoPacket.getFilePath(), 0, fileInfoPacket.getTargetPath());
                Platform.runLater(()->console.appendText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 接收服务端发送文件请求。" + JSON.toJSONString(fileInfoPacket) + "\n"));
                ctx.writeAndFlush(protocol);
                break;
            case 1:
                FileBurstInstructPacket fileBurstInstruct = JSON.parseObject(fileTransferProtocol.getTransObj().toString(),FileBurstInstructPacket.class);
                //Constants.FileStatus ｛0开始、1中间、2结尾、3完成｝
                if (Constants.FileStatus.COMPLETE == fileBurstInstruct.getStatus()) {
                    Button node = (Button)dir_pane.getChildren().get(dir_pane.getChildren().size() - 1);
                    if (node.getText() == "~/")
                        client.sendDirRequest("");
                    else
                        client.sendDirRequest(node.getId());
                    Platform.runLater(()->console.appendText("传输完成\n"));
                    break;
                }
                FileBurstDataPacket fileBurstData = FileUtil.readFile(fileBurstInstruct.getPath(), fileBurstInstruct.getPos(), fileBurstInstruct.getTargetPath());
                ctx.writeAndFlush(MsgUtil.buildTransferData(fileBurstData));
                Platform.runLater(()->console.appendText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 客户端传输文件信息。 FILE：" + fileBurstData.getFileName() + " SIZE(byte)：" + (fileBurstData.getEndPos() - fileBurstData.getBeginPos())+"\n"));
                break;
            //文件传输数据
            case 2:
                FileBurstDataPacket fileBurstDataReceive = JSON.parseObject(fileTransferProtocol.getTransObj().toString(), FileBurstDataPacket.class);
                FileBurstInstructPacket fileBurstInstructReceive = FileUtil.writeFile(fileBurstDataReceive);

                //保存断点续传信息
                CacheUtil.burstDataMap.put(fileBurstDataReceive.getFileName(), fileBurstInstructReceive);

                ctx.writeAndFlush(MsgUtil.buildTransferInstruct(fileBurstInstructReceive));
                Platform.runLater(()->console.appendText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 接收服务端传输文件数据。" + JSON.toJSONString(fileBurstDataReceive) + "\n"));

                //传输完成删除断点信息
                if (fileBurstInstructReceive.getStatus() == Constants.FileStatus.COMPLETE) {
                    CacheUtil.burstDataMap.remove(fileBurstDataReceive.getFileName());
                }
                break;
            case 4:
                FileListPacket fileListPacket = JSON.parseObject(fileTransferProtocol.getTransObj().toString(), FileListPacket.class);
                Platform.runLater(()->view_files.setItems(FXCollections.observableArrayList(fileListPacket.getFileList())));
                break;
            default:
                break;
        }
    }
}
