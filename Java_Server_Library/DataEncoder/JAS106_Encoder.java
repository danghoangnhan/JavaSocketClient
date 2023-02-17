//package DataEncoder;
//
//import DB_JassFleet_Object.Data.tb_abnormal_data;
//import DB_JassFleet_Object.Data.tb_chk_data;
//import DB_JassFleet_Object.Data.tb_log_data;
//import VDR_Object.PackageData;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class JAS106_Encoder implements IEncoder {
//    List<tb_log_data> datalogHandle = new ArrayList<>();
//
//    @Override
//    public List<String> logDataEncode(List<PackageData> log_dataList) throws Exception {
//
//        for (int i=0;i<30;i++){
//            tb_log_data data= new tb_log_data();
//            data.setLongitude("0");
//            //datalogHandle[i].DATA[u8DatalogIdx].i32Longitude = g_SysInfo.tGPS_Data.gpsLocation.dLongitude * 1000000;
//            data.setLatitude("0");
//            //datalogHandle[i].DATA[u8DatalogIdx].i32Latitude = g_SysInfo.tGPS_Data.gpsLocation.dLatitude * 1000000;
//        }
////        datalogHandle[i].DATA[u8DatalogIdx].u8Course = (uint8_t)g_SysInfo.tGPS_Data.u16Course / 10;
////        datalogHandle[i].DATA[u8DatalogIdx].u8Speed = (uint8_t)M_2_KM(g_SysInfo.tSpeedInfo.u32RealSpeed);
////        datalogHandle[i].DATA[u8DatalogIdx].b50RPM = (uint8_t)(g_SysInfo.tVehicleState.u16RPM / 50);
////        datalogHandle[i].DATA[u8DatalogIdx].EXTIO.bit._7 = g_SysInfo.tEXTIO.bits.IN_ACC;
////        datalogHandle[i].DATA[u8DatalogIdx].EXTIO.bit._6 = g_SysInfo.tEXTIO.bits.IN_5;
////        datalogHandle[i].DATA[u8DatalogIdx].EXTIO.bit._5 = g_SysInfo.tEXTIO.bits.IN_6;
////        datalogHandle[i].DATA[u8DatalogIdx].EXTIO.bit._4 = g_SysInfo.tEXTIO.bits.IN_7;
////        datalogHandle[i].DATA[u8DatalogIdx].EXTIO.bit._3 = g_SysInfo.tEXTIO.bits.IN_1;
////        datalogHandle[i].DATA[u8DatalogIdx].EXTIO.bit._2 = g_SysInfo.tEXTIO.bits.IN_2;
////        datalogHandle[i].DATA[u8DatalogIdx].EXTIO.bit._1 = g_SysInfo.tEXTIO.bits.IN_3;
////        datalogHandle[i].DATA[u8DatalogIdx].EXTIO.bit._0 = g_SysInfo.tEXTIO.bits.IN_4;
////        datalogHandle[i].DATA[u8DatalogIdx].u8Speed_GPS = (uint8_t)M_2_KM(g_SysInfo.tGPS_Data.u32Speed);
////        datalogHandle[i].DATA[u8DatalogIdx].u8Status.bit._7 = bUSB_Ready;
////        datalogHandle[i].DATA[u8DatalogIdx].u8Status.bit._1 = g_SysInfo.tSpeedInfo.bIsOverSpeed;
////        datalogHandle[i].DATA[u8DatalogIdx].u8Status.bit._0 = g_SysInfo.tSpeedInfo.bIsRPM_Idle;
//
//        return "";
//    }
//
//    @Override
//    public String eventDataEncode(List<tb_chk_data> chk_dataList) throws Exception {
//        return "";
//    }
//
//    @Override
//    public String abnormalDataEncode(List<tb_abnormal_data> abnormal_dataList) throws Exception {
//        return "";
//    }
//}
