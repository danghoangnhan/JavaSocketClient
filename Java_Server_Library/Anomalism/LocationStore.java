package Anomalism;

import DB_JassFleet_Object.Complex.Car_Station;
import DB_JassFleet_Object.Data.tb_log_data;
import DB_JassFleet_Object.Route.tb_road_line;
import DB_Table_Operate.DB_JassLin_Fleet.Operate_JassFleet_Table_Factory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LocationStore {
    private HashMap<String, tb_road_line> roadLineCahe;
    public LocationStore() {
        roadLineCahe = new HashMap<>();
    }
    public  ArrayList<tb_road_line> getData(@NotNull ArrayList<tb_log_data> logData, Car_Station Car_Station) throws Exception {

        ArrayList<tb_log_data> newLocation = logData.stream()
                .filter(element-> !this.conTain(element))
                .filter(distinctByKey(tb_log_data::getLocationString))
                .collect(Collectors.toCollection(ArrayList::new));

        upDate(newLocation,Car_Station);
        ArrayList<tb_road_line> result = logData.stream().map(element->getData(element)).collect(Collectors.toCollection(ArrayList::new));
        return  result;
    }
    public tb_road_line getData(@NotNull tb_log_data logData){
    return this.roadLineCahe.get(logData.getLocationString());
}
    public boolean conTain(@NotNull tb_log_data logData){
        return this.roadLineCahe.containsKey(logData.getLocationString());
    }

    public  void upDate(ArrayList<tb_log_data> logData,Car_Station car_station) throws Exception {
        ArrayList<tb_road_line> data = Operate_JassFleet_Table_Factory.GetInstance().getOperate_road_line().getInDistanceRoadIDs(logData,car_station);
        for(int i =0 ;i<data.size();i++){
            roadLineCahe.put(logData.get(i).getLocationString(),data.get(i));
        }
    }
    @NotNull
    @Contract(pure = true)
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor)
    {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
