package com.riven.fileutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public abstract class UnMakeImageFile extends SwingWorker<Boolean, DefaultMutableTreeNode> {
	private File csvFile;
	private boolean stop = false;

	public static class NodeUserData {
		public String name, description, type, source, dest;
		public boolean isLeaf;

		public NodeUserData(String name, CSVObject csvObject) {
			this.name = name;
			if (csvObject != null) {
				this.type = csvObject.getString(1);
				this.source = csvObject.getString(3);
				this.dest = csvObject.getString(2);
			}
			description = "";
			isLeaf = true;
		}

		public NodeUserData(String name) {
			this.name = name;
			this.type = "folder";
			description = "";
			isLeaf = false;
		}

		@Override
		public String toString() {
			return name + description;
		}
	}

	public UnMakeImageFile(File csvFile) {
		this.csvFile = csvFile;
	}

	public void stop() {
		this.stop = true;
	}

	public abstract void drawTree(DefaultTreeModel model);

	@Override
	protected Boolean doInBackground() throws Exception {
		boolean ret = false;
		HashMap<String, Object> treeData = getTreeData(csvFile);
		if (treeData.size() > 0) {
			DefaultMutableTreeNode root = new DefaultMutableTreeNode();
			createTreeNode(root, treeData);
			DefaultTreeModel model = new DefaultTreeModel(root);
			drawTree(model);
			DefaultMutableTreeNode treeNode = root.getFirstLeaf();
			while (treeNode != null && !stop) {
				NodeUserData nodeUserData = (NodeUserData) treeNode.getUserObject();
				if (nodeUserData != null && nodeUserData.isLeaf) {
					nodeUserData.description = " >>>>开始恢复...";
					treeNode.setUserObject(nodeUserData);
					publish(treeNode);
					if (nodeUserData.type.equals("folder") && nodeUserData.dest != null) {
//						System.out.print("\nnodeUserData.dest="+nodeUserData.dest);
						File file = new File(nodeUserData.dest);
						if (!file.exists()) {
							file.mkdirs();
						}
					} else if (nodeUserData.type.equals("file") && nodeUserData.dest != null && nodeUserData.source != null) {
//						System.out.print("\nnodeUserData.dest="+nodeUserData.dest);
//						System.out.print("\nnodeUserData.source="+nodeUserData.source);
//						System.out.print("\nnodeUserData.destfolder="+nodeUserData.dest.substring(0, nodeUserData.dest.lastIndexOf("\\") + 1));
						File file = new File(nodeUserData.dest.substring(0, nodeUserData.dest.lastIndexOf("\\") + 1));
						if (!file.exists()) {
							file.mkdirs();
						}
						File source = new File(nodeUserData.source);
						File dest = new File(nodeUserData.dest);

						if (source.exists() && source.isFile() && !dest.exists()) {
							Files.copy(source.toPath(), dest.toPath());
						}
					}
					nodeUserData.description = " >>>>完成";
					treeNode.setUserObject(nodeUserData);
					publish(treeNode);
				}
				treeNode = treeNode.getNextLeaf();
			}
			ret = true;
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> getTreeData(File _csvFile) {
		HashMap<String, Object> treeData = new HashMap<>();
		treeData.clear();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(_csvFile), "UTF-8"));
			int i = 0;
			String str = "";
			while ((str = br.readLine()) != null) {
				if (i == 0) {
					byte[] bytes = str.getBytes("UTF-8");
					if (bytes.length >= 3 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
						str = new String(bytes, 3, bytes.length - 3);
					}
					i++;
				}
				CSVObject csvObject = new CSVObject(str);
				String type = csvObject.getString(1);
				String path = csvObject.getString(2);
				if (type != null && (type.equals("file") || type.equals("folder")) && path != null && path.length() > 0) {
					String root = path.substring(0, path.indexOf(":\\"));
					if (root != null && root.length() > 0) {
						Object parentMap = treeData.get(root);
						path = path.substring(path.indexOf(":\\") + ":\\".length());
						if (path.endsWith("\\")) {
							path = path.substring(0, path.length() - 1);
						}
						if (path.length() > 0) {
							if (parentMap == null || parentMap instanceof String) {
								parentMap = new HashMap<String, Object>();
								treeData.put(root, parentMap);
							}
							String[] _paths = path.split("\\\\");
							for (int j = 0; j < _paths.length && parentMap != null; j++) {
								String _path = _paths[j];
								if (_path != null && _path.length() > 0) {
									Object map = ((HashMap<String, Object>) parentMap).get(_path);
									if (j == _paths.length - 1) {
										if (type.equals("file") || map == null) {
											((HashMap<String, Object>) parentMap).put(_path, csvObject);
										}
									} else {
										if (map == null || map instanceof String) {
											map = new HashMap<String, Object>();
											((HashMap<String, Object>) parentMap).put(_path, map);
										}
										parentMap = map;
									}
								} else {
									break;
								}
							}

						} else {
							if (parentMap == null) {
								treeData.put(root, csvObject);
							}
						}
					}
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return treeData;
	}

	@SuppressWarnings("unchecked")
	private void createTreeNode(DefaultMutableTreeNode treeNode, HashMap<String, Object> data) {
		for (Map.Entry<String, Object> entry : data.entrySet()) {
			Object value = entry.getValue();
			DefaultMutableTreeNode childTreeNode;
			if (value instanceof CSVObject) {
				CSVObject tmp = (CSVObject) value;
				childTreeNode = new DefaultMutableTreeNode(new NodeUserData(entry.getKey(), tmp));
				if (tmp.getString(1).equals("file")) {
					childTreeNode.setAllowsChildren(false);
				} else {
					childTreeNode.setAllowsChildren(true);
				}
			} else {
				childTreeNode = new DefaultMutableTreeNode(new NodeUserData(entry.getKey()));
				childTreeNode.setAllowsChildren(true);
				createTreeNode(childTreeNode, (HashMap<String, Object>) value);
			}
			treeNode.add(childTreeNode);
		}
	}

}
