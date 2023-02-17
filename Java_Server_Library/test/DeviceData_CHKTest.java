package test;

import DeviceData.DeviceData_CHK;
import org.junit.Assert;
import org.junit.Test;

public class DeviceData_CHKTest {

    @Test
    public void normalTest(){

    }
    @Test
    public void testNegativeSpeedGain() throws Exception {
        DeviceData_CHK currentTest = new DeviceData_CHK("{\"Tag\":\"$CHK\",\"Imei\":\"123456789054321\",\"BusID\":\"Test-001\",\"DeviceType\":\"JAS106S\",\"FwSign\":\"FW-1.001_Alpha4\",\"Csq\":\"12\",\"GpsSignal\":\"A8\",\"Acc\":\"1\",\"Internet\":\"internet\",\"IpPort\":\"128.199.184.219:9106\",\"DnsPort\":\"0:0\",\"SendTime\":\"30\",\"StandbySendTime\":\"36000\",\"SpeedTrigger\":\"0\",\"SpeedGain\":\"-68056469327705780000000000000\",\"RpmTrigger\":\"0\",\"RpmDiv\":\"3\"}");
        currentTest.processData();
        Assert.assertEquals(Double.parseDouble("-68056469327705780000000000000"), currentTest.getChkData().getSpeedGain(), 0.01);
    }
}