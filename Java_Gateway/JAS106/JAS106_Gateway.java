package JAS106;

import DataDecoder.JAS106_Decoder;
import Device.DeviceInformation;
import Device.gatewayConstants;
import Jasslin_Socket.ClientPackageObject;
import Jasslin_Socket.EndStringProcess.EndString_Sharp_RN;
import Jasslin_Socket.IClientPackageEvent;
import Jasslin_Socket.ServerParam;
import Jasslin_Socket.TCP_Server;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

public class JAS106_Gateway implements IClientPackageEvent, gatewayConstants {
    private TCP_Server server;
    private DeviceInformation deviceInformation = new DeviceInformation();
    private static final Log logger = LogFactory.getLog(JAS106_Gateway.class);
    private JAS106_Decoder decoder = new JAS106_Decoder();

    public JAS106_Gateway() throws Exception {
        ServerParam serverParam = new ServerParam();
        serverParam.setCharsets(StandardCharsets.UTF_8);
        serverParam.setServerPort(gateway106_serverPort);
        serverParam.setServerIP(gateway106_serverIP);
        serverParam.setEndDataProcess(new EndString_Sharp_RN());
        serverParam.setTimeout(gateway106_timeOut);

        server = new TCP_Server(serverParam);
        server.AddClientPackageEvent(this);
        server.Start();
    }

    @Override
    public void OnDataReceived(ClientPackageObject clientPackage, String data, byte[] bytes) {
        if (!data.isEmpty()) {
            processData(data, bytes);
            processConnection(clientPackage, data);
        }
    }

    @Override
    public void OnConnectClosed(ClientPackageObject clientPackage) {
        System.out.println("CLOSED");
    }

    @Override
    public void OnConnectConnected(ClientPackageObject clientPackage, String ipInfo) {
        System.out.println(ipInfo);
    }

    public void processData(String dataStr, byte[] bytes) {
        try {
            if (dataStr.length() > 0) {
                String[] data = dataStr.split(",");
                if (data.length > 2) {

                    String tag = data[0];
                    String carUnicode = data[1];
                    String parsedData;

                    System.out.println(dataStr);

                    if (tag.equals("$LOG")) {
                        parsedData = decoder.logDataDecode(dataStr, bytes);
                        if (parsedData != null) {
                            System.out.println(dataStr);
                        }
                    }
                    if (tag.equals("$CHK")) {
                        parsedData = decoder.eventDataDecode(dataStr, bytes);
                        if (parsedData != null) {
                            System.out.println(dataStr);
                        }
                    }
                    if (tag.equals("$DATA")) {
                        parsedData = decoder.abnormalDataDecode(dataStr, bytes);
                        if (parsedData != null) {
                            System.out.println(dataStr);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error Data: " + dataStr);
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    public void processConnection(ClientPackageObject clientPackage, @NotNull String dataStr) {
        String[] data = dataStr.split(",");
        String carUnicode = data[1];
        DeviceInformation.getInstance().saveConnectionInfo(carUnicode, clientPackage);
    }
}
