package DeviceData;

import DB_JassFleet_Object.Data.tb_abnormal_data;
import DB_Table_Operate.DB_JassLin_Fleet.Operate_JassFleet_Table_Factory;
import Time_Component.Custom_DateTime;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jettison.json.JSONObject;

import java.util.Objects;

public class DeviceData_Abnormal implements Runnable {
    private static final Log logger = LogFactory.getLog(DeviceData_Abnormal.class);
    private String data;
    private tb_abnormal_data abnormalData = new tb_abnormal_data();

    public DeviceData_Abnormal(String _data) {
        this.data = _data;
    }

    @Override
    public void run() {
        try {
            processData(this.data);
            Operate_JassFleet_Table_Factory.GetInstance().getOperateTbAbnormalData().Insert(this.abnormalData);
        } catch (Exception e) {
            logger.error(data);
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    public void processData(String dataStr) throws Exception {
        JSONObject data = new JSONObject(dataStr);
        String carUnicode = Objects.toString(data.get("Serial"), "");
        if (!carUnicode.equals("")) {
            this.abnormalData = new tb_abnormal_data();
            if (data.has("Imei")) {
                abnormalData.setImei(Objects.toString(data.get("Imei"), ""));
            }
            abnormalData.setSerial(carUnicode);
            if (data.has("Imsi")) {
                abnormalData.setImsi(Objects.toString(data.get("Imsi"), ""));
            }
            String dateTimeStr = Objects.toString(data.get("Date"), "") + " "
                    + Objects.toString(data.get("Time"), "");
            Custom_DateTime dateTime = new Custom_DateTime(dateTimeStr, "ddMMyy HHmmss").AddHour(8);
            abnormalData.setDateTime(dateTime.getDateTimeString());
            abnormalData.setGpsSignal(Objects.toString(data.get("GpsSignal"), ""));
            abnormalData.setLongitude(Objects.toString(data.get("Longitude"), ""));
            abnormalData.setLatitude(Objects.toString(data.get("Latitude"), ""));
            abnormalData.setDirection(Integer.parseInt(Objects.toString(data.get("Direction"), "0")));
            abnormalData.setSpeed(Integer.parseInt(Objects.toString(data.get("Speed"), "0")));
            abnormalData.setMile(Integer.parseInt(Objects.toString(data.get("Mile"), "0")));
            abnormalData.setRpm(Integer.parseInt(Objects.toString(data.get("Rpm"), "0")));
            abnormalData.setDriverID(Objects.toString(data.get("DriverID"), ""));
            abnormalData.setCsq(Integer.parseInt(Objects.toString(data.get("Csq"), "0")));
            abnormalData.setGps(Integer.parseInt(Objects.toString(data.get("Gps"), "0")));
            abnormalData.setIoSignal(Objects.toString(data.get("IoSignal"), ""));
            abnormalData.setAbnormalCode(Objects.toString(data.get("AbnormalCode"), ""));
            abnormalData.setAbnormalContent(Objects.toString(data.get("AbnormalContent"), ""));
            abnormalData.setInsertTime(new Custom_DateTime().AddHour(8).GetUTCDateTimeString());
        }
    }
}
