package com.mozible.gori.utils;

/**
 * Created by JunLee on 7/18/16.
 */
public class GoriTextUtil {
    public static boolean isEmpty(String tmp){
        if(tmp != null && !tmp.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
}
