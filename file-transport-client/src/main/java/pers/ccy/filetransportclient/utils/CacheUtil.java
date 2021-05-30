package pers.ccy.filetransportclient.utils;

import pers.ccy.filetransportclient.domain.packet.FileBurstInstructPacket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheUtil {
    public static Map<String, FileBurstInstructPacket> burstDataMap = new ConcurrentHashMap<>();
}
