package com.riven.fileutils;

import com.riven.fileutils.DigestUtils.FileEx;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

public class MakeImageFile extends SwingWorker<Boolean, ProcessMsg> {
    private File executeFolder;
    private File saveFolder;
    private final ArrayList<File> fileList = new ArrayList<>();
    private final ArrayList<File> folderList = new ArrayList<>();
    private int moveCount = 0;
    private int deleteCount = 0;
    private final HashMap<String, FileEx> compareFiles = new HashMap<>();
    private boolean stop = false;

    public MakeImageFile(File executeFolder, File saveFolder) {
        this.executeFolder = executeFolder;
        this.saveFolder = saveFolder;
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        Boolean ret = false;
        publish(new ProcessMsg(0, 0, "开始获取待文件......"));
        compareFiles.clear();
        fileList.clear();
        folderList.clear();
        moveCount = 0;
        deleteCount = 0;
        getFiles(executeFolder);
        if (fileList.size() > 0 || folderList.size() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            File makeImageFileCSV = new File(saveFolder.getAbsoluteFile(),
                    String.format("makeImageFile_%s.csv", sdf.format(new Date(System.currentTimeMillis()))));
            if (makeImageFileCSV.exists()) {
                makeImageFileCSV.delete();
            }
            makeImageFileCSV.createNewFile();
            FileOutputStream fos = new FileOutputStream(makeImageFileCSV);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);
            byte[] data = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
            bw.write(new String(data));
            boolean hasWriteFile = false;
            publish(new ProcessMsg(0, 0, String.format("共有%d个目录和%d个文件待处理......", folderList.size(), fileList.size())));
            File[] saveFolderList = saveFolder.listFiles();
            ArrayList<String> saveFolderNameList = new ArrayList<>();
            if (saveFolderList != null && saveFolderList.length > 0) {
                for (int i = 0; i < saveFolderList.length; i++) {
                    if (saveFolderList[i].isDirectory()) {
                        saveFolderNameList.add(saveFolderList[i].getName());
                    }
                }
            }
            Collections.sort(saveFolderNameList, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            });
            for (int i = 0; i < fileList.size() && !stop; i++) {
                File source = fileList.get(i);
                FileInfo fileInfo = new FileInfo(source);
                ProcessMsg msg = new ProcessMsg(i, fileList.size() + folderList.size(), null);
                if (fileInfo.fileType != null && !fileInfo.fileType.equals(FileInfo.FILE_TYPE_OTHER)
                        && fileInfo.exifDate != null && fileInfo.exifDate.length() > 0) {
                    String saveFolderName = "[" + fileInfo.exifDate.substring(0, 8) + "]";

                    String found = null;
                    boolean hasChanged = false;
                    for (String tmp : saveFolderNameList) {
                        String[] _tmp = tmp.split("\\]\\.\\[");
                        if (_tmp != null && _tmp.length > 1 && _tmp[0].startsWith("[")) {
                            _tmp[0] = _tmp[0].substring(1);
                            if (_tmp[1] != null && _tmp[1].length() > 0) {
                                if (_tmp[1].endsWith("]")) {
                                    _tmp[1] = _tmp[1].substring(0, _tmp[1].length() - 1);
                                }
                                if ((fileInfo.fileType.equals(FileInfo.FILE_TYPE_IMAGE) && _tmp[1].equals("IMG"))
                                        || (fileInfo.fileType.equals(FileInfo.FILE_TYPE_VIDEO) && _tmp[1].equals("MOV"))
                                        || (fileInfo.fileType.equals(FileInfo.FILE_TYPE_OTHER) && _tmp[1].equals("OTH"))) {
                                    String[] times = _tmp[0].split(",");
                                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                                    long startTime = df.parse(times[0] + "000000").getTime();
                                    long endTime = 0;
                                    String _endTime = null;
                                    if (times.length == 2) {
                                        endTime = df.parse(times[1] + "000000").getTime();
                                        _endTime = times[1];
                                    } else {
                                        endTime = startTime;
                                        _endTime = times[0];
                                    }
                                    if (fileInfo.exifDateLong >= startTime
                                            && fileInfo.exifDateLong < (endTime + (3600 * 24 * 1000 * 2))) {
                                        found = tmp;
                                        saveFolderName = "[" + times[0];

                                        if (fileInfo.exifDateLong > (endTime + (3600 * 24 * 1000))) {
                                            hasChanged = true;
                                            saveFolderName += "," + fileInfo.exifDate.substring(0, 8) + "]";
                                        } else if (!saveFolderName.contains(_endTime)) {
                                            saveFolderName += "," + _endTime + "]";
                                        } else {
                                            saveFolderName += "]";
                                        }
                                    } else if (fileInfo.exifDateLong < startTime) {
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    if (fileInfo.fileType.equals(FileInfo.FILE_TYPE_IMAGE)) {
                        saveFolderName += ".[IMG]";
                    } else if (fileInfo.fileType.equals(FileInfo.FILE_TYPE_VIDEO)) {
                        saveFolderName += ".[MOV]";
                    } else if (fileInfo.fileType.equals(FileInfo.FILE_TYPE_OTHER)) {
                        saveFolderName += ".[OTH]";
                    }
                    String[] position = null;
                    String[] description = null;
                    if (found != null && found.length() > 0) {
                        saveFolderNameList.remove(found);
                        String[] tmp = found.split("\\]\\.\\[");
                        if (tmp.length >= 3 && tmp[2] != null && tmp[2].length() > 0) {
                            if (tmp[2].endsWith("]")) {
                                tmp[2] = tmp[2].substring(0, tmp[2].length() - 1);
                            }
                            position = tmp[2].split(",");
                        }
                        if (tmp.length == 4 && tmp[3] != null && tmp[3].length() > 1) {
                            if (tmp[3].endsWith("]")) {
                                tmp[3] = tmp[3].substring(0, tmp[3].length() - 1);
                            }
                            description = tmp[3].split(",");
                        }
                    }

                    if (fileInfo.position != null && fileInfo.position.length > 0) {
                        if (position != null) {
                            for (int j = 0; j < fileInfo.position.length; j++) {
                                boolean found1 = false;
                                for (int k = 0; k < position.length; k++) {
                                    if (fileInfo.position[j].equals(position[k])) {
                                        found1 = true;
                                        break;
                                    }
                                }
                                if (!found1) {
                                    String[] _position = new String[position.length + 1];
                                    for (int n = 0; n < position.length; n++) {
                                        _position[n] = position[n];
                                    }
                                    _position[position.length] = fileInfo.position[j];
                                    position = _position;
                                    hasChanged = true;
                                }
                            }
                        } else {
                            position = fileInfo.position;
                            hasChanged = true;
                        }
                    }
                    if (fileInfo.description != null && fileInfo.description.length > 0) {
                        if (description != null) {
                            for (int j = 0; j < fileInfo.description.length; j++) {
                                boolean found1 = false;
                                for (int k = 0; k < description.length; k++) {
                                    if (fileInfo.description[j].equals(description[k])) {
                                        found1 = true;
                                        break;
                                    }
                                }
                                if (!found1) {
                                    String[] _description = new String[description.length + 1];
                                    for (int n = 0; n < description.length; n++) {
                                        _description[n] = description[n];
                                    }
                                    _description[description.length] = fileInfo.description[j];
                                    description = _description;
                                    hasChanged = true;
                                }
                            }
                        } else {
                            description = fileInfo.description;
                            hasChanged = true;
                        }
                    }
                    if (position != null && position.length > 0) {
                        String tmp = "";
                        for (String _position : position) {
                            tmp += "," + _position;
                        }
                        tmp = tmp.substring(1);
                        saveFolderName += ".[" + tmp + "]";
                    } else if (description != null && description.length > 0) {
                        saveFolderName += ".[]";
                    }
                    if (description != null && description.length > 0) {
                        String tmp = "";
                        for (String _description : description) {
                            tmp += "," + _description;
                        }
                        tmp = tmp.substring(1);
                        saveFolderName += ".[" + tmp + "]";
                    }
                    String savePath = saveFolder.getAbsolutePath() + File.separator + saveFolderName + File.separator;
                    File destFolder = new File(savePath);
                    if (found != null && found.length() > 0 && hasChanged) {
                        String oldPath = saveFolder.getAbsolutePath() + File.separator + found + File.separator;
                        File oldFolder = new File(oldPath);
                        oldFolder.renameTo(destFolder);

                    } else if (!destFolder.exists()) {
                        destFolder.mkdirs();
                    }
                    saveFolderNameList.add(saveFolderName);
                    Collections.sort(saveFolderNameList, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    String sourceFileName = null;
                    if (fileInfo.fileType.equals(FileInfo.FILE_TYPE_IMAGE)) {
                        sourceFileName = "IMG_" + fileInfo.exifDate;
                    } else if (fileInfo.fileType.equals(FileInfo.FILE_TYPE_VIDEO)) {
                        sourceFileName = "MOV_" + fileInfo.exifDate;
                    } else {
                        sourceFileName = fileInfo.exifDate;
                    }
                    if (fileInfo.duration > 0) {
                        sourceFileName += "_" + fileInfo.duration;
                    }
                    if (fileInfo.exifModel != null && fileInfo.exifModel.length() > 0) {
                        sourceFileName += "_" + fileInfo.exifModel;
                    }
                    if (fileInfo.position != null && fileInfo.position.length > 0) {
                        String tmp = "";
                        for (String _position : fileInfo.position) {
                            tmp += "," + _position;
                        }
                        tmp = tmp.substring(1);
                        sourceFileName += "_" + tmp;
                    }
                    if (fileInfo.description != null && fileInfo.description.length > 0) {
                        String tmp = "";
                        for (String _description : fileInfo.description) {
                            tmp += "," + _description;
                        }
                        tmp = tmp.substring(1);
                        sourceFileName += "_" + tmp;
                    }
                    sourceFileName += "." + fileInfo.suffixName;

                    File dest = new File(destFolder.getAbsoluteFile(), sourceFileName);
                    if (dest.exists()) {
                        FileEx destComp = compareFiles.get(dest.getAbsolutePath());
                        if (destComp == null) {
                            destComp = new FileEx(dest);
                            compareFiles.put(dest.getAbsolutePath(), destComp);
                        }
                        FileEx sourceComp = new FileEx(source);
                        if (DigestUtils.equalFile(destComp, sourceComp)) {
                            CSVObject csvObject = new CSVObject();
                            csvObject.add("move");
                            csvObject.add("file");
                            csvObject.add(source.getAbsolutePath());
                            csvObject.add(dest.getAbsolutePath());
                            bw.write(csvObject.toString() + "\r\n");
                            hasWriteFile = true;
                            msg.msg = String.format("[%s]目标目录中此文件已存在。\nDelete %s is %s", source.toPath(), source.toPath(),
                                    source.delete());
                            deleteCount++;
                            dest = null;
                        } else {
                            dest = new File(destFolder.getAbsoluteFile(),
                                    sourceFileName.substring(0, (sourceFileName.length() - ("." + fileInfo.suffixName).length()))
                                            + "_" + System.currentTimeMillis() + "." + fileInfo.suffixName);
                        }
                    }
                    if (dest != null) {
                        System.out.format("\nsource = %s;dest = %s", source.toPath(), dest.toPath());
                        CSVObject csvObject = new CSVObject();
                        csvObject.add("move");
                        csvObject.add("file");
                        csvObject.add(source.getAbsolutePath());
                        csvObject.add(dest.getAbsolutePath());
                        bw.write(csvObject.toString() + "\r\n");
                        hasWriteFile = true;
                        if (Files.move(source.toPath(), dest.toPath()).equals(dest.toPath())) {
                            moveCount++;
                            msg.msg = String.format("MOVE %s Success.", dest.toPath());
                        }
                    }
                } else {
                    msg.msg = String.format("[%s]未知类型跳过整理。", source.toPath());
                }
                publish(msg);
            }
            for (int i = 0; i < folderList.size(); i++) {
                File source = folderList.get(i);
                ProcessMsg msg = new ProcessMsg(i + fileList.size(), fileList.size() + folderList.size(), null);
                File[] list = source.listFiles();
                if (list == null || list.length == 0) {
                    CSVObject csvObject = new CSVObject();
                    csvObject.add("delete");
                    csvObject.add("folder");
                    csvObject.add(source.getAbsolutePath());
                    csvObject.add("");
                    bw.write(csvObject.toString() + "\r\n");
                    hasWriteFile = true;
                    msg.msg = String.format("[%s] delete is %s.", source.toPath(), source.delete());
                    deleteCount++;
                } else {
                    msg.msg = String.format("[%s] 还有%d子项。", source.toPath(), list.length);
                }
                publish(msg);
            }

            bw.close();
            osw.close();
            fos.close();
            if (!hasWriteFile) {
                makeImageFileCSV.delete();
            }
            fileList.clear();
            folderList.clear();
            compareFiles.clear();
            saveFolderNameList.clear();
            publish(new ProcessMsg(100, 100, String.format("移动%d个文件，删除%d个文件和目录", moveCount, deleteCount)));
            ret = true;
        }
        return ret;
    }

    private void getFiles(File folder) {
        if (folder != null && folder.exists() && folder.isDirectory()) {
            folderList.add(0, folder);
            File[] files = folder.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        getFiles(file);
                    } else {
                        fileList.add(file);
                    }
                }
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        fileList.clear();
        folderList.clear();
        compareFiles.clear();
    }

}