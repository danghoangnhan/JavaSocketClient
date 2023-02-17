package DataDecoder;

import Extension_Methods.Convert;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JAS208_Decoder implements IDecoder {

    @Override
    public String logDataDecode(String dataStr, byte[] bytes) throws Exception {
        JSONObject jsonObject = new JSONObject();

        if (dataStr.length() > 0) {
            jsonObject.put("encodeData",dataStr);
            JSONObject decodeObject = new JSONObject();
            dataStr = dataStr.replace("#", "");
            List<String> dataList = Arrays.asList(dataStr.split(","));
            if (dataList.size() < 13) {
                return null;
            }

            int startIndex = 0;
            for (int i = 0; i <= 11; i++) {
                startIndex += dataList.get(i).length();
                startIndex++;
            }
            String recordStr = parseLogRecord(Convert.ToList(bytes), startIndex);
            dataList.set(12, recordStr);
            for (int i = 0; i < dataList.size(); i++) {
                if (i == 0) {
                    decodeObject.put("Tag", dataList.get(i));
                } else if (i == 1) {
                    decodeObject.put("Imei", dataList.get(i));
                } else if (i == 2) {
                    decodeObject.put("Serial", dataList.get(i));
                } else if (i == 3) {
                    decodeObject.put("Imsi", dataList.get(i));
                } else if (i == 4) {
                    decodeObject.put("DriverID", dataList.get(i));
                } else if (i == 5) {
                    decodeObject.put("Date", dataList.get(i));
                } else if (i == 6) {
                    decodeObject.put("Time", dataList.get(i));
                } else if (i == 7) {
                    decodeObject.put("GpsSignal", dataList.get(i));
                } else if (i == 8) {
                    decodeObject.put("Csq", dataList.get(i));
                } else if (i == 9) {
                    decodeObject.put("Gps", dataList.get(i));
                } else if (i == 10) {
                    decodeObject.put("Mile", dataList.get(i));
                } else if (i == 11) {
                    decodeObject.put("LogCount", dataList.get(i));
                } else if (i == 12) {
                    jsonObject.put("LogData", dataList.get(i));
                } else if (i == dataList.size() - 1) {
                    decodeObject.put("CRC", dataList.get(i));
                }
            }
            jsonObject.put("decodeData",decodeObject.toString());
            return jsonObject.toString();
        }
        return null;
    }

    @Override
    public String eventDataDecode(String dataStr, byte[] bytes) throws Exception {
        JSONObject jsonObject = new JSONObject();
        if (dataStr.length() > 0) {
            List<String> dataList = Arrays.asList(dataStr.split(","));
            if (dataList.size() < 18) {
                return null;
            }

            for (int i = 0; i < dataList.size(); i++) {
                if (i == 0) {
                    jsonObject.put("Tag", dataList.get(i));
                } else if (i == 1) {
                    jsonObject.put("Imei", dataList.get(i));
                } else if (i == 2) {
                    jsonObject.put("BusID", dataList.get(i));
                } else if (i == 3) {
                    jsonObject.put("Imsi", dataList.get(i));
                } else if (i == 4) {
                    jsonObject.put("DeviceType", dataList.get(i));
                } else if (i == 5) {
                    jsonObject.put("FwSign", dataList.get(i));
                } else if (i == 6) {
                    jsonObject.put("Csq", dataList.get(i));
                } else if (i == 7) {
                    jsonObject.put("GpsSignal", dataList.get(i));
                } else if (i == 8) {
                    jsonObject.put("Acc", dataList.get(i));
                } else if (i == 9) {
                    jsonObject.put("Internet", dataList.get(i));
                } else if (i == 10) {
                    jsonObject.put("IpPort", dataList.get(i));
                } else if (i == 11) {
                    jsonObject.put("DnsPort", dataList.get(i));
                } else if (i == 12) {
                    jsonObject.put("SendTime", dataList.get(i));
                } else if (i == 13) {
                    jsonObject.put("StandbySendTime", dataList.get(i));
                } else if (i == 14) {
                    jsonObject.put("SpeedTrigger", dataList.get(i));
                } else if (i == 15) {
                    jsonObject.put("SpeedGain", dataList.get(i));
                } else if (i == 16) {
                    jsonObject.put("RpmTrigger", dataList.get(i));
                } else if (i == 17) {
                    jsonObject.put("RpmDiv", dataList.get(i));
                }
            }
            return jsonObject.toString();
        }
        return null;
    }

    @Override
    public String abnormalDataDecode(String dataStr, byte[] bytes) throws Exception {
        JSONObject jsonObject = new JSONObject();
        if (dataStr.length() > 0) {
            jsonObject.put("encodeData",dataStr);
            JSONObject decodeObject = new JSONObject();
            List<String> dataList = Arrays.asList(dataStr.split(","));
            if (dataList.size() < 19) {
                return null;
            }
            for (int i = 0; i < dataList.size(); i++) {
                if (i == 0) {
                    jsonObject.put("Tag", dataList.get(i));
                } else if (i == 1) {
                    jsonObject.put("Imei", dataList.get(i));
                } else if (i == 2) {
                    jsonObject.put("Serial", dataList.get(i));
                } else if (i == 3) {
                    jsonObject.put("Imsi", dataList.get(i));
                } else if (i == 4) {
                    jsonObject.put("Date", dataList.get(i));
                } else if (i == 5) {
                    jsonObject.put("Time", dataList.get(i));
                } else if (i == 6) {
                    jsonObject.put("GpsSignal", dataList.get(i));
                } else if (i == 7) {
                    jsonObject.put("Longitude", dataList.get(i));
                } else if (i == 8) {
                    jsonObject.put("Latitude", dataList.get(i));
                } else if (i == 9) {
                    jsonObject.put("Direction", dataList.get(i));
                } else if (i == 10) {
                    jsonObject.put("Speed", dataList.get(i));
                } else if (i == 11) {
                    jsonObject.put("Mile", dataList.get(i));
                } else if (i == 12) {
                    jsonObject.put("Rpm", dataList.get(i));
                } else if (i == 13) {
                    jsonObject.put("DriverID", dataList.get(i));
                } else if (i == 14) {
                    jsonObject.put("Csq", dataList.get(i));
                } else if (i == 15) {
                    jsonObject.put("Gps", dataList.get(i));
                } else if (i == 16) {
                    jsonObject.put("IoSignal", dataList.get(i));
                } else if (i == 17) {
                    jsonObject.put("AbnormalCode", dataList.get(i));
                } else if (i == 18) {
                    jsonObject.put("AbnormalContent", dataList.get(i));
                }
            }
            jsonObject.put("decodeData",jsonObject.toString());
            return jsonObject.toString();
        }
        return null;
    }

    public String parseLogRecord(List<Byte> bytes, Integer start) throws Exception {
        List<List<Byte>> records = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Integer end = start + 14;
            List<Byte> byteList = new ArrayList<>();
            for (int j = start; j < end; j++) {
                byteList.add(bytes.get(j));
            }
            records.add(byteList);
            start += 14;
        }
        JSONArray jsonArray = new JSONArray();
        for (List<Byte> record : records) {
            List<Integer> data = new ArrayList<>();
            for (Byte b : record) {
                if (b < 0 || b > 255 || b % 1 != 0) {
                    data.add(256 + (int) b);
                } else {
                    data.add((int) b);
                }
            }
            // Get log data
            Integer longitude = calc(bin(data.get(3)) + bin(data.get(2)) + bin(data.get(1)) + bin(data.get(0)));
            Integer latitude = calc(bin(data.get(7)) + bin(data.get(6)) + bin(data.get(5)) + bin(data.get(4)));
            Integer direction = Integer.parseInt(data.get(8).toString()) * 10;
            Integer speed = Integer.parseInt(data.get(9).toString());
            Integer rpm = Integer.parseInt(data.get(10).toString()) * 50;
            String io = bin(data.get(11));
            Integer gpsSpeed = Integer.parseInt(data.get(12).toString());
            String deviceStatus = bin(data.get(13));

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("longitude", longitude);
            jsonObject.put("latitude", latitude);
            jsonObject.put("direction", direction);
            jsonObject.put("speed", speed);
            jsonObject.put("rpm", rpm);
            jsonObject.put("io", io);
            jsonObject.put("gpsSpeed", gpsSpeed);
            jsonObject.put("deviceStatus", deviceStatus);
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }

    public String bin(Integer n) {
        String input = ("000000000" + Integer.toBinaryString(n));
        if (input.length() > 8) {
            return input.substring(input.length() - 8);
        } else {
            return input;
        }
    }

    public Integer calc(String hex) {
        return Integer.parseInt(hex, 2);
    }

}
