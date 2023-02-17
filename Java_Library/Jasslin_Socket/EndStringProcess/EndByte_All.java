package Jasslin_Socket.EndStringProcess;


import Extension_Methods.Convert;

import java.util.List;

public class EndByte_All implements IEndData {

    @Override
    public byte[] GetDataWithEndString(List<Byte> lstData, int startIndex) {
        if (lstData.isEmpty())
            return null;

        byte[] bytes = Convert.ToArray(lstData);
        lstData.clear();
        return bytes;
    }
}