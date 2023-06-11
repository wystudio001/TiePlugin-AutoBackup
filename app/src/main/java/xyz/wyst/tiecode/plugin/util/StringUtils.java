package xyz.wyst.tiecode.plugin.util;

import xyz.wyst.tiecode.plugin.App;
import java.io.*;
import java.lang.*;
import java.util.*;
import java.util.regex.*;
import android.content.*;

/*
* Written by WYstudio
*/

public class StringUtils {
    public static String jiequ_string(String str, String star_str, String end_str, boolean sf_jqf) {
        int left = str.indexOf(star_str);
        if (left == -1) {
            return "";
        }
        int right = str.indexOf(end_str, left + star_str.length());
        if (right == -1) {
            return "";
        }
        String temp;
        if (sf_jqf) {
            temp = quzhongjian_string(str, left, right + end_str.length() - 1);
        } else {
            temp = quzhongjian_string(str, left + star_str.length(), right - 1);
        }
        return temp;

    }

    public static String quzhongjian_string(String str, int start_int, int end_int) {
        return str.substring(start_int, end_int + 1);
    }

    public static String decodeUnicode(String theString) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(theString);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            theString = theString.replace(matcher.group(1), ch + "");
        }
        return theString;
    }
}