package com.andyadc.kratos.common.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;

public final class JsonUtils {

    private static final JSONWriter.Feature[] features = {
            JSONWriter.Feature.WriteNullListAsEmpty,
            JSONWriter.Feature.WriteNullStringAsEmpty,
            JSONWriter.Feature.WriteNullBooleanAsFalse,
            JSONWriter.Feature.WriteMapNullValue
    };

    public static void main(String[] args) {
        toJSONString(null);
    }

    public static String toJSONString(Object obj) {
        return JSON.toJSONString(obj, features);
    }
}
