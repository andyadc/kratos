package com.andyadc.kratos.common.util;

/**
 * 匹配工具类
 */
public final class MatchUtils {

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    public static boolean isMatch(String pattern, String path) {
        return ANT_PATH_MATCHER.match(pattern, path);
    }

}
