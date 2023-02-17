package Device;

import Jasslin_Socket.ClientPackageObject;

import java.util.HashMap;
import java.util.Map;

public class DeviceInformation {
    private static DeviceInformation instance;
    private Map<String, ClientPackageObject> connectInformation;


    public DeviceInformation() {
        connectInformation = new HashMap<>();
    }

    public static synchronized DeviceInformation getInstance() {

        if (instance == null) {
            instance = new DeviceInformation();
        }

        return instance;
    }

    public void saveConnectionInfo(String carUnicode, ClientPackageObject clientPackageObject) {
        connectInformation.put(carUnicode, clientPackageObject);
    }

    public void removeConnectionInfo(String carUnicode) {
        connectInformation.remove(carUnicode);
    }
}
