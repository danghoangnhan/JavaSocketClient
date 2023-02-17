package JAS208;

import DB_Table_Operate.DB_Kafka.Operate_Kafka_Factory;
import DataDecoder.JAS208_Decoder;
import Device.DeviceInformation;
import Device.gatewayConstants;
import Jasslin_Socket.ClientPackageObject;
import Jasslin_Socket.EndStringProcess.EndString_RN;
import Jasslin_Socket.EndStringProcess.EndString_Sharp_RN;
import Jasslin_Socket.IClientPackageEvent;
import Jasslin_Socket.ServerParam;
import Jasslin_Socket.TCP_Server;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class JAS208_Gateway implements IClientPackageEvent, gatewayConstants {
    private TCP_Server server;
    private DeviceInformation deviceInformation = new DeviceInformation();
    private static final Log logger = LogFactory.getLog(JAS208_Gateway.class);
    private JAS208_Decoder decoder = new JAS208_Decoder();

    public JAS208_Gateway() throws Exception {
        ServerParam serverParam = new ServerParam();
        serverParam.setCharsets(StandardCharsets.UTF_8);
        serverParam.setServerPort(gateway208_serverPort);
        serverParam.setServerIP(gateway208_serverIP);
        serverParam.setEndDataProcess(new EndString_Sharp_RN());
        serverParam.setTimeout(gateway208_timeOut);

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
//        try {
//            server.SendDataToClient(clientPackage.getId(), "");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
    public void processData(String dataStr, byte[] bytes) {
        try {
            if (dataStr.length() > 0) {
                String[] data = dataStr.split(",");
                if (data.length > 3) {
                    String tag = data[0];
                    String carUnicode = data[2];
                    String parsedData;

                    Operate_Kafka_Factory
                            .GetInstance()
                            .getOperateKafkaRawData()
                            .send(carUnicode.toUpperCase(), dataStr);

                    if (tag.equals("$LOG40")) {
                        parsedData = decoder.logDataDecode(dataStr, bytes);
                        if (parsedData != null) {
                            Operate_Kafka_Factory.GetInstance().getOperateKafkaLogData().send(carUnicode.toUpperCase(), parsedData);
                        }
                    }
                    if (tag.equals("$CHK1")) {
                        parsedData = decoder.eventDataDecode(dataStr, bytes);
                        if (parsedData != null) {
                            Operate_Kafka_Factory.GetInstance().getOperateKafkaEventData().send(carUnicode.toUpperCase(), parsedData);
                        }
                    }
                    if (tag.equals("$DATA1")) {
                        parsedData = decoder.abnormalDataDecode(dataStr, bytes);
                        if (parsedData != null) {
                            Operate_Kafka_Factory.GetInstance().getOperateKafkaAbnormalData().send(carUnicode.toUpperCase(), parsedData);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error Data: " + dataStr);
            logger.error(ExceptionUtils.getStackTrace(e));

        }
    }

    public void processConnection(ClientPackageObject clientPackage, String dataStr) {
        String[] data = dataStr.split(",");
        String carUnicode = data[1];
        DeviceInformation.getInstance().saveConnectionInfo(carUnicode, clientPackage);
    }
}
