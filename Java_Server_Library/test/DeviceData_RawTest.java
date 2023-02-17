package test;

import DB_Table_Operate.DB_Kafka.Operate_Kafka_Factory;
import Device.JAS106_Client;
import Device.JAS208_Client;
import Device.clientConstants;
import Device.deviceGroup;
import DeviceData.DeviceData_Raw;
import Time_Component.Custom_DateTime;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static Extension_Methods.Convert.String2Array;

public class DeviceData_RawTest implements clientConstants {
    private static final Log logger = LogFactory.getLog(DeviceData_RawTest.class);
    private final JAS106_Client client106 = new JAS106_Client();
    private final JAS208_Client client208 = new JAS208_Client();

    @Test
    public void test1() {
        int CarNumb = 5;
        boolean fixDate = true;
        try {
//            Operate_Kafka_Factory.GetInstance().getOperateKafkaRawData().purgeTopic();
            List<String> packetList106 = JAS106_Client.readBinFile(fileName106, new Custom_DateTime()).subList(0, 10);
            List<String> packetList208 = JAS208_Client.readBinFile(fileName208, new Custom_DateTime()).subList(0, 10);

            List<ByteBuffer> byteList106 = packetList106.stream().map(StandardCharsets.UTF_8::encode).collect(Collectors.toList());
            List<ByteBuffer> byteList208 = packetList208.stream().map(StandardCharsets.UTF_8::encode).collect(Collectors.toList());

            List<String> ASCIIList106 = byteList106.stream().map(packet->StandardCharsets.UTF_8.decode(packet).toString()).collect(Collectors.toList());
            List<String> ASCIIList208 = byteList208.stream().map(packet->StandardCharsets.UTF_8.decode(packet).toString()).collect(Collectors.toList());

            deviceGroup test106 = new deviceGroup(prefix106, packetList106, CarNumb, localIP, Port106, RapiddelayTime, fixDate, client106);
            deviceGroup test208 = new deviceGroup(prefix208, packetList208, CarNumb, localIP, Port208, RapiddelayTime, fixDate, client208);

            Thread thread106 = new Thread(test106);
            Thread thread208 = new Thread(test208);

            thread106.start();
            thread208.start();

            thread106.join();
            thread208.join();

            int totalSendMessage = test106.totalSendMessage() + test208.totalSendMessage();
            int time = 10;
            logger.info("waiting for " + time);
            TimeUnit.SECONDS.sleep(time);
            int totalReceiverMessage = 0;
            while (totalReceiverMessage < totalSendMessage) {
                ConsumerRecords<String, String> consumerRecords = Operate_Kafka_Factory.GetInstance().getOperateKafkaRawData().get();
                int totalCurrentMessage = consumerRecords.count();
                if (consumerRecords.count() > 0) {
                    totalReceiverMessage += totalCurrentMessage;
                    ExecutorService executorService = Executors.newFixedThreadPool(10);
                    consumerRecords.forEach(record -> {
                        try {
                            DeviceData_Raw rawData = new DeviceData_Raw(record.key(), record.value());
                            executorService.execute(rawData);
                        } catch (Exception e) {
                            System.out.println(e.fillInStackTrace());
                        }
                    });
                    executorService.shutdown();
                    executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
                    Operate_Kafka_Factory.GetInstance().getOperateKafkaRawData().commitSync();
                }
            }
            Custom_DateTime now = new Custom_DateTime().AddHour(8);

            for (int resultLoop = 0; resultLoop < CarNumb; resultLoop++) {
                List<String> result106List = readRawFile(String.format("rawData/%s/%s_%s.txt",
                        now.getDateString(),
                        prefix106.toUpperCase(Locale.ROOT).concat(String.valueOf(resultLoop)),
                        now.getDateString()).replace("/", System.getProperty("file.separator")));

                List<String> result208List = readRawFile(String.format("rawData/%s/%s_%s.txt",
                        now.getDateString(),
                        prefix208.toUpperCase(Locale.ROOT).concat(String.valueOf(resultLoop)),
                        now.getDateString()).replace("/", System.getProperty("file.separator")));

                String Org_device106Name = packetList106.get(0).substring(
                        StringUtils.ordinalIndexOf(packetList106.get(0), ",", 1) + 1, StringUtils.ordinalIndexOf(packetList106.get(0), ",", 2));

                String Org_device208Name = packetList208.get(0).substring(StringUtils.ordinalIndexOf(packetList208.get(0), ",", 2) + 1, StringUtils.ordinalIndexOf(packetList208.get(0), ",", 3));

                String new_device106Name = prefix106.concat(String.valueOf(resultLoop));
                String new_device208Name = prefix208.concat(String.valueOf(resultLoop));

                List<String> expectedList106 = ASCIIList106.stream().map(packet ->new String(String2Array(packet.replace(Org_device106Name, new_device106Name)),StandardCharsets.UTF_8)).collect(Collectors.toList());
                List<String> expectedList208 = ASCIIList208.stream().map(packet ->new String(String2Array(packet.replace(Org_device208Name, new_device208Name)),StandardCharsets.UTF_8)).collect(Collectors.toList());

                Assert.assertEquals(result106List, expectedList106);
                Assert.assertEquals(result208List, expectedList208);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> readRawFile(String fileDir) {
        List<String> packetList = null;
        try {
            String currentPath = new File(System.getProperty("user.dir"))
                    .getParentFile()
                    .toString()
                    .concat(System.getProperty("file.separator"))
                    .concat(fileDir);

            String content = Files.readString(Paths.get(currentPath), StandardCharsets.UTF_8);
            packetList = Arrays.asList(content.split("#")).stream().map(packet -> packet.concat("#")).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return packetList;
    }
}