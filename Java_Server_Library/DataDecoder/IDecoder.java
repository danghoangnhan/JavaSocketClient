package DataDecoder;

public interface IDecoder {
    String logDataDecode(String data, byte[] bytes) throws Exception;
    String eventDataDecode(String data, byte[] bytes) throws Exception;
    String abnormalDataDecode(String data, byte[] bytes) throws Exception;
}
