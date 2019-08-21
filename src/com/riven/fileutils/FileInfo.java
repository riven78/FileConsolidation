package com.riven.fileutils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileInfo {
    public static final String FILE_TYPE_IMAGE = "image";
    public static final String FILE_TYPE_AUDIO = "audio";
    public static final String FILE_TYPE_VIDEO = "video";
    public static final String FILE_TYPE_OTHER = "other";

    public String exifModel = "";
    public String exifDate = null;
    public long exifDateLong = 0L;
    public long duration = 0L;
    public String fullPath = null;
    public String fileName = null;
    public String fileType = null;
    public String suffixName = null;
    public String[] description = null;
    private String Latitude = null;
    private String Longitude = null;
    public String[] position = null;

    public FileInfo(File file) {
        if (file != null && file.exists() && file.isFile()) {
            ArrayList<String> _description = new ArrayList<>();
            String parentFolderName = file.getParent().substring(file.getParent().lastIndexOf(File.separator) + 1);
            Pattern pattern2 = Pattern
                    .compile("\\[[\\w\\W]+\\](?:.\\[[\\w\\W]*\\]){1,3}");
            Matcher matcher2 = pattern2.matcher(parentFolderName);
            if (!matcher2.find()) {
                Pattern pattern1 = Pattern
                        .compile("([\\w\\W]*)(?:(?:\\d{8}(?:-){1}\\d{2,4}){1}|(?:\\d{4}(?:-|[_/ ]){1}\\d{2}(?:-|[_/ ]){1}\\d{2}){1}|(?:\\d{8}){1})([\\w\\W]*)");
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
                        boolean found = false;
                        for (String tmp : _description) {
                            if (found = tmp.equals(matcher_1)) {
                                break;
                            }
                        }
                        if (!found) {
                            _description.add(matcher_1);
                        }
                    }

                    if (matcher_2 != null && matcher_2.length() > 0) {
                        if (matcher_2.startsWith("_") || matcher_2.startsWith("-") || matcher_2.startsWith(" ")) {
                            matcher_2 = matcher_2.substring(1);
                        }
                        if (matcher_2.endsWith("_") || matcher_2.endsWith("-") || matcher_2.endsWith(" ")) {
                            matcher_2 = matcher_2.substring(0, matcher_2.length() - 1);
                        }
                        boolean found = false;
                        for (String tmp : _description) {
                            if (found = tmp.equals(matcher_2)) {
                                break;
                            }
                        }
                        if (!found) {
                            _description.add(matcher_2);
                        }
                    }
                }
            }
            fileName = file.getName();
            fullPath = file.getAbsolutePath();
            suffixName = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

            if (!getImageMetadata(file)) {
                if (suffixName.equals("mts") || suffixName.equals("m2ts")
                        || suffixName.equals("modd") || suffixName.equals("mp4")
                        || suffixName.equals("m4a")) {
                    fileType = FILE_TYPE_VIDEO;
                } else if (fileType == null || fileType.length() == 0) {
                    fileType = FILE_TYPE_OTHER;
                }
            }
            if (Latitude != null && Latitude.length() > 0 && Longitude != null && Longitude.length() > 0) {
                position = BaiduGps2City.getGps2City(Latitude, Longitude);
            }
            {
                Pattern pattern = Pattern.compile("(截屏|截图)_(\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\d{2})");
                Matcher matcher = pattern.matcher(fileName);
                if (matcher.find()) {
                    String matcher_1 = matcher.group(1).trim();
                    String matcher_2 = matcher.group(2).trim();
                    boolean found = false;
                    for (String tmp : _description) {
                        if (found = tmp.equals(matcher_1)) {
                            break;
                        }
                    }
                    if (!found) {
                        _description.add(matcher_1);
                    }
                    if (matcher_2 != null && matcher_2.length() > 0 && (exifDate == null || exifDate.length() == 0)) {
                        try {
                            SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                            Date date = df.parse(matcher.group(2));
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                            exifDate = sdf.format(date);
                            exifDateLong = date.getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
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
            if (exifModel != null && exifModel.length() > 0) {
                for (String tmp : _description) {
                    if (tmp.equals(exifModel)) {
                        _description.remove(tmp);
                        break;
                    }
                }
            }
            if (_description.size() > 0) {
                description = new String[_description.size()];
                for (int i = 0; i < _description.size(); i++) {
                    description[i] = _description.get(i);
                }
            }
            if((fileType==null || fileType.length()==0) && suffixName.toLowerCase().equals("jpg")){
                fileType = FILE_TYPE_IMAGE;
            }
        }

        System.out.format("\n************************\nexifModel=%s\nexifDate=%s\nfullPath=%s\nfileName=%s\nfileType=%s\nsuffixName=%s\ndescription=%s\nposition=%s\n************************",
                exifModel, exifDate, fullPath, fileName, fileType, suffixName, description, position);
    }

    private boolean getImageMetadata(File file) {
        boolean ret = false;
        if (file.isFile() && file.exists()) {
            try {
                Metadata metadata = ImageMetadataReader.readMetadata(file.getAbsoluteFile());
                for (Directory directory : metadata.getDirectories()) {
                    if (directory.getName().equals("JPEG")) {
                        fileType = FILE_TYPE_IMAGE;
                    } else if (directory.getName().equals("MP4")) {
                        fileType = FILE_TYPE_VIDEO;
                    }
                    for (Tag tag : directory.getTags()) {
                        System.out.format("\n[%s] - %s = %s", directory.getName(), tag.getTagName(), tag.getDescription());
                        if (tag.getTagName().equals("Make")) {
                            if (exifModel == null || exifModel.length() == 0) {
                                exifModel = tag.getDescription().trim().replaceAll(" ", "");
                            }
                        } else if (tag.getTagName().equals("Model")) {
                            exifModel = tag.getDescription().trim().replaceAll(" ", "");
                        } else if (tag.getTagName().equals("Date/Time Original")) {
                            SimpleDateFormat df = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                            Date date = df.parse(tag.getDescription().trim());
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                            exifDate = sdf.format(date);
                            exifDateLong = date.getTime();
                        } else if (tag.getTagName().equals("Date/Time") && (exifDate == null || exifDate.length() == 0)) {
                            SimpleDateFormat df = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                            Date date = df.parse(tag.getDescription().trim());
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                            exifDate = sdf.format(date);
                            exifDateLong = date.getTime();
                        } else if ((tag.getTagName().equals("Modification Time") || tag.getTagName().equals("Creation Time"))
                                && (exifDate == null || exifDate.length() == 0)) {
                            SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                            Date date = df.parse(tag.getDescription().trim());
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                            exifDate = sdf.format(date);
                            exifDateLong = date.getTime();
                        } else if (tag.getTagName().equals("Duration")) {
                            duration = Long.valueOf(tag.getDescription().trim());
                        } else if (tag.getTagName().equals("GPS Latitude")) {
                            Latitude = tag.getDescription().trim().replaceAll(" ", "");
                        } else if (tag.getTagName().equals("GPS Longitude")) {
                            Longitude = tag.getDescription().trim().replaceAll(" ", "");
                        }
                    }
                    if (directory.hasErrors()) {
                        for (String error : directory.getErrors()) {
                            System.err.format("ERROR: %s", error);
                        }
                    }
                }
                ret = true;
            } catch (Exception e) {
            }
        }
        return ret;
    }
}
