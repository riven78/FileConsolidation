package com.riven.fileutils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileInfo {
    public static final String FILE_TYPE_IMAGE = "image";
    public static final String FILE_TYPE_AUDIO = "audio";
    public static final String FILE_TYPE_VIDEO = "video";

    public String exifModel = "";
    public String exifDate = null;
    public long exifDateLong = 0L;
    public long duration = 0L;
    public String fullPath = null;
    public String fileName = null;
    public String fileType = null;
    public String suffixName = null;
    public String description1 = null;
    public String description2 = null;
    private String Latitude = null;
    private String Longitude = null;
    public String[] position = null;

    public FileInfo(File file) {
        if (file != null && file.exists() && file.isFile()) {
            String parentFolderName = file.getParent().substring(file.getParent().lastIndexOf(File.separator) + 1);
            Pattern pattern2 = Pattern
                    .compile("\\[[\\w\\W]+\\](?:.\\[[\\w\\W]*\\]){1,3}");
            Matcher matcher2 = pattern2.matcher(parentFolderName);
            if (!matcher2.find()) {
                Pattern pattern1 = Pattern
                        .compile("([\\u4e00-\\u9fa5]+[a-zA-Z\\u4e00-\\u9fa5]*)?[_\\- ]?(?:(?:\\d{4}_\\d{2}_\\d{2}_RAW)|(?:\\d+[_\\-\\:\\/ ]{1}\\d+[_\\-\\:\\/ ]{1}\\d+(?:[_\\-\\:\\/ ]{1}\\d+[_\\-\\:\\/ ]{1}\\d+)?)|\\d{14}|\\d{8}|\\d{6})[_\\- ]?([\\u4e00-\\u9fa5]+[a-zA-Z0-9\\u4e00-\\u9fa5]*)?");
                Matcher matcher1 = pattern1.matcher(parentFolderName);
                if (matcher1.find()) {
                    String matcher_1 = matcher1.group(1);
                    String matcher_2 = matcher1.group(2);
                    if (matcher_1 != null && matcher_1.length() > 0) {
                        if (matcher_1.startsWith("_") || matcher_1.startsWith("-") || matcher_1.startsWith(" ")) {
                            matcher_1 = matcher_1.substring(1);
                        }
                        if (matcher_1.endsWith("_") || matcher_1.endsWith("-") || matcher_1.endsWith(" ")) {
                            matcher_1 = matcher_1.substring(0, matcher_1.length() - 1);
                        }
                        if (!matcher_1.matches("\\d+")) {
                            description1 = matcher_1;
                        }
                    }

                    if (matcher_2 != null && matcher_2.length() > 0) {
                        if (matcher_2.startsWith("_") || matcher_2.startsWith("-") || matcher_2.startsWith(" ")) {
                            matcher_2 = matcher_2.substring(1);
                        }
                        if (matcher_2.endsWith("_") || matcher_2.endsWith("-") || matcher_2.endsWith(" ")) {
                            matcher_2 = matcher_2.substring(0, matcher_2.length() - 1);
                        }
                        if (!matcher_2.matches("\\d+")) {
                            description1 = matcher_2;
                        }
                    }
                }
            }
//            else {
//                String[] _tmp = parentFolderName.split("\\]\\.\\[");
//                if (_tmp != null && _tmp.length == 4 && _tmp[3] != null && _tmp[3].length() > 1) {
//                    if (_tmp[3].endsWith("]")) {
//                        _tmp[3] = _tmp[3].substring(0, _tmp[3].length() - 1);
//                    }
//                    String[] tmp = _tmp[3].split(",");
//                    if (tmp.length == 1) {
//                        description1 = tmp[0];
//                    }
//                }
//            }
            if (description1 != null && description1.equals("王颖资料")) {
                description1 = null;
            }
            fileName = file.getName();
            if (fileName != null && fileName.length() > 0) {
                String name = fileName.replaceAll("_\\d+\\.", ".");
                Pattern pattern1 = Pattern
                        .compile("(?:_){1}([\\u4e00-\\u9fa5]+[a-zA-Z0-9\\u4e00-\\u9fa5\\,]*).[a-zA-Z0-9]+$");
                Matcher matcher1 = pattern1.matcher(name);
                if (matcher1.find()) {
                    String matcher_1 = matcher1.group(1);
                    if (matcher_1 != null && matcher_1.length() > 0) {
                        String[] tmp = matcher_1.split(",");
                        for (String _tmp : tmp) {
                            if (!_tmp.matches("\\d+") && !_tmp.equals("IMG") && !tmp.equals("HDR")) {
                                description2 = _tmp;
                                break;
                            }
                        }
                    }
                } else {
                    pattern1 = Pattern
                            .compile("([\\u4e00-\\u9fa5]+[a-zA-Z\\u4e00-\\u9fa5]*)?[_\\- ]?(?:(?:\\d+[_\\-\\:\\/ ]{1}\\d+[_\\-\\:\\/ ]{1}\\d+(?:[_\\-\\:\\/ ]{1}\\d+[_\\-\\:\\/ ]{1}\\d+)?)|\\d{14}|\\d{8}|\\d{6})[_\\- ]?([\\u4e00-\\u9fa5]+[a-zA-Z0-9\\u4e00-\\u9fa5]*)?.[a-zA-Z0-9]+$");
                    matcher1 = pattern1.matcher(name);
                    if (matcher1.find()) {
                        String matcher_1 = matcher1.group(1);
                        String matcher_2 = matcher1.group(2);
                        if (matcher_1 != null && matcher_1.length() > 0) {
                            if (matcher_1.startsWith("_") || matcher_1.startsWith("-") || matcher_1.startsWith(" ")) {
                                matcher_1 = matcher_1.substring(1);
                            }
                            if (matcher_1.endsWith("_") || matcher_1.endsWith("-") || matcher_1.endsWith(" ")) {
                                matcher_1 = matcher_1.substring(0, matcher_1.length() - 1);
                            }
                            if (!matcher_1.matches("\\d+")) {
                                description2 = matcher_1;
                            }
                        }

                        if (matcher_2 != null && matcher_2.length() > 0) {
                            if (matcher_2.startsWith("_") || matcher_2.startsWith("-") || matcher_2.startsWith(" ")) {
                                matcher_2 = matcher_2.substring(1);
                            }
                            if (matcher_2.endsWith("_") || matcher_2.endsWith("-") || matcher_2.endsWith(" ")) {
                                matcher_2 = matcher_2.substring(0, matcher_2.length() - 1);
                            }
                            if (!matcher_2.matches("\\d+")) {
                                description2 = matcher_2;
                            }
                        }
                    }
                }
            }
            fullPath = file.getAbsolutePath();
            suffixName = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

            if (!getImageMetadata(file)) {
                if (suffixName.equals("mts") || suffixName.equals("m2ts")
                        || suffixName.equals("modd") || suffixName.equals("mp4")
                        || suffixName.equals("m4a") || suffixName.equals("m2t")
                        || suffixName.equals("avi") || suffixName.equals("3gp")
                        || suffixName.equals("mov")) {
                    fileType = FILE_TYPE_VIDEO;
                }
            }
            if (Latitude != null && Latitude.length() > 0 && Longitude != null && Longitude.length() > 0) {
                position = BaiduGps2City.getGps2City(Latitude, Longitude);
                if (position != null && position.length > 0 && description2 != null && description2.length() > 0) {
                    for (String _tmp : position) {
                        if (_tmp.equals(description2)) {
                            description2 = null;
                            break;
                        }
                    }
                }
            }

            if (exifDate == null || exifDate.length() == 0) {
                Pattern pattern = Pattern.compile("\\d{14}");
                Matcher matcher = pattern.matcher(fileName);
                if (matcher.find()) {
                    try {
                        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                        Date date = df.parse(matcher.group());
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                        exifDate = sdf.format(date);
                        exifDateLong = date.getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (exifDate == null || exifDate.length() == 0) {
                Pattern pattern = Pattern.compile("\\d{8}_\\d{6}");
                Matcher matcher = pattern.matcher(fileName);
                if (matcher.find()) {
                    try {
                        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
                        Date date = df.parse(matcher.group());
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                        exifDate = sdf.format(date);
                        exifDateLong = date.getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (exifDate == null || exifDate.length() == 0) {
                Pattern pattern = Pattern.compile("\\d{8}_\\d{6}");
                Matcher matcher = pattern.matcher(parentFolderName);
                if (matcher.find()) {
                    try {
                        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
                        Date date = df.parse(matcher.group());
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                        exifDate = sdf.format(date);
                        exifDateLong = date.getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (exifDate == null || exifDate.length() == 0) {
                exifDateLong = file.lastModified();
                if (exifDateLong > 0) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                    exifDate = sdf.format(new Date(exifDateLong));
                } else {
                    exifDateLong = 0;
                }
            }
            if ((fileType == null || fileType.length() == 0) && suffixName.toLowerCase().equals("jpg")) {
                fileType = FILE_TYPE_IMAGE;
            }
        }

        System.out.format("\n************************\nexifModel=%s\nexifDate=%s\nfullPath=%s\nfileName=%s\nfileType=%s\nsuffixName=%s\ndescription1=%s\ndescription2=%s\nposition=%s\n************************",
                exifModel, exifDate, fullPath, fileName, fileType, suffixName, description1, description2, position != null ? position[0] : null);
    }

    private boolean getImageMetadata(File file) {
        boolean ret = false;
        if (file.isFile() && file.exists()) {
            try {
                Metadata metadata = ImageMetadataReader.readMetadata(file.getAbsoluteFile());
                for (Directory directory : metadata.getDirectories()) {
                    if (directory.getName().equals("JPEG")
                            || directory.getName().contains("PNG")
                            || directory.getName().equals("Photoshop")) {
                        fileType = FILE_TYPE_IMAGE;
                    } else if (directory.getName().equals("MP4")
                            || directory.getName().equals("AVI")
                            || directory.getName().equals("QuickTime Video")) {
                        fileType = FILE_TYPE_VIDEO;
                    }
                    String GPSTime = "";
                    for (Tag tag : directory.getTags()) {
                        System.out.format("\n[%s] - %s = %s", directory.getName(), tag.getTagName(), tag.getDescription());
                        if (tag.getTagName().equals("Make") && !tag.getDescription().trim().equals("N/A")) {
                            if (exifModel == null || exifModel.length() == 0) {
                                exifModel = tag.getDescription().trim().replaceAll(" ", "");
                            }
                        } else if (tag.getTagName().equals("Detected File Type Name")) {
                            String tmp = tag.getDescription().trim();
                            if (tmp.equals("CR2")) {
                                fileType = FILE_TYPE_IMAGE;
                            }
                        } else if (tag.getTagName().equals("Detected MIME Type")) {
                            String mimetype = tag.getDescription().trim();
                            if (mimetype.startsWith("video/")) {
                                fileType = FILE_TYPE_VIDEO;
                            } else if (mimetype.startsWith("image/")) {
                                fileType = FILE_TYPE_IMAGE;
                            }
                        } else if (tag.getTagName().equals("Model") && !tag.getDescription().trim().equals("N/A")) {
                            exifModel = tag.getDescription().trim().replaceAll(" ", "");
                        } else if (tag.getTagName().equals("Date/Time Original")) {
                            String strTime = tag.getDescription().trim();
                            if (strTime != null && strTime.matches("\\d{4}:\\d{2}:\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                                SimpleDateFormat df = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                                Date date = df.parse(strTime);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                                exifDate = sdf.format(date);
                                if (exifDate.startsWith("1904")) {
                                    exifDate = null;
                                } else {
                                    exifDateLong = date.getTime();
                                }
                            }
                        } else if (tag.getTagName().equals("Date/Time") && (exifDate == null || exifDate.length() == 0)) {
                            String strTime = tag.getDescription().trim();
                            if (strTime != null && strTime.matches("\\d{4}:\\d{2}:\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                                SimpleDateFormat df = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                                Date date = df.parse(strTime);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                                exifDate = sdf.format(date);
                                if (exifDate.startsWith("1904")) {
                                    exifDate = null;
                                } else {
                                    exifDateLong = date.getTime();
                                }
                            }
                        } else if (tag.getTagName().equals("Datetime Original")) {
                            String strTime = tag.getDescription().trim();
                            strTime = strTime.replace("星期一", "Mon");
                            strTime = strTime.replace("星期二", "Tue");
                            strTime = strTime.replace("星期三", "Wed");
                            strTime = strTime.replace("星期四", "Thu");
                            strTime = strTime.replace("星期五", "Fri");
                            strTime = strTime.replace("星期六", "Sat");
                            strTime = strTime.replace("星期日", "Sun");

                            strTime = strTime.replace("十一月", "Nov");
                            strTime = strTime.replace("十二月", "Dec");
                            strTime = strTime.replace("一月", "Jan");
                            strTime = strTime.replace("二月", "Feb");
                            strTime = strTime.replace("三月", "Mar");
                            strTime = strTime.replace("四月", "Apr");
                            strTime = strTime.replace("五月", "May");
                            strTime = strTime.replace("六月", "Jun");
                            strTime = strTime.replace("七月", "Jul");
                            strTime = strTime.replace("八月", "Aug");
                            strTime = strTime.replace("九月", "Sep");
                            strTime = strTime.replace("十月", "Oct");

                            if (strTime != null && strTime.matches("\\w{3} \\w{3} \\d{2} \\d{2}:\\d{2}:\\d{2} \\d{4}")) {
                                SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.US);
                                Date date = df.parse(strTime);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                                exifDate = sdf.format(date);
                                if (exifDate.startsWith("1904")) {
                                    exifDate = null;
                                } else {
                                    exifDateLong = date.getTime();
                                }
                            }
                        } else if ((tag.getTagName().equals("Modification Time")
                                || tag.getTagName().equals("Creation Time")
                                || tag.getTagName().equals("File Modified Date"))
                                && (exifDate == null || exifDate.length() == 0)) {
                            String strTime = tag.getDescription().trim();
                            strTime = strTime.replace("星期一", "Mon");
                            strTime = strTime.replace("星期二", "Tue");
                            strTime = strTime.replace("星期三", "Wed");
                            strTime = strTime.replace("星期四", "Thu");
                            strTime = strTime.replace("星期五", "Fri");
                            strTime = strTime.replace("星期六", "Sat");
                            strTime = strTime.replace("星期日", "Sun");

                            strTime = strTime.replace("十一月", "Nov");
                            strTime = strTime.replace("十二月", "Dec");
                            strTime = strTime.replace("一月", "Jan");
                            strTime = strTime.replace("二月", "Feb");
                            strTime = strTime.replace("三月", "Mar");
                            strTime = strTime.replace("四月", "Apr");
                            strTime = strTime.replace("五月", "May");
                            strTime = strTime.replace("六月", "Jun");
                            strTime = strTime.replace("七月", "Jul");
                            strTime = strTime.replace("八月", "Aug");
                            strTime = strTime.replace("九月", "Sep");
                            strTime = strTime.replace("十月", "Oct");
                            strTime = strTime.replace("+08:00", "CST");

                            if (strTime != null && strTime.matches("\\w{3} \\w{3} \\d{2} \\d{2}:\\d{2}:\\d{2} \\w{3} \\d{4}")) {
                                SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                                Date date = df.parse(strTime);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                                exifDate = sdf.format(date);
                                if (exifDate.startsWith("1904")) {
                                    exifDate = null;
                                } else {
                                    exifDateLong = date.getTime();
                                }
                            }
                        } else if (tag.getTagName().equals("Duration")) {
                            String _duration = tag.getDescription().trim();
                            if (_duration != null && _duration.matches("\\d+")) {
                                duration = Long.valueOf(tag.getDescription().trim());
                            } else if (_duration != null && _duration.matches("\\d{2}:\\d{2}:\\d{2}")) {
                                String[] tmp = _duration.split(":");
                                if (tmp != null && tmp.length == 3) {
                                    duration = (Long.valueOf(tmp[0]) * 3600 + Long.valueOf(tmp[1]) * 60 + Long.valueOf(tmp[2])) * 1000;
                                }
                            }
                        } else if (tag.getTagName().equals("GPS Latitude")) {
                            Latitude = tag.getDescription().trim().replaceAll(" ", "");
                        } else if (tag.getTagName().equals("GPS Longitude")) {
                            Longitude = tag.getDescription().trim().replaceAll(" ", "");
                        } else if (tag.getTagName().equals("GPS Time-Stamp")) {
                            GPSTime += " " + tag.getDescription().trim();
                        } else if (tag.getTagName().equals("GPS Date Stamp")) {
                            GPSTime = tag.getDescription().trim() + GPSTime;
                        }
                    }
                    if (directory.hasErrors()) {
                        for (String error : directory.getErrors()) {
                            System.err.format("\nERROR: %s", error);
                        }
                    }
                    if (GPSTime != null && GPSTime.matches("\\d{4}:\\d{2}:\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3} UTC")) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                        try {
                            exifDateLong = format.parse(GPSTime.substring(0, 19)).getTime() + Calendar.getInstance().get(Calendar.ZONE_OFFSET)
                                    + Calendar.getInstance().get(Calendar.DST_OFFSET);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                            exifDate = sdf.format(new Date(exifDateLong));
                        } catch (java.text.ParseException e) {
                        }
                    }
                }
                ret = true;
            } catch (Exception e) {
            }
        }
        return ret;
    }

    public static void main(String[] args) {
        FileInfo fileInfo = new FileInfo(new File("E:\\photo整理\\准备整理2\\photo\\IXUS 800 IS 照片\\已选IXUS 800 IS照片\\2009_12_27\\IMG_4576.JPG.files\\vcm_s_kf_m160_160x120.jpg"));

        String sourceFileName = null;
        if (fileInfo.fileType.equals(FileInfo.FILE_TYPE_IMAGE)) {
            sourceFileName = "IMG_" + fileInfo.exifDate;
        } else if (fileInfo.fileType.equals(FileInfo.FILE_TYPE_VIDEO)) {
            sourceFileName = "MOV_" + fileInfo.exifDate + "_" + fileInfo.duration;
        }
        sourceFileName += "_";
        if (fileInfo.exifModel != null && fileInfo.exifModel.length() > 0) {
            sourceFileName += fileInfo.exifModel;
        }
        sourceFileName += "_";
        if (fileInfo.position != null && fileInfo.position.length > 0) {
            String tmp = "";
            for (String _position : fileInfo.position) {
                tmp += "," + _position;
            }
            tmp = tmp.substring(1);
            sourceFileName += tmp;
        }
        sourceFileName += "_";
        if (fileInfo.description2 != null && fileInfo.description2.length() > 0) {
            sourceFileName += fileInfo.description2;
        } else if (fileInfo.description1 != null && fileInfo.description1.length() > 0) {
            sourceFileName += fileInfo.description1;
        }
        sourceFileName += "." + fileInfo.suffixName;
        System.out.print("\nsourceFileName=" + sourceFileName + "\n");
    }
}
