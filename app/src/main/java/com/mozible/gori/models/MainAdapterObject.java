package com.mozible.gori.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JunLee on 7/18/16.
 */
public class MainAdapterObject {
    public static enum MAIN_ADAPTER_TYPE {TYPE_CONTENT, TYPE_OTHER_PROFILE, TYPE_MY_PROFILE};
    public MAIN_ADAPTER_TYPE mainAdapterType = MAIN_ADAPTER_TYPE.TYPE_CONTENT;

    public static int getViewTypeCount() {
        return MAIN_ADAPTER_TYPE.values().length;
    }

    public int getViewType() {
        return mainAdapterType.ordinal();
    }
}
