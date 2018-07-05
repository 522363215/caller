package com.md.block.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberUtil {

    public static String getNumberByPattern(String rawNum) {
        String number = "";

        if (rawNum == null || rawNum.trim().length() == 0) {
            return number;
        }

        String first = "";
        if (rawNum.trim().startsWith("+")) {
            first = "+";
        }

        rawNum = rawNum.trim();

        String regex = "\\d*";
        Pattern p = Pattern.compile(regex);

        Matcher m = p.matcher(rawNum);

        StringBuilder sb = new StringBuilder();
        sb.append(first);
        while (m.find()) {
            if (!"".equals(m.group())) {
                sb.append(m.group());
            }
        }

        number = sb.toString();
        return number;
    }
}
