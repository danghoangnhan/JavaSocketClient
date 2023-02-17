package DeviceData;

import DB_JassFleet_Object.Data.tb_chk_data;
import DB_Table_Operate.DB_JassLin_Fleet.Operate_JassFleet_Table_Factory;
import Time_Component.Custom_DateTime;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jettison.json.JSONObject;

import java.util.Objects;

public class DeviceData_CHK implements Runnable {
    private static final Log logger = LogFactory.getLog(DeviceData_CHK.class);
    private String data;
    private tb_chk_data chkData;

    public DeviceData_CHK(String _data) {
        this.data = _data;
    }

    public String getData() {
        return data;
    }

    public tb_chk_data getChkData() {
        return chkData;
    }

    @Override
    public void run() {
        try {
            processData();
            Operate_JassFleet_Table_Factory.GetInstance().getOperateTbChkData().Insert(chkData);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    public void processData() throws Exception {
        JSONObject data = new JSONObject(this.data);
        String carUnicode = Objects.toString(data.get("BusID"), "");
        if (!carUnicode.equals("")) {
            //String customerID = checkTableExist(carUnicode);
            //if (customerID != null) {
            this.chkData = new tb_chk_data();
            chkData.setImei(Objects.toString(data.get("Imei"), ""));
            chkData.setBusID(carUnicode);
            if (data.has("Imsi")) {
                chkData.setImsi(Objects.toString(data.get("Imsi"), ""));
            }
            chkData.setDeviceType(Objects.toString(data.get("DeviceType"), ""));
            chkData.setFwSign(Objects.toString(data.get("FwSign"), ""));
            chkData.setCsq(Integer.parseInt(Objects.toString(data.get("Csq"), "0")));
            chkData.setGpsSignal(Objects.toString(data.get("GpsSignal"), ""));
            chkData.setAcc(Objects.toString(data.get("Acc"), ""));
            chkData.setInternet(Objects.toString(data.get("Internet"), ""));
            chkData.setIpPort(Objects.toString(data.get("IpPort"), ""));
            chkData.setDnsPort(Objects.toString(data.get("DnsPort"), ""));
            chkData.setSendTime(Integer.parseInt(Objects.toString(data.get("SendTime"), "0")));
            chkData.setStandbySendTime(Integer.parseInt(Objects.toString(data.get("StandbySendTime"), "0")));
            chkData.setSpeedTrigger(Integer.parseInt(Objects.toString(data.get("SpeedTrigger"), "0")));
            chkData.setSpeedGain(Double.parseDouble(Objects.toString(data.get("SpeedGain"), "0.0")));
            chkData.setRpmTrigger(Integer.parseInt(Objects.toString(data.get("RpmTrigger"), "0")));
            chkData.setRpmDiv(Integer.parseInt(Objects.toString(data.get("RpmDiv"), "0")));
            chkData.setInsertTime(new Custom_DateTime().AddHour(8).GetUTCDateTimeString());

            //Operate_JassFleet_Table_Factory.GetInstance().getOperateTbChkData().Insert(chkData, customerID);
            //}
        }

    }


    public void processDataOld(String dataStr) throws Exception {
        String[] data = dataStr.split(",");
        if (data.length >= 19) {
            String carUnicode = Objects.toString(data[1], "");
                tb_chk_data chkData = new tb_chk_data();
                chkData.setImei(carUnicode);
                chkData.setBusID(Objects.toString(data[2], ""));
                chkData.setDeviceType(Objects.toString(data[3], ""));
                chkData.setFwSign(Objects.toString(data[4], ""));
                chkData.setCsq(Integer.parseInt(Objects.toString(data[5], "0")));
                chkData.setGpsSignal(Objects.toString(data[6], ""));
                chkData.setAcc(Objects.toString(data[7], ""));
                chkData.setInternet(Objects.toString(data[8], ""));
                chkData.setIpPort(Objects.toString(data[9], ""));
                chkData.setDnsPort(Objects.toString(data[10], ""));
                chkData.setSendTime(Integer.parseInt(Objects.toString(data[11], "0")));
                chkData.setStandbySendTime(Integer.parseInt(Objects.toString(data[12], "0")));
                chkData.setSpeedTrigger(Integer.parseInt(Objects.toString(data[13], "0")));
                chkData.setSpeedGain(Double.parseDouble(Objects.toString(data[14], "0.0")));
                chkData.setRpmTrigger(Integer.parseInt(Objects.toString(data[15], "0")));
                chkData.setRpmDiv(Integer.parseInt(Objects.toString(data[16], "0")));
                chkData.setInsertTime(new Custom_DateTime().GetUTCDateTimeString());

                Operate_JassFleet_Table_Factory.GetInstance().getOperateTbChkData().Insert(chkData);

        } else {
            logger.error("ERROR CHK DATA" + dataStr);
        }
    }
}
