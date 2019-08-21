package com.riven.fileutils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingWorker;

import com.riven.fileutils.DigestUtils.FileEx;

public abstract class CheckDuplicateFiles extends SwingWorker<Boolean, String> {
	private File executeFolder;

	private final ArrayList<FileEx> fileList = new ArrayList<>();
	private final ArrayList<File> folderList = new ArrayList<>();
	private final ArrayList<ArrayList<FileEx>> duplicateFilesList = new ArrayList<>();
	private boolean stop = false;

	public CheckDuplicateFiles(File executeFolder) {
		this.executeFolder = executeFolder;
	}

	public void stop() {
		this.stop = true;
	}

	public abstract void drawTabel(ArrayList<ArrayList<FileEx>> duplicateFilesList);

	@Override
	protected Boolean doInBackground() throws Exception {
		Boolean ret = false;
		publish("开始获取待文件");
		fileList.clear();
		folderList.clear();
		for (ArrayList<FileEx> list : duplicateFilesList) {
			list.clear();
		}
		duplicateFilesList.clear();
		HashMap<String, String> md5Map = new HashMap<>();
		getFiles(executeFolder);
		if (fileList.size() > 0) {
			publish(String.format("\n共有%d个目录和%d个文件待处理......\n", folderList.size(), fileList.size()));
			Map<Long, ArrayList<FileEx>> duplicateFiles = new HashMap<>();
			Map<Long, Integer> map = new HashMap<>();
			int index = 0;
			for (FileEx fileEx : fileList) {
				if (stop)
					break;
				if (map.containsKey(fileEx.getFileSize())) {
					if (duplicateFiles.containsKey(fileEx.getFileSize())) {
						ArrayList<FileEx> list = duplicateFiles.get(fileEx.getFileSize());
						list.add(fileEx);
					} else {
						ArrayList<FileEx> list = new ArrayList<>();
						list.add(fileList.get(map.get(fileEx.getFileSize())));
						list.add(fileEx);
						duplicateFiles.put(fileEx.getFileSize(), list);
					}
					publish(String.format("\n[%s]文件长度有重复", fileEx.getFile().getAbsoluteFile()));
				} else {
					map.put(fileEx.getFileSize(), index);
					publish(".");
				}
				index++;
			}
			map.clear();

//			Map<String, ArrayList<FileEx>> duplicateFilesMD5 = new HashMap<>();
//			for (Map.Entry<Long, ArrayList<FileEx>> entry : duplicateFiles.entrySet()) {
//				ArrayList<FileEx> list = entry.getValue();
//				Map<String, Integer> mapMD5 = new HashMap<>();
//				for (int i = 0; i < list.size(); i++) {
//					FileEx fileEx = list.get(i);
//					if (mapMD5.containsKey(fileEx.getFileSize() + fileEx.getMD5())) {
//						if (duplicateFilesMD5.containsKey(fileEx.getFileSize() + fileEx.getMD5())) {
//							ArrayList<FileEx> list1 = duplicateFilesMD5.get(fileEx.getFileSize() + fileEx.getMD5());
//							list1.add(fileEx);
//						} else {
//							ArrayList<FileEx> list1 = new ArrayList<>();
//							list1.add(list.get(mapMD5.get(fileEx.getFileSize() + fileEx.getMD5())));
//							list1.add(fileEx);
//							duplicateFilesMD5.put(fileEx.getFileSize() + fileEx.getMD5(), list1);
//						}
//						publish(String.format("\n[%s]文件MD5值有重复", fileEx.getFile().getAbsoluteFile()));
//					} else {
//						mapMD5.put(fileEx.getFileSize() + fileEx.getMD5(), i);
//						publish(".");
//					}
//				}
//				mapMD5.clear();
//				list.clear();
//			}
//			duplicateFiles.clear();

			for (Map.Entry<Long, ArrayList<FileEx>> entry : duplicateFiles.entrySet()) {
				if (stop)
					break;
				ArrayList<FileEx> list = entry.getValue();
				int i = 0;
				while (i < (list.size() - 1)) {
					FileEx fileEx1 = list.get(i);
					ArrayList<FileEx> newList = new ArrayList<>();
					boolean found = false;
					int j = i + 1;
					while (j < list.size()) {
						FileEx fileEx2 = list.get(j);
						if (DigestUtils.equalFileContent(fileEx1.getFile(), fileEx2.getFile())) {
							found = true;
							newList.add(fileEx2);
							list.remove(j);
							publish(String.format("\n[%s]文件内容重复", fileEx2.getFile().getAbsoluteFile()));
						} else {
							j++;
							publish(".");
						}
					}
					if (found) {
						newList.add(fileEx1);
						duplicateFilesList.add(newList);
					}
					i++;
				}
				list.clear();
			}
			duplicateFiles.clear();
		}
		drawTabel(duplicateFilesList);
		ret = true;
		md5Map.clear();
		fileList.clear();
		folderList.clear();
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
						fileList.add(new FileEx(file));
						publish(".");
					}
				}
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		fileList.clear();
		folderList.clear();
	}

}
