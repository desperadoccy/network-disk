package pers.ccy.filetransportserver.utils;

import com.alibaba.fastjson.JSON;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SerializationUtil {

    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

    private static Objenesis objenesis = new ObjenesisStd();

    private SerializationUtil() {

    }

    public static <T> byte[] serialize(T object) {
        return JSON.toJSONString(object).getBytes(StandardCharsets.UTF_8);
    }

    public static <T> T deserialize(String string, Class<T> clz) {
        return JSON.parseObject(string, clz);
    }

}