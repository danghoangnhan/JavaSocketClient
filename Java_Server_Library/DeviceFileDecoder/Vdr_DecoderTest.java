package DeviceFileDecoder;

import DataEncoder.JAS208_Encoder;
import Extension_Methods.SystemVariable;
import VDR_Object.PackageData;
import VDR_Object.tb_carInformation_data;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import Extension_Methods.Convert.*;


public class Vdr_DecoderTest {

    @Test
    public void KKA_7786() throws Exception {
        String fileVdr =  SystemVariable.DataSetDirPath
                .concat(SystemVariable.separator)
                .concat("vdr")
                .concat(SystemVariable.separator)
                .concat("D20220102_235959_KKA-7786.VDR");
        Vdr_Decoder vdr_decoder = new Vdr_Decoder(Files.readAllBytes(Paths.get(fileVdr)));
        vdr_decoder.TableGenerate();
    }
    @Test
    public void KNB_7225() throws Exception {
        String fileVdr =  SystemVariable.DataSetDirPath.concat(SystemVariable.separator)
                .concat("vdr")
                .concat(SystemVariable.separator)
                .concat("D20220309_235959_KNB-7225.VDR");
        Vdr_Decoder vdr_decoder = new Vdr_Decoder(Files.readAllBytes(Paths.get(fileVdr)));
        vdr_decoder.TableGenerate();
    }
    @Test
    public void MARK01() throws Exception {
        String fileVdr =  SystemVariable.DataSetDirPath.concat(SystemVariable.separator)
                .concat("vdr")
                .concat(SystemVariable.separator)
                .concat("D20220309_235959_MARK01.VDR");
        Vdr_Decoder vdr_decoder = new Vdr_Decoder(Files.readAllBytes(Paths.get(fileVdr)));
        vdr_decoder.TableGenerate();
    }
    @Test
    public void RPM2061208() throws Exception {
        String fileVdr =  SystemVariable.DataSetDirPath.concat(SystemVariable.separator)
                .concat("vdr")
                .concat(SystemVariable.separator)
                .concat("D20220309_235959_RBM2061208.VDR");
        Vdr_Decoder vdr_decoder = new Vdr_Decoder(Files.readAllBytes(Paths.get(fileVdr)));
        vdr_decoder.TableGenerate();

        tb_carInformation_data carInfo  =   vdr_decoder.getCarInformation();
        JAS208_Encoder currentTest      =   new JAS208_Encoder(carInfo);
        List<String> encodedPacket      =   currentTest.logDataEncode(vdr_decoder.logDataGenerate());
    }
}