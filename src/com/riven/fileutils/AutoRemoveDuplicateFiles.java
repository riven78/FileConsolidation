package com.riven.fileutils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultTreeModel;

public abstract class AutoRemoveDuplicateFiles extends SwingWorker<Boolean, ProcessMsg> {
	private JTable table;
	private File saveFolder;
	private boolean stop = false;

	public AutoRemoveDuplicateFiles(JTable table, File saveFolder) {
		this.table = table;
		this.saveFolder = saveFolder;
	}

	public void stop() {
		this.stop = true;
	}

	public abstract void drawTree(DefaultTreeModel model);

	class MyFile {
		int index;
		File file;

		public MyFile(int index, File file) {
			this.index = index;
			this.file = file;
		}
	}

	@Override
	protected Boolean doInBackground() throws Exception {
		boolean ret = false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		File makeImageFileCSV = new File(saveFolder.getAbsoluteFile(),
				String.format("AutoRemoveDuplicateFiles_%s.csv", sdf.format(new Date(System.currentTimeMillis()))));
		if (makeImageFileCSV.exists()) {
			makeImageFileCSV.delete();
		}
		makeImageFileCSV.createNewFile();
		FileOutputStream fos = new FileOutputStream(makeImageFileCSV);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
		BufferedWriter bw = new BufferedWriter(osw);
		byte[] data = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
		bw.write(new String(data));
		boolean hasWriteFile = false;

		table.setEnabled(false);
		int rowCount = table.getRowCount();
		int i = 0;
		int step = 0;
		while (i < rowCount && !stop) {
			ArrayList<MyFile> filelist = new ArrayList<>();
			int group = Integer.valueOf((String) table.getValueAt(i, 1));
			while (group < 0 && i < rowCount) {
				group = Integer.valueOf((String) table.getValueAt(++i, 1));
				publish(new ProcessMsg(++step, rowCount));
			}
			for (; i < rowCount; i++) {
				int currentGroup = Integer.valueOf((String) table.getValueAt(i, 1));
				if (currentGroup >= 0) {
					if (currentGroup == group) {
						filelist.add(new MyFile(i, new File((String) table.getValueAt(i, 3))));
					} else {
						break;
					}
				} else {
					publish(new ProcessMsg(++step, rowCount));
				}
			}
			publish(new ProcessMsg(++step, rowCount));
			if (filelist.size() > 1) {
				Collections.sort(filelist, new Comparator<MyFile>() {
					@Override
					public int compare(MyFile o1, MyFile o2) {
						int ret = (int) (o1.file.lastModified() - o2.file.lastModified());
						if (ret == 0) {
							ret = o2.file.getName().length() - o1.file.getName().length();
						}
						return ret;
					}
				});
				String source = filelist.get(filelist.size() - 1).file.getAbsolutePath();

				for (int j = 0; j < filelist.size() - 1 && !stop; j++) {
					MyFile myFile = filelist.get(j);
					CSVObject csvObject = new CSVObject();
					csvObject.add(source);
					csvObject.add(myFile.file.getAbsolutePath());
					bw.write(csvObject.toString() + "\r\n");
					hasWriteFile = true;
					Files.delete(myFile.file.toPath());
					table.setValueAt("-1", myFile.index, 1);
					publish(new ProcessMsg(++step, rowCount));
				}
			}
			filelist.clear();
		}
		bw.close();
		osw.close();
		fos.close();
		if (!hasWriteFile) {
			makeImageFileCSV.delete();
		}
		ret = true;
		return ret;
	}

}
