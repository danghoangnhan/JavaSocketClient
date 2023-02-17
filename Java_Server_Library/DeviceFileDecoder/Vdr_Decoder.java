package DeviceFileDecoder;

import Extension_Methods.Convert;
import Time_Component.Custom_DateTime;
import VDR_Object.*;

import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Vdr_Decoder implements FileDecoder{

    Map<String,String> malFunctionType =  new HashMap<>(){
        {
        put("1", "GPS模組故障");
        put("2", "印表機模組故障");
        put("3","顯示器模組故障");
        }
    };
    Map<String,String> EventType = new HashMap<>() {
        {
            put("1", "電源供應中斷");
            put("2", "安全防護攻擊");
            put("3", "參數調整");
            put("4", "時間調整");
            put("5", "USB下載一天");
            put("6", "USB下載小時");
            put("7", "RS232下載一天");
            put("8", "RS232下載小時");
            put("9", "列印機缺紙");
            put("A", "列印駕駛活動");
            put("B", "列印事件故障");
            put("C", "列印技術資料");
            put("D", "列印曲線圖");
        }
    };
    Map<String,String> DriverActivityEventType = new HashMap<>() {
        {
            put("0", "車停中");
            put("1", "行駛中");
            put("2", "待班中");
            put("3", "休息中");
        }
    };

    public enum VdrBlockType {
        SAMSUNG, NOKIA, APPLE
    }

    int objectNumb;
    String checkSum;
    byte[] rawData;

    private List<tb_ioStatus_data>                  ioStatus_dataList;
    private tb_carInformation_data                  carInformation;
    Map<Custom_DateTime,tb_malfunction_data>        tb_malfunction_dataList;
    Map<Custom_DateTime,tb_event_data>              tb_event_dataList;
    private List<tb_speed_data>                     tb_speed_dataList;
    List<tb_Location_data>                          tb_location_dataList;
    Map<Custom_DateTime,tb_fixLog_data>             tb_fixLog_dataList;
    Map<Custom_DateTime,tb_fixTime_data>            tb_fixTime_dataList;
    Map<Custom_DateTime,tb_continousTime_data>      tb_continousTime_dataList;
    Map<Custom_DateTime,tb_driverStop_data>         tb_driverStop_dataList;
    Map<Custom_DateTime,tb_carRecord_data>          tb_carRecord_dataList;
    List<tb_driverActivity_data>                    tb_driverActivity_dataList;
    List<tb_rpm_data>                               tb_rpm_dataList;


    public Vdr_Decoder(byte[] originalData){

        this.rawData=originalData;
        this.ioStatus_dataList          = new ArrayList<>();
        this.tb_rpm_dataList            = new ArrayList<>();
        this.tb_speed_dataList          = new ArrayList<>();
        this.tb_carRecord_dataList      = new HashMap<>();
        this.tb_continousTime_dataList  = new HashMap<>();
        this.tb_driverStop_dataList     = new HashMap<>();
        this.tb_location_dataList       = new ArrayList<>();
        this.tb_driverActivity_dataList = new ArrayList<>();
        this.tb_malfunction_dataList    = new HashMap<>();
        this.tb_event_dataList          = new HashMap<>();
        this.tb_fixLog_dataList         = new HashMap<>();
        this.tb_fixTime_dataList        = new HashMap<>();

    }
    public void TableGenerate() {
        this.objectNumb = Convert.ToInt(this.rawData,0,2);
        int currentIndex = 2;
        int  BlockIndex =-1;
        String BlockRecordName="test";
        while (currentIndex<this.rawData.length){
            try {
                BlockIndex = Convert.ToInt(this.rawData,currentIndex,1);
                currentIndex+=1;
                BlockRecordName = new String(Arrays.copyOfRange(this.rawData, currentIndex,currentIndex+18), Charset.forName("big5"));
                currentIndex+=18;
                long BlockLens = Convert.ToLong(this.rawData,currentIndex,4);
                currentIndex+=4;
                byte []DATA = Arrays.copyOfRange(this.rawData, currentIndex, (int) (currentIndex+BlockLens));
                currentIndex+=BlockLens;
                System.out.println(currentIndex+","+BlockIndex+","+BlockRecordName);
                switch(BlockIndex) {
                    case 0:
//                        this.tb_malfunction_dataList.add(new tb_malfunction_data());
                    case 1:
//                        this.tb_event_dataList.put(new Custom_DateTime,new tb_event_data());
                        break;
                    case 2:
                        this.tb_speed_dataList.addAll(tb_speed_dataDecode(DATA));
                        break;
                    case 3:
                        this.carInformation = new tb_carInformation_data(DATA);
                        break;
                    case 4:
//                        this.tb_fixLog_dataList.put(new tb_fixLog_data());
                        break;
                    case 5:
//                        this.tb_fixTime_dataList.put(new tb_fixTime_data());
                        break;
                    case 6:
//                        this.tb_continousTime_dataList.put(new tb_continousTime_data());
                        break;
                    case 7:
//                        this.tb_driverStop_dataList.put(new tb_driverStop_data());
                        break;
                    case 8:
                        this.tb_driverActivity_dataList.addAll(tb_driverActivity_dataDecode(DATA));
                        break;
                    case 9:
                        this.tb_location_dataList.addAll(tb_location_dataDecode(DATA));
                        break;
                    case 10:
                        this.ioStatus_dataList.addAll(tb_ioStatus_dataDecode(DATA));
                        break;
                    case 11:
                        this.tb_carRecord_dataList.putAll(tb_carRecord_dataDecode(DATA));
                        break;
                    case 12:
                        this.tb_rpm_dataList.addAll(tb_rpm_dataDecode(DATA));
                        break;
                    default:
                        System.out.println("debugg");
                }
            }
            catch (Exception e){
                System.out.println(e.fillInStackTrace());
            }
        }
    }

    @Override
    public List<PackageData> logDataGenerate() {
        List<PackageData> logData = new ArrayList<>();

        for (tb_speed_data speed_data:this.tb_speed_dataList){
              PackageData logIndex = new PackageData();
              logIndex.setSpeed(speed_data.getSpeed().floatValue());
              logIndex.setTime(speed_data.getCurrentTime());
              logIndex.setCourse(0);
              logIndex.setDeviceStatus( "0".repeat(5));
              logData.add(logIndex);
        }
        Iterator<tb_Location_data>  tb_location_dataIterator                    =   this.tb_location_dataList.iterator();
        Iterator<tb_rpm_data>       tb_rpm_dataIterator                         =   this.tb_rpm_dataList.iterator();
        Iterator<tb_ioStatus_data>  tb_ioStatus_dataIterator                    =   this.ioStatus_dataList.iterator();
        Iterator<tb_driverActivity_data>  tb_driverActivity_dataIterator        =   this.tb_driverActivity_dataList.iterator();

        AtomicReference<tb_Location_data>       currentLocationIterator     = new AtomicReference<>(tb_location_dataIterator.next());
        AtomicReference<tb_rpm_data>            currentRpmIterator          = new AtomicReference<>(tb_rpm_dataIterator.next());
        AtomicReference<tb_ioStatus_data>       currentIostatusIterator     = new AtomicReference<>(tb_ioStatus_dataIterator.next());
        AtomicReference<tb_driverActivity_data> currentDriverActivity       = new AtomicReference<>(tb_driverActivity_dataIterator.next());
        logData.forEach(
                data ->{
                    try {
                        while (data.getTime().after(currentLocationIterator.get().getStartTime()))
                                currentLocationIterator.set(tb_location_dataIterator.next());
                        while (data.getTime().after(currentRpmIterator.get().getTime()))
                            currentRpmIterator.set(tb_rpm_dataIterator.next());
                        while (data.getTime().after(currentIostatusIterator.get().getTime()))
                            currentIostatusIterator.set(tb_ioStatus_dataIterator.next());
                        while (data.getTime().after(currentDriverActivity.get().getActivityDate()))
                            currentDriverActivity.set(tb_driverActivity_dataIterator.next());

                        data.setRpm(currentRpmIterator.get().getRpm());
                        data.setLongitude(currentLocationIterator.get().getLongitude());
                        data.setLatitude(currentLocationIterator.get().getLatitude());
                        data.setGpsSpeed(currentRpmIterator.get().getGpsSpeed());
                        data.setIoStatus(currentRpmIterator.get().getIo_status());
                        data.setDistance(currentIostatusIterator.get().getDistance());
                        data.setDriverId(currentDriverActivity.get().getDriverID());
                    }catch (Exception e){
                        e.getStackTrace();
                    }
                }
        );
        return logData;
    }

    public List<tb_ioStatus_data> tb_ioStatus_dataDecode(byte[] DATA) {
        int Data_Count = Convert.ToInt(DATA,0,2);
        return IntStream.iterate(2, i -> i + 12)
                .limit(Data_Count)
                .mapToObj(j -> new tb_ioStatus_data(Arrays.copyOfRange(DATA, j, Math.min(DATA.length, j + 12))))
                .collect(Collectors.toList());
    }
    public List<tb_rpm_data> tb_rpm_dataDecode(byte[] DATA) {
        int Data_Count = Convert.ToInt(DATA,0,4);
        return IntStream.iterate(4, i -> i + 9)
                .limit(Data_Count)
                .mapToObj(j -> new tb_rpm_data(Arrays.copyOfRange(DATA, j, Math.min(DATA.length, j + 9))))
                .collect(Collectors.toList());
    }
    public List<tb_speed_data> tb_speed_dataDecode(byte[] DATA) throws Exception {
        int Data_Count = Convert.ToInt(DATA,0,2);
        List<tb_speed_data>  tb_speed_dataList = new ArrayList<>();
        for (int currentBlockData=2;currentBlockData<Data_Count*127;currentBlockData+=127){
            Custom_DateTime startTime = new Custom_DateTime(Arrays.copyOfRange(DATA,currentBlockData,currentBlockData+7));
            for (int currentIndex = 7;currentIndex<127 ;currentIndex+=2){
                Double speedFirstSec =  Convert.ToDouble(DATA,currentBlockData+currentIndex,1);
                Double speedSecondSec = Convert.ToDouble(DATA,currentBlockData+currentIndex+1,1);
                tb_speed_data currentIndexTable = new tb_speed_data((speedFirstSec+speedSecondSec)/2,new Custom_DateTime(startTime).AddSecond((currentIndex/2)+1));
                tb_speed_dataList.add(currentIndexTable);
            }
        }return tb_speed_dataList;
    }
    public List<tb_Location_data> tb_location_dataDecode(byte[] DATA) {
        int Data_Count = Convert.ToInt(DATA,0,2);
        return IntStream.iterate(2, i -> i + 18)
                .limit(Data_Count)
                .mapToObj(j -> new tb_Location_data(Arrays.copyOfRange(DATA, j, Math.min(DATA.length, j + 18))))
                .collect(Collectors.toList());
    }

    public List<tb_driverActivity_data> tb_driverActivity_dataDecode(byte[] DATA) {
        int Data_Count = Convert.ToInt(DATA,0,2);
        return IntStream.iterate(2, i -> i + 26)
                .limit(Data_Count)
                .mapToObj(j -> new tb_driverActivity_data(Arrays.copyOfRange(DATA, j, Math.min(DATA.length, j + 26))))
                .collect(Collectors.toList());
    }
    public Map<Custom_DateTime,tb_carRecord_data> tb_carRecord_dataDecode(byte[] DATA) {
        int Data_Count = Convert.ToInt(DATA,0,2);
        return IntStream.iterate(2, i -> i + 9)
                .limit(Data_Count)
                .mapToObj(j -> new tb_carRecord_data(Arrays.copyOfRange(DATA, j, Math.min(DATA.length, j + 9))))
                .collect(Collectors.toMap(tb_carRecord_data::getCurrentTime,Function.identity()));
    }

    public tb_carInformation_data getCarInformation() { return carInformation; }
}