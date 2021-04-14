package com.buguagaoshu.homework.evaluation.utils;

import com.buguagaoshu.homework.evaluation.config.MinIOConfigProperties;
import io.minio.MinioClient;
import org.apache.commons.io.IOUtils;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.lionsoul.ip2region.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.*;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2019-08-13 13:29
 */
public class IpUtil {
    private static final Logger logger = LoggerFactory.getLogger(IpUtil.class);

    private final MinioClient minioClient;

    private final MinIOConfigProperties properties;

    private static DbSearcher searcher = null;


    public IpUtil(MinioClient minioClient, MinIOConfigProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;

        try {
            // 获取ip库路径
            InputStream stream = minioClient.getObject(this.properties.getBucketName(), "/ip2region/ip2region.db");
            byte[] bytes = IOUtils.toByteArray(stream);
            searcher = new DbSearcher(new DbConfig(), bytes);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }


    public String getCity(String ip) {
        if (!Util.isIpAddress(ip)) {
            logger.error("Error: Invalid ip address,{} 不是IP地址！ ", ip);
            return "";
        }

        if (searcher == null) {
            logger.error("ip2region 搜索数据库加载失败！");
            return "";
        }
        // 查询算法：B-tree、Binary、Memory
        try {
            Method method = searcher.getClass().getMethod("memorySearch", String.class);
            DataBlock dataBlock  = (DataBlock) method.invoke(searcher, ip);
            return dataBlock.getRegion();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * 获取用户登陆IP
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1")) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) {
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = "未知IP";
        }
        // ipAddress = this.getRequest().getRemoteAddr();
        return ipAddress;
    }

    public static String getUa(HttpServletRequest request) {
        String ua = request.getHeader("user-agent");
        if (StringUtils.isEmpty(ua)) {
            return "未知设备";
        }
        return ua;
    }
}
