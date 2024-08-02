import org.pcap4j.packet.IpV4Rfc791Tos;
import org.pcap4j.packet.namednumber.IpNumber;
import org.pcap4j.packet.namednumber.IpVersion;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;


public class WebRequestSimulation {

    public static void main(String[] args){
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        try{
            // TODO 浏览器会先解析 URL，解析出域名、资源路径、端口等信息，然后构造 HTTP 请求报文。
            String urlString = "http://www.baidu.com/";
            // 解析URL
            // 使用URL类来解析输入的URL字符串
            URL url = new URL(urlString);
            String protocol = url.getProtocol(); // 获取协议，如http或https
            String host = url.getHost(); // 获取主机名（域名）
            int port = url.getPort() == -1 ? url.getDefaultPort() : url.getPort(); // 获取端口号，如果未指定则使用默认端口
            String path = url.getPath().isEmpty() ? "/" : url.getPath(); // 获取资源路径，如果为空则设置为根路径"/"
            String query = url.getQuery(); // 获取查询字符串
            // 打印解析出的URL信息
            System.out.println("Protocol: " + protocol);
            System.out.println("Host: " + host);
            System.out.println("Port: " + port);
            System.out.println("Path: " + path);
            System.out.println("Query: " + query);


            // TODO 先查缓存 -> host文件 -> 本地域名服务器 -> 根域名服务器   -> 目标服务器
            //                                         -> 顶级域名服务器
            //                                         -> 权威域名服务器
            InetAddress inetAddress = InetAddress.getByName(host);
            String ip = inetAddress.getHostAddress();
            System.out.println("ip = " + ip);


            // TODO 构造HTTP请求报文 添加http数据
//            StringBuilder httpRequest = new StringBuilder();
            ETHERDATA httpRequest = new ETHERDATA();
            httpRequest.iphead = new IPHEAD();
            httpRequest.iphead.tcphead = new TCPHEAD();
            httpRequest.iphead.tcphead.httpData = new HTTPDATA();

            StringBuilder createdHttpData = new StringBuilder();
            createdHttpData.append("GET ").append(path);
            if (query != null) {
                createdHttpData.append("?").append(query); // 如果有查询字符串，添加到请求行
            }
            createdHttpData.append(" HTTP/1.1\r\n");
// 构造请求头
            createdHttpData.append("Host: ").append(host).append("\r\n");
            createdHttpData.append("Connection: Close\r\n");
            httpRequest.iphead.tcphead.httpData.httpData = createdHttpData;

            // TODO 构造TCP头部
            StringBuilder createdTCPData = new StringBuilder();
            createdTCPData.append("srcInetAddress:localhost\n");
            createdTCPData.append("dstInetAddress:127.0.0.1:80\n");
            createdTCPData.append("srcPort:8080\n");
            createdTCPData.append("dstPort:80\n");
            createdTCPData.append("correctChecksumAtBuild(true)\n" +
                    ".correctLengthAtBuild(true);");
            httpRequest.iphead.tcphead.tcpData = createdTCPData;
            // TODO 构建IP头部
            StringBuilder createdIPData = new StringBuilder();
            createdIPData.append("IpVersion.IPV4 = " + IpVersion.IPV4);
            createdIPData.append("IpV4Rfc791Tos.newInstance((byte) 0) = " + IpV4Rfc791Tos.newInstance((byte) 0));
            createdIPData.append("ttl = " + 64);
            createdIPData.append("IpNumber.TCP = " + IpNumber.TCP);
            createdIPData.append("                     .srcAddr((Inet4Address) srcInetAddress)\n" +
                    "                     .dstAddr((Inet4Address) dstInetAddress)\n" +
                    "                     .payloadBuilder(tcpBuilder)\n" +
                    "                     .correctChecksumAtBuild(true)\n" +
                    "                     .correctLengthAtBuild(true);");


            System.out.println();
            // 输出HTTP请求报文
            System.out.println("HTTP Request:");
            System.out.println(httpRequest.toString());

            // TODO Socket会自动进行三次握手
            socket = new Socket(ip, port);
            // 发送HTTP请求
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // TODO 将HTTP请求报文发送到服务器
            out.print(httpRequest.toString());
            out.flush(); // 确保所有数据都已发送



            // TODO 接收HTTP响应
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine).append("\n"); // 逐行读取并存储响应内容
            }

            // 输出HTTP响应
            System.out.println("HTTP Response:");
            System.out.println(response.toString()); // 打印响应内容


            in.close();
            out.close();
            socket.close();
        }catch (IOException e) {
            e.printStackTrace(); // 捕获并打印IO异常
        }






    }
    public static class HTTPDATA{
        StringBuilder httpData;

        @Override
        public String toString() {
            return "HTTPDATA{" +
                    "httpData=" + httpData +
                    '}';
        }
    }
    public static class TCPHEAD{
        HTTPDATA httpData;
        StringBuilder tcpData;

        @Override
        public String toString() {
            return "TCPHEAD{" +
                    "httpData=" + httpData +
                    ", tcpData=" + tcpData +
                    '}';
        }
    }
    public static class IPHEAD{
        TCPHEAD tcphead;
        StringBuilder ipData;

        @Override
        public String toString() {
            return "IPHEAD{" +
                    "tcphead=" + tcphead +
                    ", ipData=" + ipData +
                    '}';
        }
    }

    public static class ETHERDATA{
        IPHEAD iphead;
        StringBuilder etherData;

        @Override
        public String toString() {
            return "ETHERDATA{" +
                    "iphead=" + iphead +
                    ", etherData=" + etherData +
                    '}';
        }
    }

}
