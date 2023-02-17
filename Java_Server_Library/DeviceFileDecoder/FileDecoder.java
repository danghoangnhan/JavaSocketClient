package DeviceFileDecoder;

import VDR_Object.PackageData;

import java.util.List;

public interface FileDecoder {
    void TableGenerate();
    List<PackageData>logDataGenerate();
}
