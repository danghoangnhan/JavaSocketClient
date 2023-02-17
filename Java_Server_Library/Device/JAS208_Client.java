package Device;

import Time_Component.Custom_DateTime;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class JAS208_Client extends JassDevice_Client implements clientConstants, Runnable {



    private static String logHeader = "$LOG40";
    private static String dataHeader = "$DATA1";
    private static String chkHeader = "$CHK1";

    public JAS208_Client(String deviceName,
                         int index,
                         List<String> packetList106,
                         int delayTime,
                         String targetIP,
                         String targetPort,
                         boolean fixDate) {
        this.setDeviceIndex(index);
        this.setDeviceName(deviceName);
        this.setIP(targetIP);
        this.setPORT(targetPort);
        this.setDelayTime(delayTime);
        this.setPacketList(packetList106);
        this.setFixDate(fixDate);
        currentIndex = new AtomicInteger(0);
    }

    public JAS208_Client() {

    }

    @Override
    public void run() {
        System.out.println(this.getDeviceName() + " started");
        sendPacket();
        System.out.println(this.getDeviceName() + " ended");
    }

    @Override
    public void sendPacket() {
        Socket client = new Socket();
        InetSocketAddress isa = new InetSocketAddress(this.getIP(), Integer.parseInt(this.getPORT()));
        try {
            client.connect(isa, 10000);
            BufferedOutputStream out = new BufferedOutputStream(client.getOutputStream());

            int startDeviceNameIndex =  StringUtils.ordinalIndexOf(this.getPacketList().get(0), ",", 2);
            int endDeviceNameIndex =    StringUtils.ordinalIndexOf(this.getPacketList().get(0), ",", 3);

            String originDeviceName = this.getPacketList().get(0).substring(startDeviceNameIndex + 1, endDeviceNameIndex);

            for (String originPacket : this.getPacketList()) {
                String packet = originPacket.replace(originDeviceName, this.getDeviceName()).concat("\r\n");
                out.write(packet.getBytes(StandardCharsets.UTF_8));
                this.getCurrentIndex().incrementAndGet();
                out.flush();
                Thread.sleep(this.getDelayTime());
            }
            client.close();
        } catch (IOException | InterruptedException e) {
            System.out.println("Socket連線有問題 !\n" +
                    this.getIP() + "\n" +
                    this.getPORT() + "\n" +
                    e.toString());
            System.out.println("IOException :" + e.toString());
        }
    }
    public static List<String>  readBinFile(String fileDir) {
            List<String> packetList = null;
            try {
                String content = new String(Files.readAllBytes(Paths.get(fileDir)),StandardCharsets.UTF_8);
                    packetList = Arrays.asList(content.split("\r\n"))
                            .stream()
                            .filter(packet -> packet.startsWith("$LOG"))
                            .map(filteredPacket -> filteredPacket.replace("$LOG,", logHeader.concat(",")))
                            .collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return packetList;
    }
    public static List<String> readBinFile(String fileDir,Custom_DateTime fixDate) {
        List<String> originPacketList = readBinFile(fileDir);
        List<String> result = new ArrayList<>();
        for (String packet:originPacketList){
            result.add(packet.replace(packet.substring(StringUtils.ordinalIndexOf(packet,",",5)+1,StringUtils.ordinalIndexOf(packet,",",7)), fixDate.getEncodedDate()));
            fixDate = fixDate.AddSecond(30);
        }
        return result;
    }
}