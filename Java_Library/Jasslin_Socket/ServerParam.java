package Jasslin_Socket;

import Jasslin_Socket.EndStringProcess.IEndData;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ServerParam {
    private int timeout = 15000;
    private Charset charsets = StandardCharsets.UTF_8;
    private IEndData endDataProcess = null;
    private int serverPort;
    private String serverIP;
    private int dataBufferSize = 256;
    private int maxByteSize = 1048576;

    public int getTimeout() {
        return this.timeout;
    }

    public void setTimeout(int timeout)
            throws Exception {
        this.timeout = timeout;
    }

    public Charset getCharsets() {
        return this.charsets;
    }

    public void setCharsets(Charset charsets) {
        this.charsets = charsets;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getServerIP() {
        return this.serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public IEndData getEndDataProcess() {
        return this.endDataProcess;
    }

    public void setEndDataProcess(IEndData endDataProcess) {
        this.endDataProcess = endDataProcess;
    }

    public int getDataBufferSize() {
        return this.dataBufferSize;
    }

    public void setDataBufferSize(int dataBufferSize) {
        this.dataBufferSize = dataBufferSize;
    }

    public int getMaxByteSize() {
        return this.maxByteSize;
    }

    public void setMaxByteSize(int maxByteSize) {
        this.maxByteSize = maxByteSize;
    }
}
