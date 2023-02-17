package Jasslin_Socket.EndStringProcess;

import java.util.List;

public interface IEndData {

    byte[] GetDataWithEndString(List<Byte> lstData, int startIndex);
}
