package pers.ccy.filetransportserver.utils;

import pers.ccy.filetransportserver.domain.packet.FileBurstInstructPacket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheUtil {
    public static Map<String, FileBurstInstructPacket> burstDataMap = new ConcurrentHashMap<>();
}
