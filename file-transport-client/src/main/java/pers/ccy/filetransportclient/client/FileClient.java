package pers.ccy.filetransportclient.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import javafx.application.Platform;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import pers.ccy.filetransportclient.codec.ObjDecoder;
import pers.ccy.filetransportclient.codec.ObjEncoder;
import pers.ccy.filetransportclient.controller.MainViewController;
import pers.ccy.filetransportclient.domain.MyFile;
import pers.ccy.filetransportclient.utils.MsgUtil;

import java.io.File;

public class FileClient {
    private int port;
    private String ip;
    private TextArea console;
    private MainViewController controller;
    private TableView view_files;
    private AnchorPane dir_pane;
    private FileClient self = this;

    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Channel channel;
    private ChannelFuture future;

    public FileClient(String ip, int port, TextArea console, MainViewController controller, TableView view_files, AnchorPane dir_pane) {
        this.ip = ip;
        this.port = port;
        this.console = console;
        this.controller = controller;
        this.view_files = view_files;
        this.dir_pane = dir_pane;
    }

    public void run() {
        ChannelFuture channelFuture = null;
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.AUTO_READ, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(new ObjDecoder(FileTransferProtocol.class));
                    pipeline.addLast(new ObjEncoder(FileTransferProtocol.class));
                    pipeline.addLast(new ClientHandler(console, view_files, self, dir_pane, controller));
                }
            });
            channelFuture = b.connect(ip, port).syncUninterruptibly();
            this.channel = channelFuture.channel();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != channelFuture && channelFuture.isSuccess()) {
                Platform.runLater(()->console.appendText("连接成功\n"));
            } else {
                console.appendText("连接失败\n");
            }
        }
        this.future = channelFuture;
    }

    public static void changeDir(String path) {
        System.out.println(path);
    }

    public void destroy() {
        if (null == channel) return;
        channel.close();
        workerGroup.shutdownGracefully();
    }

    public void sendDirRequest(String path){
        FileTransferProtocol fileTransferProtocol = MsgUtil.buildDirListRequest(path);
        future.channel().writeAndFlush(fileTransferProtocol);
    }

    public void upload(File file, String baseUrl){
        System.out.println(baseUrl);
        FileTransferProtocol fileTransferProtocol = MsgUtil.buildRequestTransferFile(file.getAbsolutePath(), file.getName(), file.length(), baseUrl);
        future.channel().writeAndFlush(fileTransferProtocol);
    }

    public void download(MyFile file, String baseUrl){
        System.out.println(baseUrl);
        FileTransferProtocol fileTransferProtocol = MsgUtil.buildDownloadRequest(file.getUrl(), file.getFileName(), baseUrl);
        future.channel().writeAndFlush(fileTransferProtocol);
    }
}
