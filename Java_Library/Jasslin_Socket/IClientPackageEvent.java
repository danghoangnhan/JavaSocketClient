package Jasslin_Socket;

public interface IClientPackageEvent {

    void OnDataReceived(ClientPackageObject clientPackage, String data, byte[] bytes) throws Exception;

    void OnConnectClosed(ClientPackageObject clientPackage);

    void OnConnectConnected(ClientPackageObject clientPackage, String ipInfo);
}
