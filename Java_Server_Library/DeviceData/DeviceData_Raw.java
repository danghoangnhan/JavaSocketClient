package DeviceData;
import Time_Component.Custom_DateTime;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
public class DeviceData_Raw implements Runnable {
    private static final Log logger = LogFactory.getLog(DeviceData_Raw.class);
    private final String fileName;
    String data;
    String carNumber;

    public DeviceData_Raw(String carNumber, String _data) {
        this.data = _data;
        this.carNumber = carNumber;
        Custom_DateTime now = new Custom_DateTime();
        now = now.AddHour(8);
        this.fileName = String.format("rawData/%s/%s.txt = .txt",this.carNumber,now.getDateString().concat(now.getDateString().concat(".txt")));
    }
    @Override
    public void run() {
        try {
            LogggingRawData(data);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    public synchronized void LogggingRawData(String dataStr) throws IOException {
        File deviceFile = new File(this.fileName);
        if (deviceFile.getParentFile() != null) {
            deviceFile.getParentFile().mkdirs(); //File with Create Dir
        }
        if (!deviceFile.exists()) {
            deviceFile.createNewFile();
        }
        FileWriter fstream = new FileWriter(this.fileName, StandardCharsets.UTF_8, true);
        BufferedWriter info = new BufferedWriter(fstream);
            info.write(dataStr);
            info.close();  // write to file
    }
}