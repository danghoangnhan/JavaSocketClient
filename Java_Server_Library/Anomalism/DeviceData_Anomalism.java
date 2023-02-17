package Anomalism;

import DB_JassFleet_Object.Complex.Car_Station;
import DB_JassFleet_Object.Data.tb_log_data;
import DB_JassFleet_Object.Index.tb_information_anomalism;
import DB_JassFleet_Object.Report.tb_events_detail;
import DB_JassFleet_Object.Route.tb_road_line;
import DB_Table_Operate.DB_JassLin_Fleet.*;
import Time_Component.Custom_DateTime;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toCollection;

public class DeviceData_Anomalism implements Runnable {

    private static final Log logger = LogFactory.getLog(Operate_tb_log_data.class);

    private Custom_DateTime startTime;
    private Custom_DateTime endTime;
    private tb_information_anomalism abnormalParameter;
    private ArrayList<tb_log_data> logData;
    private List<tb_events_detail> eventList;
    private String deviceName;
    private Car_Station Car_Station;

    private Operate_events_detail  operate_events_detail;
    private Operate_information_anomalism operate_information_anomalism;
    private Operate_tb_log_data operate_tb_log_data;
    private Operate_road_line operate_road_line;
    private  LocationStore locationStore;

    public DeviceData_Anomalism(String data) throws Exception {
        this();
        try {
            Map<String,Object> ObjectValue   = new ObjectMapper().readValue(data, new TypeReference<>() {});
            this.deviceName = ObjectValue.get("CarNumber").toString();
            this.startTime = new Custom_DateTime(ObjectValue.get("startTime").toString());
            this.endTime = new Custom_DateTime(ObjectValue.get("endTime").toString());
        }
        catch (Exception e){
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }


    public DeviceData_Anomalism(
            String deviceName,
            tb_information_anomalism abnormalParameter,
            Car_Station car_Station,
            String startTime,
            String endTime,
            LocationStore locationStore
    ) throws Exception {
        this();
        this.deviceName = deviceName;
        this.abnormalParameter = abnormalParameter;
        this.Car_Station = car_Station;
        this.startTime = new Custom_DateTime(startTime);
        this.endTime = new Custom_DateTime(endTime);
        this.locationStore =   locationStore;
    }
    private DeviceData_Anomalism() throws Exception {
        this.operate_events_detail = Operate_JassFleet_Table_Factory.GetInstance().getOperate_events_detail();
        this.operate_information_anomalism = Operate_JassFleet_Table_Factory.GetInstance().getOperate_f_information_anomalism();
        this.operate_tb_log_data = Operate_JassFleet_Table_Factory.GetInstance().getOperateTbLogData();
        this.operate_road_line = Operate_JassFleet_Table_Factory.GetInstance().getOperate_road_line();
        this.logData = new ArrayList<>();
        this.eventList = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            checkOverSpeedEvent();
            if(this.eventList.size()>0){
                this.operate_events_detail.DeleteRecordWithDate(startTime.getDateTimeString(), endTime.getDateTimeString(), this.deviceName);
                this.operate_events_detail.Insert(this.eventList);
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }


    public int nextIndexTime(int startIndex) {

        Custom_DateTime timeBreakEnd = new Custom_DateTime(this.logData.get(startIndex).getDateTime());
        timeBreakEnd.AddSecond(this.abnormalParameter.getBreakingTime());
        int result = startIndex;
        while (result < this.logData.size() && this.logData.get(result).getDateTime().before(timeBreakEnd)) {
            result++;
        }
        return result;
    }

    public boolean checkOverSpeed(int startIndex) {
        Custom_DateTime lastTimeOverSpeed = new Custom_DateTime(this.logData.get(startIndex).getDateTime());
        lastTimeOverSpeed.AddSecond(this.abnormalParameter.getOverTime() + 1);
        IntPredicate endOverLimit = index -> this.logData.get(index).getSpeed() <= this.abnormalParameter.getSpeed_limit() && this.logData.get(index).getDateTime().before(lastTimeOverSpeed);
        boolean result = IntStream.range(startIndex, this.logData.size()).anyMatch(endOverLimit);
        return !result;
    }
    public ArrayList<tb_log_data> getLogData(String CarNumber, @NotNull Custom_DateTime startTime, @NotNull Custom_DateTime endTime) throws Exception {
        ArrayList<tb_log_data> result;
        if (startTime.GetYear()==endTime.GetYear()){
            String tableName = "g_"+CarNumber+"_"+startTime.GetYear();
            result =  this.operate_tb_log_data.getLogTable(tableName, startTime.getDateTimeString(), endTime.getDateTimeString());
        }else {
            Custom_DateTime firstLimit = new Custom_DateTime(startTime.GetYear(),startTime.GetMonth(),startTime.GetDay(),23,59,59);
            Custom_DateTime secondLimit = new Custom_DateTime(endTime.GetYear(),endTime.GetMonth(), endTime.GetDay(), 0,0,0);
            String firstTableName = "g_"+CarNumber+"_"+startTime.GetYear();
            String secondTableName = "g_"+CarNumber+"_"+endTime.GetYear();
            ArrayList <tb_log_data> firstLogData = getLogData(firstTableName,startTime,firstLimit);
            ArrayList <tb_log_data> secondLogData = getLogData(secondTableName,secondLimit,endTime);
            firstLogData.addAll(secondLogData);
            result = firstLogData;
        }
        return (result != null)?result: new ArrayList<>();
    }



    public void checkOverSpeedEvent() {
        try {
            this.logData = getLogData(this.deviceName, startTime, endTime);

            if (this.logData.size() == 0) {
                System.out.println("device:"+this.deviceName+
                        ",startTime:"+startTime.getDateTimeString()+
                        ",endTime:"+endTime.getDateTimeString()+
                        ",data record not found");
                return;
            }
            if (this.Car_Station!=null&&this.Car_Station.getCar().getCarType()!=null &&this.Car_Station.getCar().getCarType().equals("自訂道路")) {
                setTripsIdInLogData();
                ArrayList<tb_information_anomalism> abnormalParameters = getAbnormalParametersFromTripsId();
                System.out.println("starting analysing data from " + this.startTime.getDateTimeString() + " to " + endTime.getDateTimeString() + " with Car Number:" + this.deviceName);
                int startRange = firstIndexValueWithSpeedCheck(0,abnormalParameters);
                while (startRange < this.logData.size()) {
                    tb_log_data startMoment = this.logData.get(startRange);
                    tb_information_anomalism abnormalParameter = abnormalParameters.get(startRange);

                    boolean isSpeedAllLargerInOverSpeedRange = checkOverSpeedCustomRoute(startRange,abnormalParameters);
                    boolean isRoadIDsAllMatchInOverSpeedRange = checkRoadIDs(startRange,abnormalParameters);

                    if (startMoment.getSpeed() > abnormalParameter.getSpeed_limit() && isSpeedAllLargerInOverSpeedRange && isRoadIDsAllMatchInOverSpeedRange) {
                        int endRange = nextIndexValueCustomRoute(startRange + 1,abnormalParameters);

                        if (endRange >= this.logData.size())
                            break;
                        tb_log_data endMoment = this.logData.get(endRange);
                        if (endMoment.getSpeed() <= abnormalParameter.getSpeed_limit()) {
                            double maxSpeed = getMaxSpeedInRange(startRange,endRange,this.logData);
                            this.eventList.add(new tb_events_detail(this.deviceName,this.logData.get(startRange), this.logData.get(endRange - 1), maxSpeed, abnormalParameter, this.logData.get(startRange).getTripsId(), this.logData.get(startRange).getRoadName()));
                            startRange = nextIndexTimeCustomRoute(endRange,abnormalParameters);
                        }
                    } else {
                        // case: speed doesnt match, skip index to next speed match
                        if (startMoment.getSpeed() <= abnormalParameter.getSpeed_limit() || !isSpeedAllLargerInOverSpeedRange) {
                            startRange = firstIndexValueWithSpeedCheck(startRange + 1,abnormalParameters);
                        } else {
                            // case: speed match but road ids doesnt match, skip index to next road id
                            startRange = firstIndexValueWithRoadIDCheck(startRange + 1);  
                        }
                    }
                }
                ShowResultInfo(this.deviceName,this.startTime,this.endTime,this.eventList.size());
            } else {
                if (this.abnormalParameter == null){
                    logger.info("device:" + this.deviceName + " doesn't have abnormal information, exiting...");
                    return;
                }
                    ShowDebugInfo(this.abnormalParameter,this.startTime,this.endTime);
                    int startRange = firstIndexValue(0);
                    while (startRange < this.logData.size()) {
                      tb_log_data startMoment = this.logData.get(startRange);
                      if (startMoment.getSpeed() > this.abnormalParameter.getSpeed_limit() && checkOverSpeed(startRange)) {
                          int endRange = nextIndexValue(startRange + 1);

                          if (endRange >= this.logData.size())
                              break;
                          tb_log_data endMoment = this.logData.get(endRange);
                          if (endMoment.getSpeed() <= this.abnormalParameter.getSpeed_limit()) {

                              double maxSpeed = getMaxSpeedInRange(startRange,endRange,this.logData);
                              this.eventList.add(new tb_events_detail(this.deviceName,this.logData.get(startRange), this.logData.get(endRange - 1), maxSpeed,this.abnormalParameter));
                              startRange = nextIndexTime(endRange);
                          }
                      } else {
                          startRange = firstIndexValue(startRange + 1);
                      }
                  }
                ShowResultInfo(this.deviceName,this.startTime,this.endTime,this.eventList.size());
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }
    
    public boolean checkOverSpeedCustomRoute(int startIndex, @NotNull List<tb_information_anomalism> abnormalParameters) {
        Custom_DateTime lastTimeOverSpeed = new Custom_DateTime(this.logData.get(startIndex).getDateTime());
        lastTimeOverSpeed.AddSecond(abnormalParameters.get(startIndex).getOverTime() + 1);
        IntPredicate endOverLimit = index -> this.logData.get(index).getSpeed() <= abnormalParameters.get(index).getSpeed_limit() && this.logData.get(index).getDateTime().before(lastTimeOverSpeed);
        boolean result = IntStream.range(startIndex, this.logData.size()).anyMatch(endOverLimit);
        return !result;
    }

    public boolean checkRoadIDs(int startIndex, @NotNull List<tb_information_anomalism>abnormalParameters) {
        Custom_DateTime lastTimeOverSpeed = new Custom_DateTime(this.logData.get(startIndex).getDateTime());
        lastTimeOverSpeed.AddSecond(abnormalParameters.get(startIndex).getOverTime() + 1);
        IntPredicate endOverLimit = index -> !this.logData.get(index - 1).getTripsId().equals(this.logData.get(index).getTripsId()) && this.logData.get(index).getDateTime().before(lastTimeOverSpeed);
        if (startIndex == 0) { // function will fail at index 0
            startIndex = 1;
        }
        return IntStream.range(startIndex, this.logData.size()).noneMatch(endOverLimit);
    }
    public void setTripsIdInLogData() throws Exception {
        tb_road_line prevLine = new tb_road_line("",-1);
        ArrayList<tb_road_line> data = this.locationStore.getData(this.logData,this.Car_Station);
        for (int i = 0; i < this.logData.size(); i++) {
            tb_road_line dataI = data.get(i);
            // to make sure road id change is leading to new route and not intersection with other route
            if (!dataI.getRoadId().equals(prevLine.getRoadId())) {
                int futurePointsCount = 5; // number of future points to check
                if (i + futurePointsCount >= this.logData.size()) { // prevent out of bound edge case
                    futurePointsCount = this.logData.size() - i;
                }
                List<tb_log_data> subList = this.logData.subList(i, i + futurePointsCount - 1);
                ArrayList<tb_road_line> _data = new ArrayList<>(data.subList(i, i + futurePointsCount - 1));
                ArrayList<Integer> futureRoadIds = IntStream
                        .range(0, subList.size())
                        .mapToObj(j->_data.get(j).getRoadId())
                        .collect(toCollection(ArrayList::new));

                if (!compareRoadIDFrequency(futureRoadIds, dataI.getRoadId(), prevLine.getRoadId())) {
                    dataI = prevLine;
                }
            }
            this.logData.get(i).setTripsId(dataI.getRoadId());
            this.logData.get(i).setRoadName(dataI.getRoadName());
            prevLine = dataI;
        }
    }


    public Boolean compareRoadIDFrequency(List<Integer> futureRoadIds,
                                          Integer roadId,
                                          Integer prevRoadId) {
        int roadIdCount = Collections.frequency(futureRoadIds, roadId);
        int prevRoadIdCount = Collections.frequency(futureRoadIds, prevRoadId);
        return roadIdCount >= prevRoadIdCount;
    }

    // trips id = road id in road_route
    // trips id = para_4 in information_anomalism
    public ArrayList<tb_information_anomalism> getAbnormalParametersFromTripsId() throws Exception {
        ConcurrentHashMap<Integer, tb_information_anomalism> abnormalParametersMap = this.operate_information_anomalism.getCustomeRoadAbnormalSettingsWithAreaIDRaws("020",this.Car_Station.getStation().getAreaID());
        // trips id = road id in road_route
        // trips id = para_4 in information_anomalism
        return this.logData.stream().map(logDatum-> {
            if (logDatum.getTripsId() != -1 && abnormalParametersMap.containsKey(logDatum.getTripsId())) {
                    return new tb_information_anomalism(abnormalParametersMap.get(logDatum.getTripsId()));
            } else {
                return new tb_information_anomalism("020");
            }
        }).collect(toCollection(ArrayList::new));
    }

    public int nextIndexTimeCustomRoute(int startIndex, @NotNull ArrayList<tb_information_anomalism>abnormalParameters) {
        Custom_DateTime timeBreakEnd = new Custom_DateTime(this.logData.get(startIndex).getDateTime());
        timeBreakEnd.AddSecond(abnormalParameters.get(startIndex).getBreakingTime());
        int result = startIndex;
        while (result < this.logData.size() && this.logData.get(result).getDateTime().before(timeBreakEnd)) { result++; }
        return result;
    }

    public int nextIndexValueCustomRoute(int nextIndex, List<tb_information_anomalism>abnormalParameters) {
        // abnormal param is same for same road IDs
        tb_information_anomalism abnormalParam = abnormalParameters.get(nextIndex);
        return IntStream
                .range(nextIndex, this.logData.size())
                .filter(i -> abnormalParam.getSpeed_limit() > 0)
                .filter(i->this.logData.get(i).getSpeed() <= abnormalParam.getSpeed_limit())
                .findFirst()
                .orElse(this.logData.size());
    }

    public int nextIndexValue(int nextIndex) {
        return IntStream
                .range(nextIndex, this.logData.size())
                .filter(isOverSpeed(this.logData,this.abnormalParameter).negate())
                .findFirst()
                .orElse(this.logData.size());
    }
    public int firstIndexValue(int startIndex) {
        return IntStream
                .range(startIndex, this.logData.size())
                .filter(isOverSpeed(this.logData,this.abnormalParameter))
                .findFirst()
                .orElse(this.logData.size());
    }
    public int firstIndexValueWithSpeedCheck(int startIndex,ArrayList<tb_information_anomalism>abnormalParameters) {
        return   IntStream
                .range(startIndex, this.logData.size())
                .filter(positiveSpeedLimit(abnormalParameters).and(isOverSpeed(this.logData,abnormalParameters)))
                .findFirst()
                .orElse(this.logData.size());
    }

    public int firstIndexValueWithRoadIDCheck(int startIndex) {
        return   IntStream
                .range(startIndex, this.logData.size())
                .filter(i -> !this.logData.get(i - 1).getTripsId().equals(this.logData.get(i).getTripsId()))
                .findFirst()
                .orElse(this.logData.size());
    }

    public Double getMaxSpeedInRange(int startRange,int endRange,ArrayList<tb_log_data> dataList){
        return  IntStream
                .range(startRange, endRange)
                .mapToDouble(index -> dataList.get(index).getSpeed())
                .max()
                .orElseThrow(NoSuchElementException::new);
    }
    public void ShowDebugInfo(@NotNull tb_information_anomalism abnormalParameter, @NotNull Custom_DateTime startTime, @NotNull Custom_DateTime endTime){
        logger.info("starting analysing data from " + startTime.getDateTimeString() + " to " + endTime.getDateTimeString() + " with" +
                "\n Car Number:" + this.deviceName+
                "\n abnormal parameter:" +      abnormalParameter.getSpeed_limit() +
                "\n Over Time:" +               abnormalParameter.getOverTime() +
                "\n Breaking Time:" +           abnormalParameter.getBreakingTime() +
                "\n Para4:" +                   abnormalParameter.getPara_4() +
                "\n Para5:" +                   abnormalParameter.getPara_5());
    }
    public void ShowResultInfo(String deviceName, @NotNull Custom_DateTime startTime, @NotNull Custom_DateTime endTime, Integer eventListSize) {
        logger.info(" analysing data has end with Car Number:" + deviceName + ",from " + startTime.getDateTimeString() + "to " + endTime.getDateTimeString() + " found " + eventListSize + " event 020 data");
    }
    IntPredicate positiveSpeedLimit(ArrayList<tb_information_anomalism>abnormalParameters){ return i->abnormalParameters.get(i).getSpeed_limit() > 0; }
    IntPredicate isOverSpeed(List<tb_log_data>logData,ArrayList<tb_information_anomalism>abnormalParameters){ return i-> logData.get(i).getSpeed() > abnormalParameters.get(i).getSpeed_limit(); }
    IntPredicate isOverSpeed(List<tb_log_data>logData,tb_information_anomalism abnormalParameter){ return i-> logData.get(i).getSpeed() > abnormalParameter.getSpeed_limit(); }
    Predicate<tb_information_anomalism> isPositiveSpeed  = element -> element.getSpeed_limit()>0;
}