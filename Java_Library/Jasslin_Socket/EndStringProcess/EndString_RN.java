package Jasslin_Socket.EndStringProcess;

import java.util.List;

public class EndString_RN implements IEndData {

    @Override
    public byte[] GetDataWithEndString(List<Byte> lstData, int startIndex) {
        byte[] bytes;
        for (int i = startIndex; i < lstData.size(); i++) {
            if (lstData.get(i - 1) == 13 && lstData.get(i) == 10) {
                bytes = new byte[i - 1];
                for (int j = 0; j < bytes.length; j++) {
                    bytes[j] = lstData.get(j);
                }
                lstData.subList(0, i + 1).clear();
                return bytes;
            }
        }
        return null;
    }

}
