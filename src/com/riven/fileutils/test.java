package com.riven.fileutils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {

    public static void main(String[] args) {
//        test1();
//        test2();
//        test3();
        test4();
    }

    public static void test1() {
        String parentFolderName = "20160625-29_长白山";
        Pattern pattern1 = Pattern
                .compile("([\\w\\W]*)(?:(?:\\d{8}(?:-){1}\\d{2,4}){1}|(?:\\d{4}(?:-|[_/ ]){1}\\d{2}(?:-|[_/ ]){1}\\d{2}){1}|(?:\\d{8}){1})([\\w\\W]*)");
        Matcher matcher1 = pattern1.matcher(parentFolderName);
        if (matcher1.find()) {
            String description = "";
            String matcher_1 = matcher1.group(1);
            String matcher_2 = matcher1.group(2);
            System.out.println("matcher1.group(1)=" + matcher_1);
            System.out.println("matcher1.group(2)=" + matcher_2);
            if (matcher_1 != null && matcher_1.length() > 0) {
                if (matcher_1.startsWith("_") || matcher_1.startsWith("-") || matcher_1.startsWith(" ")) {
                    matcher_1 = matcher_1.substring(1);
                }
                if (matcher_1.endsWith("_") || matcher_1.endsWith("-") || matcher_1.endsWith(" ")) {
                    matcher_1 = matcher_1.substring(0, matcher_1.length() - 1);
                }
                description += matcher_1;
            }

            if (matcher_2 != null && matcher_2.length() > 0) {
                if (matcher_2.startsWith("_") || matcher_2.startsWith("-") || matcher_2.startsWith(" ")) {
                    matcher_2 = matcher_2.substring(1);
                }
                if (matcher_2.endsWith("_") || matcher_2.endsWith("-") || matcher_2.endsWith(" ")) {
                    matcher_2 = matcher_2.substring(0, matcher_2.length() - 1);
                }
                if (description.length() > 0 && matcher_2.length() > 0) {
                    description += "_";
                }
                description += matcher_2;
            }

            System.out.println("description=" + description);
        }
    }

    public static void test2() {
        Double _latitude, _longitude;
        Pattern pattern = Pattern.compile("((?: |\\d)+)°((?: |\\d)+)'");
        Matcher matcher = pattern.matcher("31° 15' 47.83");
        if (matcher.find()) {
            String tmp1 = matcher.group(1).trim();
            String tmp2 = matcher.group(2).trim();
            _latitude = Double.valueOf(tmp1) + (Double.valueOf(tmp2) / 60);
            System.out.println("_latitude=" + _latitude);
        }
    }

    public static void test3() {
        String[] position = null;
        String[] discription = null;
        String found = "[20190819105900].[IMG].[].[和平公园]";
        if (found != null && found.length() > 0) {
            String[] tmp = found.split("\\]\\.\\[");
            for (int i = 0; i < tmp.length; i++) {
                System.out.println("tmp[" + i + "]=" + tmp[i]);
            }
        }
    }

    public static void test4() {
        Pattern pattern2 = Pattern
                .compile("\\[[\\w\\W]+\\](?:.\\[([\\w\\W]*)\\]){1,3}");
        Matcher matcher2 = pattern2.matcher("[20171114,20171115].[IMG].[上海市,长宁区],长宁区],长宁区],长宁区],长宁区],长宁区],长宁区]]");
        if (matcher2.find()) {
            for (int i = 1; i <= matcher2.groupCount(); i++) {
                System.out.println("matcher2.group(" + i + ")=" + matcher2.group(i));
            }
        }
    }
}
