package com.andyadc.kratos.common.util;

import com.alibaba.fastjson2.JSON;

public final class JsonUtils {

    public static String toJSONString(Object obj) {
        return JSON.toJSONString(obj);
    }
}
