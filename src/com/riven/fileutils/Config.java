package com.riven.fileutils;

import org.json.JSONObject;

import java.io.*;

public class Config {

    public static String baiduAppKey;
    public static String baiduSecritKey;
    public static String gps2cityServerAddress;

    public Config() {
        String pathname = "config.json";
        String _pathname = "selfConfig.json";
        File _file = new File(_pathname);
        if (_file.exists()) {
            pathname = _pathname;
        }
        try (FileReader reader = new FileReader(pathname);
             BufferedReader br = new BufferedReader(reader) // 建立一个对象，它把文件内容转成计算机能读懂的语言
        ) {
            String data = "";
            String line;
            //网友推荐更加简洁的写法
            while ((line = br.readLine()) != null) {
                // 一次读入一行数据
                data += line;
            }
            JSONObject json = new JSONObject(data);
            if (json != null) {
                baiduAppKey = json.optString("baiduAppKey");
                baiduSecritKey = json.optString("baiduSecritKey");
                gps2cityServerAddress = json.optString("gps2cityServerAddress");
            }
            br.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        File file = new File("abc.txt");//绝对路径
////      File file = new File("hello.html");//相对路径
//        try {
//            file.createNewFile();
//            BufferedWriter out = new BufferedWriter(new FileWriter(file));
//            out.write(baiduAppKey);
//            out.flush(); // 把缓存区内容压入文件
//            out.close(); // 最后记得关闭文件
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
