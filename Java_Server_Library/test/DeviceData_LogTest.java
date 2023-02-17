package test;
import DB_JassFleet_Object.Index.tb_information_car;
import DB_JassFleet_Object.Data.tb_log_data;
import DB_Table_Operate.DB_JassLin_Fleet.Operate_JassFleet_Table_Factory;
import DB_Table_Operate.DB_Kafka.Operate_Kafka_Factory;
import Device.clientConstants;
import DeviceData.DeviceData_Log;
import Kafka_Object.Kafka_LogData_Detail_Object;
import Packet_Component.Package;

import Device.JAS106_Client;
import Device.JAS208_Client;
import Time_Component.Custom_DateTime;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DeviceData_LogTest implements clientConstants {
    private ConcurrentHashMap<String, Boolean> existTableMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, tb_information_car> lastAccStatusMap;

    @Test
    public void Test2() {

    }


    public void testTemplate(String filename106Dir, String fileName208Dir, int CarNumb, String targetIP, String target106Port, String target208Port, int delayTime, boolean fixDate) throws InterruptedException {

        List<String> packetList106 = JAS106_Client.readBinFile(filename106Dir);
        List<String> packetList208 = JAS208_Client.readBinFile(fileName208Dir);

        LinkedList<AtomicInteger> Device106Result = new LinkedList<>(Collections.nCopies(CarNumb, new AtomicInteger(0)));
        LinkedList<AtomicInteger> Device208Result = new LinkedList<>(Collections.nCopies(CarNumb, new AtomicInteger(0)));

        ExecutorService executorService = Executors.newFixedThreadPool(CarNumb * 2);

        Custom_DateTime startTime = new Custom_DateTime();
        System.out.println("start at: " + startTime.getDateTimeString());

        for (int i = 0; i < CarNumb; i++) {
            JAS106_Client JAS106_Test = new JAS106_Client(
                    "106_TestDevice_" + i, i,
                    packetList106,
                    delayTime,
                    targetIP,
                    target106Port,
                    fixDate);

            executorService.execute(JAS106_Test);

            JAS208_Client JAS208_Test = new JAS208_Client(
                    "208_TestDevice_" + i,
                    i,
                    packetList208,
                    delayTime,
                    targetIP,
                    target208Port,
                    fixDate);
            executorService.execute(JAS208_Test);
        }
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        ;
        System.out.println("end at: " + new Custom_DateTime().getDateTimeString());
        System.out.println(Device106Result.stream().mapToInt(AtomicInteger::intValue).sum() + " 106 has been sent");
        System.out.println(Device208Result.stream().mapToInt(AtomicInteger::intValue).sum() + " 208 has been sent");
    }

    @Test
    public void testConstructor() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("encodeData","$LOG40,864292042623254,MARK01,466891006473726,12345678,030522,075129,A,27,18,011958448,30,c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000c�<\u0007��}\u0001\rw\u0014�(\u0000,8F8E#");
        obj.put("LogData","[{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"}," +
                "{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"}," +
                "{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"}," +
                "{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"}," +
                "{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"},{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"},{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"},{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"},{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"},{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"},{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"},{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"},{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"},{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"},{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"},{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"},{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"},{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"},{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"},{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"},{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"},{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"},{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"},{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"},{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"},{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"},{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"},{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"},{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"},{\"longitude\":121421155,\"latitude\":25031860,\"direction\":130,\"speed\":119,\"rpm\":1000,\"io\":\"10000000\",\"gpsSpeed\":40,\"deviceStatus\":\"00000000\"}]");
        obj.put("decodeData","{\"Tag\":\"$LOG40\",\"Imei\":\"864292042623254\",\"Serial\":\"MARK01\",\"Imsi\":\"466891006473726\",\"DriverID\":\"12345678\",\"Date\":\"030522\",\"Time\":\"075129\",\"GpsSignal\":\"A\",\"Csq\":\"27\",\"Gps\":\"18\",\"Mile\":\"011958448\",\"LogCount\":\"30\",\"CRC\":\"8F8E\"}");
        ConcurrentHashMap<String, Boolean> _existTableMap = new ConcurrentHashMap<String, Boolean>(1);
        ConcurrentHashMap<String, tb_information_car> _lastStatusMap = new ConcurrentHashMap<String, tb_information_car>(1);
        DeviceData_Log currentTest = new DeviceData_Log(obj.toString(), _existTableMap, _lastStatusMap);
        currentTest.run();
    }

    @Test
    public void testProcess() throws Exception {
        String tableName = "g_KEA-9920_2022";

        String startTime = new Custom_DateTime("2022-02-06 00:00:00").getDateTimeString();
        String endTime = new Custom_DateTime("2022-02-06 02:00:00").getDateTimeString();

        List<tb_log_data> logList = Operate_JassFleet_Table_Factory
                .GetInstance()
                .getOperateTbLogData()
                .getLogData(tableName, startTime, endTime)
                .stream()
                .parallel()
                .map(tb_log_data::new)
                .collect(Collectors.toList());

        List<Kafka_LogData_Detail_Object> detail_objects = logList
                .stream()
                .parallel()
                .map(Kafka_LogData_Detail_Object::new)
                .collect(Collectors.toList());

        List<List<tb_log_data>> result = Lists.partition(logList, 30);
        System.out.println("done");
    }

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: run()
     */
    @Test
    public void testRun() throws Exception {
//TODO: Test goes here...
    }

    /**
     * Method: processData()
     */
    @Test
    public void testProcessDataFAF_501() throws Exception {

        String DeviceName = "FAF-501";
        String TableName = "g_TESTCAR106_31_2022";
        Custom_DateTime startTime = new Custom_DateTime("2022-02-17 00:00:00");
        Custom_DateTime endTime = new Custom_DateTime("2022-02-18 07:59:59");

        ConcurrentHashMap<String, Boolean> existTableMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, tb_information_car> lastAccStatusMap = new ConcurrentHashMap<>();

        List<tb_log_data> logData = Operate_JassFleet_Table_Factory
                .GetInstance()
                .getOperateTbLogData()
                .getLogData(TableName, startTime.getDateTimeString(), endTime.getDateTimeString())
                .stream()
                .map(tb_log_data::new)
                .collect(Collectors.toList());

        Custom_DateTime fakeTime = new Custom_DateTime("2022-01-01 00:00:00");
        logData.forEach(data -> {
            data.setDateTime(fakeTime);
            data.setSerial(DeviceName);
            fakeTime.AddSecond(1);
        });
        List<String> packageList = Lists.partition(logData, 30).stream().map(Package::new).map(Package::GetJsonValue).collect(Collectors.toList());
        System.out.println("done");
        for (String packet : packageList) {
            DeviceData_Log currentTest = new DeviceData_Log(packet, existTableMap, lastAccStatusMap);
        }
    }

    /**
     * Method: checkTableExist(String tableName)
     */
    @Test
    public void testCheckTableExist() throws Exception {
        Custom_DateTime checkTime = new Custom_DateTime();
        Custom_DateTime fi = new Custom_DateTime(checkTime).AddHour(1);
        long seconds = fi.Subtract(checkTime).getDateTime().getTime() / 1000;
    }

    /**
     * Method: checkLastAccSatus(String carUnicode, int year)
     */
    @Test
    public void testCheckLastAccSatus() throws Exception {
//TODO: Test goes here...
    }

    /**
     * Method: processDataNew()
     */
    @Test
    public void testProcessDataNew() throws Exception {
//TODO: Test goes here...
    }

    /**
     * Method: InsertDatabase()
     */
    @Test
    public void testInsertDatabase() throws Exception {
        loadCarTableData();
        loadLastAccStatusData();

        Operate_Kafka_Factory.GetInstance().getOperateKafkaLogData().setOffset(5047523);
        while (true) {
            try {
                ConsumerRecords<String, String> consumerRecords = Operate_Kafka_Factory.GetInstance().getOperateKafkaLogData().get(1000000);
                if (consumerRecords.count() > 0) {
                    ExecutorService executorService = Executors.newFixedThreadPool(1000000000);
                    consumerRecords.forEach(record -> {
                        DeviceData_Log deviceDataLog = null;
                        try {
                            deviceDataLog = new DeviceData_Log(record.value(), existTableMap, lastAccStatusMap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        executorService.execute(deviceDataLog);
                    });
                    executorService.shutdown();
                    executorService.awaitTermination(30, TimeUnit.SECONDS);
                    Operate_Kafka_Factory.GetInstance().getOperateKafkaLogData().commitSync();
                }
                Thread.sleep(100);
            } catch (Exception e) {
                System.out.println(ExceptionUtils.getStackTrace(e));
            }
        }
    }
    private void loadCarTableData() {
        try {
            List<Map<String, Object>> fTables = Operate_JassFleet_Table_Factory.GetInstance().getOperateTbLogData().GetTableName();
            for (Map<String, Object> fTable : fTables) {
                String tableName = Objects.toString(fTable.get("TABLE_NAME"), "");
                if (!tableName.equals("")) {
                    existTableMap.put(tableName, true);
                }
            }
            System.out.println("LOADING TABLE MAP COMPLETED");
        } catch (Exception e) {
            System.out.println(ExceptionUtils.getStackTrace(e));
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    private void loadLastAccStatusData() {
        try {

            lastAccStatusMap  = Operate_JassFleet_Table_Factory
                    .GetInstance()
                    .getOperate_information_car()
                    .getAllSatusIO()
                    .parallelStream()
                    .map(tb_information_car::new)
                    .collect(Collectors.toMap(tb_information_car::getCar_number, Function.identity(),(o1, o2) -> o1, ConcurrentHashMap::new));

            System.out.println("LOADING CarInfomcation MAP COMPLETED");
        } catch (Exception e) {
            System.out.println(ExceptionUtils.getStackTrace(e));
        }
    }
    @Test
    public void testAccTransitionStatus() throws Exception {
        JSONObject test = new JSONObject();
        test.put("encodeData","$LOG40,864292043975414,GARYTEST,466924700770283,12345678,090622,010059,A,16,11,003480954,30,h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000,756C#");
        test.put("LogData","[{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"}]");
        test.put("decodeData","{\"Tag\":\"$LOG40\",\"Imei\":\"864292043975414\",\"Serial\":\"GARYTEST\",\"Imsi\":\"466924700770283\",\"DriverID\":\"12345678\",\"Date\":\"090622\",\"Time\":\"010059\",\"GpsSignal\":\"A\",\"Csq\":\"16\",\"Gps\":\"11\",\"Mile\":\"003480954\",\"LogCount\":\"30\",\"CRC\":\"756C\"}");
        DeviceData_Log currentTest = new DeviceData_Log(test.toString(),null,null);
        currentTest.setLogDataList(currentTest.processDataNew());
        currentTest.setLogDataList(currentTest.filterData(currentTest.getLogDataList()));
    }
    @Test
    public void testThreadSafe() throws Exception {

        List<tb_log_data> logDataList = null;
        ForkJoinPool customThreadPool = null;
        List<Map<String,Object>> ObjectDetailList = null;
        Map<String,Object>       ObjectList = null;
        AtomicInteger index = new AtomicInteger(0);
        JSONObject data = new JSONObject();
        JSONArray jsonArray = new JSONArray("[{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"},{\"longitude\":121421160,\"latitude\":25031800,\"direction\":240,\"speed\":0,\"rpm\":0,\"io\":\"10000000\",\"gpsSpeed\":0,\"deviceStatus\":\"00000000\"}]");
        for(int i = 0 ;i<30;i++){
            if (i<10||i>20){
                jsonArray.put(i,jsonArray.getJSONObject(i).put("io","000000"));
            }
            else {
                jsonArray.put(i,jsonArray.getJSONObject(i).put("io","100000"));
            }
        }
        data.put("encodeData","$LOG40,864292043975414,GARYTEST,466924700770283,12345678,090622,010059,A,16,11,003480954,30,h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000h�<\u0007x�}\u0001\u0018\u0000\u0000�\u0000\u0000,756C#");
        data.put("LogData",jsonArray.toString());
        data.put("decodeData","{\"Tag\":\"$LOG40\",\"Imei\":\"864292043975414\",\"Serial\":\"GARYTEST\",\"Imsi\":\"466924700770283\",\"DriverID\":\"12345678\",\"Date\":\"090622\",\"Time\":\"010059\",\"GpsSignal\":\"A\",\"Csq\":\"16\",\"Gps\":\"11\",\"Mile\":\"003480954\",\"LogCount\":\"30\",\"CRC\":\"756C\"}");

        ObjectDetailList   = new ObjectMapper().readValue(Objects.toString(data.get("LogData"), ""), new TypeReference<>() {});
        ObjectList         = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(data.get("decodeData").toString(), HashMap.class);
        customThreadPool = new ForkJoinPool(30);
        Map<String, Object> finalObjectList = ObjectList;
        ObjectDetailList.parallelStream().forEach(element->element.putAll(finalObjectList));
        List<Map<String, Object>> finalObjectDetailList = ObjectDetailList;
        logDataList = customThreadPool.submit(
                () -> finalObjectDetailList
                        .stream()
                        .map(tb_log_data::new)
                        .map(tb_log_data::fixUpperCaseTable)
                        .map(element->element.setDateTime(element.getDateTime().AddSecond(-1 * (29 - index.getAndIncrement()))))
                        .collect(Collectors.toList())).get();
        logDataList.forEach(element->{
            System.out.println(element.getDateTime().getDateTimeString());
            System.out.println(element.getIo());
        });
    }
}