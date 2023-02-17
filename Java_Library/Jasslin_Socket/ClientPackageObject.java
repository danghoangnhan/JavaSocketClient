package Jasslin_Socket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class ClientPackageObject extends ServerParam {

    private final Object synced = new Object();

    private Socket socket;
    private byte[] dataBuffer = new byte[256];
    private UUID id;
    private Object tag;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public byte[] getDataBuffer() {
        return dataBuffer;
    }

    public void setDataBuffer(byte[] dataBuffer) {
        this.dataBuffer = dataBuffer;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public void WriteData(String data) throws IOException {
        byte[] bytes = data.getBytes(getCharsets());
        WriteData(bytes);
    }

    public void WriteData(byte[] bytes) throws IOException {
        synchronized (synced) {
            DataOutputStream bufferedOutputStream = new DataOutputStream(socket.getOutputStream());
            bufferedOutputStream.write(bytes);
        }
    }

    public void Dispose() throws Exception {
        if (this.socket != null) {
            this.socket.close();
        }
        this.dataBuffer = null;
        this.socket = null;
    }
}
