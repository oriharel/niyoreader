package com.niyo.reader.app;

/**
 * Created by oriharel on 6/7/14.
 */
public class AndroidUtil {

    public static String getArrayAsString(Object[] array)
    {
        String result = "";
        if (array != null) {
            for (Object object : array) {
                result += object.toString() + ", ";
            }
        }
        return result;
    }
}
