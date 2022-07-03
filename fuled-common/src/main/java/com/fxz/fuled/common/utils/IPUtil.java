package com.fxz.fuled.common.utils;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * IPUtil
 */
public class IPUtil {
    public static List<String> getIpAddress() {
        List<String> list = new LinkedList<>();
        try {
            Enumeration enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface network = (NetworkInterface) enumeration.nextElement();
                if (network.isVirtual() || !network.isUp()) {
                    continue;
                } else {
                    Enumeration addresses = network.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress address = (InetAddress) addresses.nextElement();
                        if (address != null && (address instanceof Inet4Address || address instanceof Inet6Address)) {
                            list.add(address.getHostAddress());
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        list.remove("127.0.0.1");
        list.remove("::1");
        list.remove("0:0:0:0:0:0:0:1");
        return list;
    }
}
