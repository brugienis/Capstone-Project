package au.com.kbrsolutions.melbournepublictransport.utilities;

import java.util.Arrays;

/**
 * Created by business on 26/09/2016.
 */

public class Other {

    /*
        from Joachim Sauer
            http://stackoverflow.com/questions/80476/how-can-i-concatenate-two-arrays-in-java
    */
    public static <T> T[] concatAll(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }
}
