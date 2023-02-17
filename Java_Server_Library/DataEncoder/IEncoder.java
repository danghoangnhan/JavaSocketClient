package DataEncoder;

import DB_JassFleet_Object.Data.tb_abnormal_data;
import DB_JassFleet_Object.Data.tb_chk_data;
import VDR_Object.PackageData;

import java.util.List;

public interface IEncoder {
    List<String> logDataEncode(List<PackageData> log_dataList) throws Exception;
    String eventDataEncode(List<tb_chk_data> chk_dataList) throws Exception;
    String abnormalDataEncode(List<tb_abnormal_data>  abnormal_dataList) throws Exception;
}
