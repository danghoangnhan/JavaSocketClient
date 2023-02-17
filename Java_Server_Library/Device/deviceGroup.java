package Device;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class deviceGroup implements Runnable{

    private String prefix;
    private List<String> packetList;
    private int CarNumb;
    private String targetIP;
    private String targetPort;

    private int delayTime;
    private boolean fixDate;
    private JassDevice_Client clientClass;
    private int DeviceResult;
    private List<JassDevice_Client> clientGroup;
    public deviceGroup(String prefix, List<String> packetList, int CarNumb, String targetIP, String targetPort, int delayTime, boolean fixDate,JassDevice_Client clientClass) {
        this.setCarNumb(CarNumb);
        this.setPrefix(prefix);
        this.setPacketList(packetList);
        this.setFixDate(fixDate);
        this.setTargetIP(targetIP);
        this.setTargetPort(targetPort);
        this.setDelayTime(delayTime);
        this.clientClass = clientClass;
    }

    @Override
    public void run() {
        this.clientGroup = new ArrayList<>();
        for (int i = 0; i < CarNumb; i++) {
            if (clientClass instanceof JAS106_Client){
                clientGroup.add(new JAS106_Client(prefix.concat(String.valueOf(i)),i, packetList, delayTime, targetIP, targetPort, fixDate));
            }
            else {
                clientGroup.add(new JAS208_Client(prefix.concat(String.valueOf(i)),i, packetList, delayTime, targetIP, targetPort, fixDate));
            }
        }
        ExecutorService executorService = Executors.newFixedThreadPool(clientGroup.size());
        for (JassDevice_Client client:clientGroup){
            executorService.execute((Runnable) client);
        }
        executorService.shutdown();
        while (!executorService.isTerminated());
    }
    public int totalSendMessage(){
        return clientGroup.stream().mapToInt(JassDevice_Client::getFinalIndex).sum();
    }
    public boolean isFixDate() {
        return fixDate;
    }

    public void setFixDate(boolean fixDate) {
        this.fixDate = fixDate;
    }

    public JassDevice_Client getClientClass() {
        return clientClass;
    }

    public void setClientClass(JassDevice_Client clientClass) {
        this.clientClass = clientClass;
    }

    public int getDeviceResult() {
        return DeviceResult;
    }

    public void setDeviceResult(int deviceResult) {
        DeviceResult = deviceResult;
    }
    public String getTargetIP() {
        return targetIP;
    }

    public void setTargetIP(String targetIP) {
        this.targetIP = targetIP;
    }

    public String getTargetPort() {
        return targetPort;
    }

    public void setTargetPort(String targetPort) {
        this.targetPort = targetPort;
    }

    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public List<String> getPacketList() {
        return packetList;
    }

    public void setPacketList(List<String> packetList) {
        this.packetList = packetList;
    }

    public int getCarNumb() {
        return CarNumb;
    }

    public void setCarNumb(int carNumb) {
        CarNumb = carNumb;
    }
}