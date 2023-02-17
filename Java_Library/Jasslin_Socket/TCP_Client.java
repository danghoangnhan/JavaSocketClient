package Jasslin_Socket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class TCP_Client {

    private  ServerParam serverParam;

    public TCP_Client(ServerParam _serverParam) throws Exception {
        this.serverParam = _serverParam;
    }

    public void sendDataAndWait(String data) {
        try {
            Socket socket = new Socket(this.serverParam.getServerIP(), this.serverParam.getServerPort());
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
            System.out.println(e.fillInStackTrace().getMessage());
        }
    }
}