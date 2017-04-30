package util;

import java.util.List;

/**
 * Created by bk on 26/04/2017.
 */
public class Helper {

    /**
     * covnerts given byte array list into single byte array
     *
     * @param listByte
     * @return
     */
    public static byte[] convertListToByteArr(List<byte[]> listByte) {

        byte[] toStore = new byte[0];
        for (byte[] byteArr : listByte) {

            int aLen = toStore.length;
            int bLen = byteArr.length;
            byte[] c = new byte[aLen+bLen];
            System.arraycopy(toStore, 0, c, 0, aLen);
            System.arraycopy(byteArr, 0, c, aLen, bLen);
            toStore = new byte[c.length];
            System.arraycopy(c, 0, toStore, 0, c.length );

        }

        return toStore;
    }

}
