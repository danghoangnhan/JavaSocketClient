package DataEncoder;

import DB_JassFleet_Object.Data.tb_abnormal_data;
import DB_JassFleet_Object.Data.tb_chk_data;
import Time_Component.Custom_DateTime;
import VDR_Object.PackageData;
import VDR_Object.tb_carInformation_data;
import com.google.common.collect.Lists;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JAS208_Encoder implements IEncoder {
    private tb_carInformation_data carData;
    public JAS208_Encoder(tb_carInformation_data  carInfo){
        this.carData = carInfo;
    }
    @Override
    public List<String> logDataEncode(List<PackageData> log_dataList) {
        List<String> result = new ArrayList<>();
        for (List<PackageData> subPacketList:Lists.partition(log_dataList,30)){
            String cIMEI = "123456";
            String cIMSI = "123456";
            int u8GSM_CSQ = 69;
            boolean bStatus = true;
            int u8StlNum = 21;
            Custom_DateTime packageDateTime = log_dataList.get(subPacketList.size()-1).getTime();
            PackageData mainPacket = subPacketList.get(subPacketList.size()-1);
            String pBufPtr = String.format("%s,%s,%s,%s,%s,%s,%s,%c,%02d,%02d,%09d,%02d,",
                    "$LOG40",
                    cIMEI,
                    this.carData.getCarDeviceSerial(),
                    cIMSI,
                    mainPacket.getDriverId(),
                    packageDateTime.GetEncodedDate(),
                    packageDateTime.GetEncodedTime(),
                    (bStatus) ? 'A' : 'V',
                    u8GSM_CSQ,
                    u8StlNum,
                    mainPacket.getDistance(),
                    subPacketList.size());
            for (PackageData packet:subPacketList){
                pBufPtr=pBufPtr.concat(new String(packet.encode(), StandardCharsets.UTF_8));
            }
            result.add(pBufPtr.concat("#"));
        }
        return result;
    }

    @Override
    public String eventDataEncode(List<tb_chk_data> chk_dataList) throws Exception {
        return "";
    }

    @Override
    public String abnormalDataEncode(List<tb_abnormal_data> abnormal_dataList) throws Exception {
        return "";
    }
}
