package Device;

import Time_Component.Custom_DateTime;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import test.DeviceData_RawTest;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;



public class JAS106_Client extends JassDevice_Client implements clientConstants, Runnable {
    private static final Log logger = LogFactory.getLog(DeviceData_RawTest.class);

    public JAS106_Client(String deviceName,
                         int carIndex,
                         List<String> packetList,
                         int delayTime,
                         String targetIP,
                         String targetPort,
                         boolean fixDate) {
        super();
        this.setDeviceName(deviceName);
        this.setIP(targetIP);
        this.setPORT(targetPort);
        this.setDelayTime(delayTime);
        this.setPacketList(packetList);
        this.setFixDate(fixDate);
        this.setDeviceIndex(carIndex);
        currentIndex = new AtomicInteger(0);
    }

    public JAS106_Client() {

    }
    @Override
    public void run() {
        logger.info(this.getDeviceName() + " started");
        sendPacket();
        logger.info(this.getDeviceName() + " ended");
    }
    @Override
    public void sendPacket() {
        Socket client = new Socket();
        InetSocketAddress isa = new InetSocketAddress(this.getIP(), Integer.parseInt(this.getPORT()));
        try {
            client.connect(isa, 3600000);
            BufferedOutputStream out = new BufferedOutputStream(client.getOutputStream());
            // 送出字串
            int startDeviceNameIndex = StringUtils.ordinalIndexOf(this.getPacketList().get(0), ",", 1);
            int endDeviceNameIndex = StringUtils.ordinalIndexOf(this.getPacketList().get(0), ",", 2);

            String originDeviceName = this.getPacketList().get(0).substring(startDeviceNameIndex + 1, endDeviceNameIndex);

            for (String originPacket : this.getPacketList()) {
                String packet = originPacket.replace(originDeviceName, this.getDeviceName()).concat("\r\n");
                byte [] bytePacket =  packet.getBytes(StandardCharsets.UTF_8);
                out.write(bytePacket);
                this.getCurrentIndex().getAndIncrement();
                out.flush();
                Thread.sleep(this.getDelayTime());
            }
            client.close();
        } catch (IOException | InterruptedException e) {
            logger.error("Socket連線有問題 !\n" +
                    this.getIP() + "\n" +
                    this.getPORT() + "\n" +
                    e.toString());
            logger.error("IOException :" + e.toString());
        }
    }

    public static List<String> readBinFile(String fileDir) {
            List<String> packetList = null;
            try {
                String content = new String(Files.readAllBytes(Paths.get(fileDir)),StandardCharsets.UTF_8);

                    packetList = Arrays.asList(content.split("#"));
                    packetList = packetList.stream()
                            .filter(packet -> packet.startsWith("$LOG"))
                            .map(filteredPacket -> filteredPacket.concat("#"))
                            .collect(Collectors.toList());
            } catch (IOException e) {
                logger.error(e.fillInStackTrace());
                e.printStackTrace();
            }
            return packetList;
    }
    public static List<String> readBinFile(String fileDir,Custom_DateTime fixDate) {
        List<String> originPacketList = readBinFile(fileDir);
        List<String> result = new ArrayList<>();
        for (String packet:originPacketList){
            result.add(packet.replace(packet.substring(StringUtils.ordinalIndexOf(packet,",",2)+1,StringUtils.ordinalIndexOf(packet,",",4)), fixDate.getEncodedDate()));
            fixDate = fixDate.AddSecond(30);
        }
        return result;
    }
}