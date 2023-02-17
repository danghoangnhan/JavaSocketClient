package Jasslin_Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TCP_Server implements IClientPackageEvent {
    private static final Log logger = LogFactory.getLog(TCP_Server.class);
    private ServerSocket server;
    private Map<UUID, ClientPackage> clients = new HashMap<>();
    private ServerParam serverParam;
    private List<IClientPackageEvent> clientPackageEvents = new ArrayList<>();
    private TCP_Server tcpServer;

    public TCP_Server(ServerParam _serverParam) throws Exception {
        serverParam = _serverParam;
        server = new ServerSocket(serverParam.getServerPort());
        tcpServer = this;
    }

    public void SendDataToClient(UUID clientId, String data) throws Exception {
        ClientPackage cpSend = clients.get(clientId);
        if (cpSend != null)
            cpSend.SendData(data);
    }

    public void SendDataToClient(UUID clientId, byte[] data) throws Exception {
        ClientPackage cpSend = clients.get(clientId);
        if (cpSend != null)
            cpSend.SendData(data);
    }

    public void Start() {
        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    Socket socket = server.accept();
                    UUID uuid = UUID.randomUUID();
                    ClientPackageObject clientPackageObject = new ClientPackageObject();
                    clientPackageObject.setCharsets(serverParam.getCharsets());
                    clientPackageObject.setDataBuffer(new byte[serverParam.getDataBufferSize()]);
                    clientPackageObject.setMaxByteSize(serverParam.getMaxByteSize());
                    clientPackageObject.setEndDataProcess(serverParam.getEndDataProcess());
                    clientPackageObject.setId(uuid);
                    clientPackageObject.setSocket(socket);
                    clientPackageObject.setTimeout(serverParam.getTimeout());
                    synchronized (clients) {
                        if (clients.containsKey(uuid) == false)
                            clients.put(uuid, new ClientPackage(clientPackageObject, tcpServer));
                    }
                    clients.get(uuid).Start();
                }
            } catch (Exception e) {
            }
        });
        thread.start();
    }

    public void AddClientPackageEvent(IClientPackageEvent clientPackageEvent) {
        synchronized (clientPackageEvents) {
            clientPackageEvents.add(clientPackageEvent);
        }
    }

    public void RemoveClientPackageEvent(IClientPackageEvent clientPackageEvent) {
        synchronized (clientPackageEvents) {
            clientPackageEvents.remove(clientPackageEvent);
        }
    }

    @Override
    public void OnConnectClosed(ClientPackageObject clientPackage) {
        synchronized (clients) {
            if (clients.containsKey(clientPackage.getId()))
                clients.remove(clientPackage.getId());
        }
        for (IClientPackageEvent clientPackageEvent : clientPackageEvents)
            clientPackageEvent.OnConnectClosed(clientPackage);

        try {
            clientPackage.Dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnConnectConnected(ClientPackageObject clientPackage, String ipInfo) {
        for (IClientPackageEvent clientPackageEvent : clientPackageEvents) {
            clientPackageEvent.OnConnectConnected(clientPackage, ipInfo);
        }
    }

    @Override
    public void OnDataReceived(ClientPackageObject clientPackage, String data, byte[] bytes) throws Exception {
        for (IClientPackageEvent clientPackageEvent : clientPackageEvents) {
            clientPackageEvent.OnDataReceived(clientPackage, data, bytes);
        }
    }

    public int GetConnectionNumber() {
        return clients.size();
    }

    public void CloseOneClient(UUID uuid) {
        ClientPackage clientPackage = null;
        synchronized (clients) {
            if (clients.containsKey(uuid)) {
                clientPackage = clients.get(uuid);
                clients.remove(uuid);
            }
        }

        try {
            clientPackage.Dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Dispose() throws IOException {
        server.close();
    }

    public static void sendDataWithSpecificIp(String IP, int port, String data) throws IOException {
        try {
            Socket socket = new Socket(IP, port);
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            byte[] bytes = data.getBytes(StandardCharsets.UTF_8); // Charset to encode into
            dataOutputStream.write(bytes);
            dataOutputStream.flush(); // send the message
            byte[] bbb = new byte[1024];
            String response;
            while (true) {
                // Read next message.
                response = String.valueOf(inputStream.read(bbb));
                // handle message...
                break;
                // If you need to stop communication use 'break' to exit loop;
            }
            System.out.print("is sent Successfully(Ack=" + response + ",string:" + data);
            inputStream.close();
            dataOutputStream.close(); // close the output stream when we're done.
            socket.close();
        } catch (Exception e) {
            logger.error(e.fillInStackTrace());
        }
    }
}
