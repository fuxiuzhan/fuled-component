package com.fxz.fuled.common.utils;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * IPUtil
 */
public class IPUtil {

    public static final Pattern IPV4_REGEX =
            Pattern.compile(
                    "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");

    public static List<String> getIpAddress() {
        List<String> list = new LinkedList<>();
        try {
            Enumeration enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface network = (NetworkInterface) enumeration.nextElement();
                if (network.isVirtual() || !network.isUp()) {
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

    /**
     *
     * @param ip
     * @return
     */
    public static boolean isIpv4(String ip) {
        return IPV4_REGEX.matcher(ip).matches();
    }
}
