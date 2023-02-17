package Jasslin_Socket;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class ClientPackage {

    private List<Byte> dataList = new ArrayList<>();
    private ClientPackageObject clientPackageObject;
    private IClientPackageEvent clientPackageEvent = null;

    public ClientPackage(ClientPackageObject _clientPackageObject, IClientPackageEvent _clientPackageEvent) throws SocketException {
        clientPackageObject = _clientPackageObject;
        clientPackageObject.getSocket().setSoTimeout(clientPackageObject.getTimeout());
        this.clientPackageEvent = _clientPackageEvent;
    }

    public void SendData(String data) throws Exception {
        clientPackageObject.WriteData(data);
    }

    public void SendData(byte[] data) throws Exception {
        clientPackageObject.WriteData(data);
    }

    public void Start() {
        Thread thread = new Thread(() -> {
            try {
                if (clientPackageObject != null)
                    clientPackageEvent.OnConnectConnected(clientPackageObject, clientPackageObject.getSocket().getInetAddress().getHostAddress());
                DataInputStream bufferedInputStream = new DataInputStream(clientPackageObject.getSocket().getInputStream());
                int receivedByteLength = 0;
                byte[] bs = null;
                while (true) {
                    receivedByteLength = bufferedInputStream.read(clientPackageObject.getDataBuffer(), 0, clientPackageObject.getDataBuffer().length);
                    if (receivedByteLength <= 0)
                        throw new IOException();

                    if (dataList.size() > clientPackageObject.getMaxByteSize())
                        throw new Exception();

                    for (int i = 0; i < receivedByteLength; i++) {
                        dataList.add(clientPackageObject.getDataBuffer()[i]);
                    }

                    int startIndex = 1;
                    if (dataList.size() > clientPackageObject.getDataBuffer().length * 2)
                        startIndex = dataList.size() - clientPackageObject.getDataBuffer().length - 2;

                    bs = null;
                    while (true) {
                        bs = clientPackageObject.getEndDataProcess().GetDataWithEndString(dataList, startIndex);
                        if (bs == null)
                            break;
                        String data = new String(bs, clientPackageObject.getCharsets());
                        startIndex = 1;
                        if (clientPackageObject != null)
                            clientPackageEvent.OnDataReceived(clientPackageObject, data, bs);
                    }
                }
            } catch (Exception e) {
                if (clientPackageObject != null)
                    clientPackageEvent.OnConnectClosed(clientPackageObject);
            }
        });
        thread.start();

    }

    public void Dispose() throws Exception {
        if (this.clientPackageObject != null) {
            this.clientPackageObject.Dispose();
            this.clientPackageObject = null;
        }
    }

    public ClientPackageObject getClientPackageObject() {
        return clientPackageObject;
    }
}
