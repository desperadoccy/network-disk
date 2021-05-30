package pers.ccy.filetransportserver.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import javafx.scene.control.Control;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import pers.ccy.filetransportserver.codec.ObjDecoder;
import pers.ccy.filetransportserver.codec.ObjEncoder;
import pers.ccy.filetransportserver.controller.MainViewController;

public class FileServer {
    private int port;
    private TextArea console;
    private MainViewController controller;
    public static String baseUrl;
    private TableView view_files;
    private AnchorPane dir_pane;

    //配置服务端NIO线程组
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Channel channel;
    private ChannelFuture future;

    public FileServer(int port, TextArea console, MainViewController controller, String baseUrl, TableView view_files, AnchorPane dir_pane) {
        this.port = port;
        this.console = console;
        this.controller = controller;
        this.baseUrl = baseUrl;
        this.view_files = view_files;
        this.dir_pane = dir_pane;
    }

    public void run(){
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        pipeline.addLast(new ObjDecoder(FileTransferProtocol.class));
                        pipeline.addLast(new ObjEncoder(FileTransferProtocol.class));
                        pipeline.addLast(new ServerHandler(console, baseUrl, view_files, controller, dir_pane));
                    }
                });
        ChannelFuture future = bootstrap.bind(port).syncUninterruptibly();
        this.future = future;
        this.channel = future.channel();
        if (future != null && future.isSuccess()){
            console.appendText("端口绑定成功\n");
        }else{
            console.appendText("端口被占用\n");
        }
    }

    public void destroy() {
        if (null == channel) return;
        channel.close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
