package DeviceData;

import DB_JassFleet_Object.Index.tb_information_car;
import DB_JassFleet_Object.Data.tb_log_data;
import DB_Table_Operate.DB_JassLin_Fleet.Operate_JassFleet_Table_Factory;
import Time_Component.Custom_DateTime;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jettison.json.JSONObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class DeviceData_Log implements Runnable {

    private static final Log logger = LogFactory.getLog(DeviceData_Log.class);
    private ConcurrentHashMap<String, Boolean> existTableMap;
    private ConcurrentHashMap<String, tb_information_car> lastAccStatusMap;
    private String data;
    private List<tb_log_data> logDataList;
    private Map<String,List<tb_log_data>> logDataTable;
    private String rawData;
    private List<JSONObject> tripList;
    private List<Map<String,Object>> ObjectDetailList = null;
    private Map<String,Object>       ObjectList = null;

    public DeviceData_Log(String _data, ConcurrentHashMap<String, Boolean> _existTableMap, ConcurrentHashMap<String, tb_information_car> _lastAccStatusMap) {
        try {
            this.data = _data;
            this.existTableMap = _existTableMap;
            this.lastAccStatusMap = _lastAccStatusMap;
            this.tripList = new ArrayList<>();
        }catch (Exception e){
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public void run() {
        try {
            logDataList = processDataNew();
            logDataList = filterData(logDataList);
            logDataTable = classificateTable(logDataList);
            InsertDatabase(logDataTable);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    public void checkTableExist(String TableName) throws Exception {
        if (!existTableMap.containsKey(TableName)) {
            Boolean tableExist = Operate_JassFleet_Table_Factory.GetInstance().getOperateTbLogData().CheckTableExist(TableName);
            if (!tableExist) {
                Operate_JassFleet_Table_Factory.GetInstance().getOperateTbLogData().CreateTable(TableName);
            }
            existTableMap.put(TableName, true);
        }
    }
    public void checkLastAccSatus(List<tb_log_data> dataList) throws Exception {
        for (tb_log_data DataIndex:dataList){
            tb_information_car currentDeviceInfor = this.lastAccStatusMap.get(DataIndex.getSerial());
            if (!DataIndex.getLastAccStatus(0).equals(currentDeviceInfor.getLastAcc_Status_IO())){
                    if (DataIndex.getLastAccStatus(0).equals("0")){
                        JSONObject tripEvent = new JSONObject();
                        tripEvent.put("Serial", currentDeviceInfor.getCarID());
                        tripEvent.put("Start_Time",currentDeviceInfor.getLastStatus_Date().getTimeString());
                        tripEvent.put("End_Time", DataIndex.getDateTime().getDateTimeString());
                        this.tripList.add(tripEvent);
                    }
                    currentDeviceInfor.setLastAcc_Status_IO(Integer.parseInt(DataIndex.getLastAccStatus(1)));
                    currentDeviceInfor.setLastStatus_Date(DataIndex.getDateTime());
            }
        }
    }
    public List<tb_log_data> processDataNew(){
        List<tb_log_data> logDataList = null;
        ForkJoinPool customThreadPool = null;
        AtomicInteger index = new AtomicInteger(0);
        try {
            JSONObject data = new JSONObject(this.data);
            this.rawData = Objects.toString(data.get("encodeData"),"");
            ObjectDetailList   = new ObjectMapper().readValue(Objects.toString(data.get("LogData"), ""), new TypeReference<>() {});
            ObjectList         = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(data.get("decodeData").toString(), HashMap.class);
            customThreadPool = new ForkJoinPool(ObjectDetailList.size());
            ObjectDetailList.parallelStream().forEach(element->element.putAll(ObjectList));
            logDataList = customThreadPool.submit(
                    () -> ObjectDetailList
                            .stream()
                            .map(tb_log_data::new)
                            .map(tb_log_data::fixUpperCaseTable)
                            .map(element->element.setDateTime(element.getDateTime().AddSecond(-1 * (29 - index.getAndIncrement()))))
                            .collect(Collectors.toList())).get();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            customThreadPool.shutdown();
        }
        return logDataList;
    }
    public void InsertDatabase(Map<String,List<tb_log_data>> tableList) {
        tableList.forEach((key, value) -> {
            try {
                checkTableExist(key);
                Operate_JassFleet_Table_Factory.GetInstance()
                        .getOperateTbLogData()
                        .Insert(key, value);
            } catch (Exception e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        });
    }
    public List<tb_log_data> filterData(List<tb_log_data> log_dataList) throws Exception {
        ListIterator<tb_log_data> log_dataIterator = log_dataList.listIterator();
        Custom_DateTime currentTime = new Custom_DateTime();
        while (log_dataIterator.hasNext()){
            tb_log_data logDataElement = log_dataIterator.next();
            Custom_DateTime checkTime = new Custom_DateTime(logDataElement.getDateTime()).AddHour(-8);
            if (checkTime.Subtract(currentTime).getDateTime().getTime()/1000>60) {
                logger.error("error with carnumber:"  + logDataElement.getSerial() +
                                "\n arrived datetime(UTC):"           + currentTime.getDateTimeString() +
                                "\n packet datetime(UTC):"            + checkTime.getDateTimeString() +
                                "\n originData:"                + rawData +
                                "\n the date time is after the insert time");
                log_dataIterator.remove();
            }
        }
       return log_dataList.parallelStream().collect(Collectors.toList());
    }
    public  Map<String,List<tb_log_data>> classificateTable(List<tb_log_data> originalListData){
        return originalListData.stream().parallel().collect(Collectors.groupingBy(tb_log_data::getTableName));
    }

    public List<tb_log_data> getLogDataList() {
        return logDataList;
    }

    public void setLogDataList(List<tb_log_data> logDataList) {
        this.logDataList = logDataList;
    }
}