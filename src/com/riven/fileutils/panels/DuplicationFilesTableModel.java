package com.riven.fileutils.panels;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.swing.table.AbstractTableModel;

import com.riven.fileutils.DigestUtils.FileEx;

public class DuplicationFilesTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private String[] head = { "选择", "组", "文件名", "路径", "修改日期", "大小" };
	@SuppressWarnings("rawtypes")
	private Class[] typeArray = { Boolean.class, Object.class, Object.class, Object.class, Object.class, Object.class };

	public static class MyFile {
		public File file;
		public Boolean isSelected = false;
		public int group;

		public MyFile(int index, File file) {
			this.file = file;
			this.group = index;
		}
	}

	private final ArrayList<MyFile> duplicateFilesList = new ArrayList<>();

	public DuplicationFilesTableModel(ArrayList<ArrayList<FileEx>> _duplicateFilesList) {
		int i = 0;
		for (ArrayList<FileEx> fileExs : _duplicateFilesList) {
			Collections.sort(fileExs, new Comparator<FileEx>() {
				public int compare(FileEx o1, FileEx o2) {
					return (int) (o1.getFile().lastModified() - o2.getFile().lastModified());
				}
			});
			for (FileEx fileEx : fileExs) {
				duplicateFilesList.add(new MyFile(i, fileEx.getFile()));
			}
			fileExs.clear();
			i++;
		}
		_duplicateFilesList.clear();
	}

	@Override
	public int getRowCount() {
		return duplicateFilesList.size();
	}

	@Override
	public int getColumnCount() {
		return head.length;
	}

	@Override
	public String getColumnName(int column) {
		return head[column];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object ret = "";
		if (rowIndex < duplicateFilesList.size()) {
			MyFile myFile = duplicateFilesList.get(rowIndex);
			switch (columnIndex) {
			case 0:
				ret = myFile.isSelected;
				break;
			case 1:
				ret = myFile.group + "";
				break;
			case 2:
				ret = myFile.file.getName();
				break;
			case 3:
				ret = myFile.file.getAbsolutePath();
				break;
			case 4:
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				ret = sdf.format(new Date(myFile.file.lastModified()));
				break;
			case 5:
				ret = myFile.file.length() + "";
				break;
			default:
				break;
			}
		}
		return ret;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		boolean ret = false;
		if (columnIndex == 0) {
			ret = true;
		}
		return ret;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			duplicateFilesList.get(rowIndex).isSelected = (Boolean) aValue;
		} else if (columnIndex == 1) {
			duplicateFilesList.get(rowIndex).group = Integer.valueOf((String) aValue);
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return typeArray[columnIndex];
	}

	@Override
	protected void finalize() throws Throwable {
		duplicateFilesList.clear();
	}

}
