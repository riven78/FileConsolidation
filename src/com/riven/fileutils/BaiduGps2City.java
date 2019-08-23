package com.riven.fileutils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaiduGps2City {
//    public static String ak = "5Djp50AmUTzXmo3oOoYhGGyPODIN9xFc";
//    public static String sk = "lpvSDe1k3s6LhNy5U2kiINa5bPceywtU";
//    public static String serverAddrss = "http://127.0.0.1:8888";

    public static void main(String[] args) throws UnsupportedEncodingException,
            NoSuchAlgorithmException {
        getGps2City("31° 15' 47.83", "121° 30' 27.53");
    }

    public static String[] getGps2City(String Latitude, String Longitude) {
        String[] ret = null;
        if (Config.baiduAppKey != null && Config.baiduAppKey.length() > 0
                && Config.gps2cityServerAddress != null && Config.gps2cityServerAddress.length() > 0) {
            Double _latitude, _longitude;
            Pattern pattern = Pattern.compile("((?: |\\d)+)°((?: |\\d)+)'");
            Matcher matcher = pattern.matcher(Latitude);
            if (matcher.find()) {
                String tmp1 = matcher.group(1).trim();
                String tmp2 = matcher.group(2).trim();
                _latitude = Double.valueOf(tmp1) + (Double.valueOf(tmp2) / 60);
            } else {
                _latitude = Double.valueOf(Latitude);
            }
            matcher = pattern.matcher(Longitude);
            if (matcher.find()) {
                String tmp1 = matcher.group(1).trim();
                String tmp2 = matcher.group(2).trim();
                _longitude = Double.valueOf(tmp1) + (Double.valueOf(tmp2) / 60);
            } else {
                _longitude = Double.valueOf(Longitude);
            }
            if (_latitude > 0 && _longitude > 0) {
                Map paramsMap = new LinkedHashMap<String, String>();
                paramsMap.put("output", "json");
                paramsMap.put("coordtype", "wgs84ll");
                paramsMap.put("location", _latitude + "," + _longitude);
                paramsMap.put("ak", Config.baiduAppKey);

                try {
                    String paramsStr = toQueryString(paramsMap);
                    String url = Config.gps2cityServerAddress + "/reverse_geocoding/v3/?" + paramsStr;
                    if (Config.baiduSecritKey != null && Config.baiduSecritKey.length() > 0) {
                        String wholeStr = "/reverse_geocoding/v3/?" + paramsStr + Config.baiduSecritKey;
                        String tempStr = URLEncoder.encode(wholeStr, "UTF-8");
                        url += "&sn=" + MD5(tempStr);
                    }
//                System.out.println(url);
                    String result = HttpClient.doGet(url);
                    if (result != null && result.length() > 0) {
                        String country = null;
                        String province = null;
                        String city = null;
                        String district = null;
                        if (result.startsWith("{")) {
                            JSONObject json = new JSONObject(result);
                            JSONObject _result = json.optJSONObject("result");
                            if (_result != null) {
                                JSONObject addressComponent = _result.optJSONObject("addressComponent");
                                if (addressComponent != null) {
                                    country = addressComponent.optString("country");
                                    province = addressComponent.optString("province");
                                    city = addressComponent.optString("city");
                                    district = addressComponent.optString("district");
                                }
                            }
                        } else if (result.startsWith("[")) {
                            JSONArray array = new JSONArray(result);
                            if (array.length() > 0) {
                                JSONObject obj = array.getJSONObject(0);
                                country = obj.optString("country");
                                province = obj.optString("province");
                                city = obj.optString("city");
                                district = obj.optString("district");
                            }
                        }

                        ArrayList<String> list = new ArrayList<>();
                        if (district != null && district.length() > 0) {
                            list.add(district);
                        }
                        if (city != null && city.length() > 0) {
                            boolean found = false;
                            for (String tmp : list) {
                                if (found = tmp.equals(city)) {
                                    break;
                                }
                            }
                            if (!found) {
                                list.add(0, city);
                            }
                        }
                        if (province != null && province.length() > 0) {
                            boolean found = false;
                            for (String tmp : list) {
                                if (found = tmp.equals(province)) {
                                    break;
                                }
                            }
                            if (!found) {
                                list.add(0, province);
                            }
                        }
                        if (country != null && country.length() > 0 && !country.equals("中国")) {
                            boolean found = false;
                            for (String tmp : list) {
                                if (found = tmp.equals(country)) {
                                    break;
                                }
                            }
                            if (!found) {
                                list.add(0, country);
                            }
                        }
                        if (list.size() > 0) {
                            ret = new String[list.size()];
                            for (int i = 0; i < list.size(); i++) {
                                ret[i] = list.get(i);
                            }
                            list.clear();
                        }
                    }
//                System.out.println(ret);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    // 对Map内所有value作utf8编码，拼接返回结果
    private static String toQueryString(Map<?, ?> data)
            throws UnsupportedEncodingException {
        StringBuffer queryString = new StringBuffer();
        for (Map.Entry<?, ?> pair : data.entrySet()) {
            queryString.append(pair.getKey() + "=");
            queryString.append(URLEncoder.encode((String) pair.getValue(),
                    "UTF-8") + "&");
        }
        if (queryString.length() > 0) {
            queryString.deleteCharAt(queryString.length() - 1);
        }
        return queryString.toString();
    }

    // 来自stackoverflow的MD5计算方法，调用了MessageDigest库函数，并把byte数组结果转换成16进制
    private static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
                        .substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }
}