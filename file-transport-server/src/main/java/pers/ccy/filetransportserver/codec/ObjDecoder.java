package pers.ccy.filetransportserver.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import pers.ccy.filetransportserver.utils.SerializationUtil;

import java.util.List;

public class ObjDecoder extends ByteToMessageDecoder {
    private Class<?> genericClass;

    public ObjDecoder(Class<?> genericClass){
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 8) return;
        in.markReaderIndex();
        int len = in.readInt();
        if (in.readableBytes() < len){
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[len];
        in.readBytes(data);
        out.add(SerializationUtil.deserialize(new String(data), genericClass));
    }
}
