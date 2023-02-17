package Device;

import Time_Component.Custom_DateTime;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;



public class JassDevice_Client {

    protected int deviceIndex;
    protected AtomicInteger currentIndex;
    private String deviceName;
    private String IP;
    private String PORT;
    private int delayTime;
    private boolean fixDate;

    private static String logHeader;
    private static String dataHeader;
    private static String chkHeader;


    public AtomicInteger getCurrentIndex() {
        return currentIndex;
    }
    public int getFinalIndex() {
        return currentIndex.get();
    }
    public void setCurrentIndex(AtomicInteger currentIndex) {
        this.currentIndex = currentIndex;
    }
    public boolean isFixDate() {
        return fixDate;
    }

    public void setFixDate(boolean fixDate) { this.fixDate = fixDate; }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    private List<String> packetList = new ArrayList<>();

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getPORT() {
        return PORT;
    }

    public void setPORT(String PORT) {
        this.PORT = PORT;
    }

    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    public List<String> getPacketList() {
        return packetList;
    }

    public void setPacketList(List<String> packetList) {
        this.packetList = packetList;
    }


    public int getDeviceIndex() { return deviceIndex; }
    public void setDeviceIndex(int deviceIndex) { this.deviceIndex = deviceIndex; }
    public static List<String> readBinFile(String fileNameDir){
        return null;
    }
    public void sendPacket() {
        Socket client = new Socket();
        InetSocketAddress isa = new InetSocketAddress(this.getIP(), Integer.parseInt(this.getPORT()));
        try {
            client.connect(isa, 10000);
            BufferedOutputStream out = new BufferedOutputStream(client.getOutputStream());

            int startDeviceIndex = StringUtils.ordinalIndexOf(this.getPacketList().get(0), ",", 1);
            int endDeviceIndex =   StringUtils.ordinalIndexOf(this.getPacketList().get(0), ",", 2);
            String originDeviceName = this.getPacketList().get(0).substring(startDeviceIndex + 1, endDeviceIndex);

            int startDateIndex = StringUtils.ordinalIndexOf(this.getPacketList().get(0), ",", 5);
            int endDateIndex =   StringUtils.ordinalIndexOf(this.getPacketList().get(0), ",", 6);
            int endTimeIndex =   StringUtils.ordinalIndexOf(this.getPacketList().get(0), ",", 7);

            for (String packet : this.getPacketList()) {

                String originDate = packet.substring(startDateIndex + 1, endTimeIndex);
                String newPack = packet.replace(originDeviceName, this.getDeviceName());

                if (this.isFixDate()) {
                    String currentDate = new Custom_DateTime().getEncodedDate();
                    newPack = packet.replace(originDate, currentDate);
                }
                System.out.println(newPack);
                out.write(packet.getBytes());
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
