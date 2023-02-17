package Device;

import org.codehaus.jettison.json.JSONObject;

import java.io.File;

public interface clientConstants {
    String separator = System.getProperty("file.separator");
    String dir = System.getProperty("user.dir");
    String dataSetDir = new File(dir).getParentFile().getAbsolutePath().concat(separator).concat("dataSet").concat(separator).concat("bin").concat(separator);
    public final int RapiddelayTime = 2000;
    public final int NormaldelayTime = 30000;
    public final String Port106 = "9106";
    //for 208 gateway
    public final int maxDevice208 = 1000;
    public final String fileName208 = dataSetDir.concat("208Data").concat(separator).concat("2021-04-24_RawData(208-LOG40).bin");
    public final String fileName106 = dataSetDir.concat("106Data").concat(separator).concat("2022-01-24_RawData.bin");

    public final String localIP = "127.0.0.1";
    public final String Port208 = "9208";
    public final String DoIP = "128.199.184.219";
    public final String prefix106 = "testCar106_";
    public final String prefix208 = "testCar208_";
}
