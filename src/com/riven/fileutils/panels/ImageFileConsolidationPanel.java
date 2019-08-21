package com.riven.fileutils.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.riven.fileutils.AppMain;
import com.riven.fileutils.MakeImageFile;
import com.riven.fileutils.ProcessMsg;
import com.riven.fileutils.UnMakeImageFile;

public class ImageFileConsolidationPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private AppMain mainFrame;
	private JTabbedPane resultTab;
	private JPanel chooseFolderPanel, saveFolderPanel, buttonPanel, logsPanel, treePanel;
	private JButton browseButton1, browseButton2, doImageFileConsolidationButton, unMakeImageFilesButton;
	private JTextField folderPath1, folderPath2;
	private JTextArea logs;
	private JTree tree;
	private JScrollPane treeScrollPane;
	private JProgressBar progressBar;

	public ImageFileConsolidationPanel(AppMain mainFrame) {
		this.mainFrame = mainFrame;
		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		chooseFolderPanel = new JPanel();//待整理的目录
		add(chooseFolderPanel, gbc);

		gbc.gridy = 1;
		saveFolderPanel = new JPanel(); //存放的目
		add(saveFolderPanel, gbc);

		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 50;
		gbc.gridx = 0;
		gbc.gridy = 2;
		resultTab = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		logsPanel = new JPanel();

		resultTab.add("运行日志", logsPanel);
		treePanel = new JPanel();
		resultTab.add("整理恢复树", treePanel);
		add(resultTab, gbc);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridx = 0;
		gbc.gridy = 3;
		buttonPanel = new JPanel();
		add(buttonPanel, gbc);
	  //待整理的目录
		chooseFolderPanel();
		//存放的目
		saveFolderPanel();
		logsPanel();
		buttonPanel();
		treePanel(null);
	}

	private void chooseFolderPanel() {
		chooseFolderPanel.setLayout(new BorderLayout(5, 5));
		chooseFolderPanel.add(new JLabel("待整理的目录:"), BorderLayout.WEST);

		folderPath1 = new JTextField();
		chooseFolderPanel.add(folderPath1, BorderLayout.CENTER);
		browseButton1 = new JButton("浏览...");
		final JFileChooser fileDialog = new JFileChooser();
		fileDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		browseButton1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = fileDialog.showOpenDialog(mainFrame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileDialog.getSelectedFile();
					folderPath1.setText(file.getAbsolutePath());
				}
			}
		});
		chooseFolderPanel.add(browseButton1, BorderLayout.EAST);
	}

	private void saveFolderPanel() {
		saveFolderPanel.setLayout(new BorderLayout(5, 5));
		saveFolderPanel.add(new JLabel("存放的目录:  "), BorderLayout.WEST);
		folderPath2 = new JTextField();
		saveFolderPanel.add(folderPath2, BorderLayout.CENTER);
		browseButton2 = new JButton("浏览...");
		final JFileChooser fileDialog = new JFileChooser();
		fileDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		browseButton2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = fileDialog.showOpenDialog(mainFrame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileDialog.getSelectedFile();
					folderPath2.setText(file.getAbsolutePath());
				}
			}
		});
		saveFolderPanel.add(browseButton2, BorderLayout.EAST);
	}

	private void logsPanel() {
		logsPanel.setLayout(new BorderLayout(5, 5));
		logsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		JScrollPane scrollPane = new JScrollPane();
		logs = new JTextArea();
		logs.setLineWrap(true);
		scrollPane.setViewportView(logs);
		logsPanel.add(scrollPane, BorderLayout.CENTER);

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		logsPanel.add(progressBar, BorderLayout.SOUTH);
	}

	private void buttonPanel() {
		buttonPanel.setLayout(new BorderLayout());
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		initDoImageFileConsolidation();
		panel.add(doImageFileConsolidationButton);
		initUnMakeImageFiles();
		panel.add(unMakeImageFilesButton);
		buttonPanel.add(panel, BorderLayout.EAST);
	}

	MakeImageFile makeImageFile = null;

	private void initDoImageFileConsolidation() {
		doImageFileConsolidationButton = new JButton("影像文件整理");
		doImageFileConsolidationButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (makeImageFile == null) {
					String executeFolder = folderPath1.getText();
					String saveFolder = folderPath2.getText();
					if (executeFolder != null && executeFolder.length() > 0 && saveFolder != null && saveFolder.length() > 0) {
						File executeFile = new File(executeFolder);
						File saveFile = new File(saveFolder);
						if (executeFile.exists()) {
							if (!saveFile.exists()) {
								saveFile.mkdirs();
							}
							logs.setText("");
							progressBar.setValue(0);
							resultTab.setSelectedIndex(0);
							if (resultTab.getTabCount() == 2) {
								resultTab.remove(1);
							}
							doImageFileConsolidationButton.setText("停止整理");
							unMakeImageFilesButton.setVisible(false);
							makeImageFile = new MakeImageFile(executeFile, saveFile) {
								protected void process(List<ProcessMsg> chunks) {
									if (chunks != null && chunks.size() > 0) {
										for (ProcessMsg msg : chunks) {
											if (msg.count > 0) {
												progressBar.setValue((msg.index + 1) * 100 / msg.count);
											}
											logs.append(msg.msg + "\n");
										}
									}
								};

								protected void done() {
									try {
										Boolean ret = get();
										if (ret) {
											progressBar.setValue(100);
											logs.append("Completed");
											JOptionPane.showMessageDialog(mainFrame, "影像文件整理完成");
										} else {
											logs.append("ERROR!");
											JOptionPane.showMessageDialog(mainFrame, "影像文件整理失败");
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
									makeImageFile = null;
									resultTab.add("整理恢复树", treePanel);
									unMakeImageFilesButton.setVisible(true);
									doImageFileConsolidationButton.setText("影像文件整理");
								};
							};
							makeImageFile.execute();
						}
					}
				} else {
					makeImageFile.stop();
				}
			}
		});
	}

	UnMakeImageFile unMakeImageFile = null;

	private void initUnMakeImageFiles() {
		unMakeImageFilesButton = new JButton("影像文件整理恢复");
		final JFileChooser fileDialog = new JFileChooser();
		fileDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileDialog.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				boolean ret = false;
				if (f != null && f.exists()
						&& ((f.isFile() && f.getName().toLowerCase().endsWith(".csv")) || f.isDirectory())) {
					ret = true;
				}
				return ret;
			}

			@Override
			public String getDescription() {
				return ".csv Files";
			}

		});
		unMakeImageFilesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (unMakeImageFile == null) {
					int returnVal = fileDialog.showOpenDialog(mainFrame);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fileDialog.getSelectedFile();
						resultTab.setSelectedIndex(1);
						doImageFileConsolidationButton.setVisible(false);
						unMakeImageFilesButton.setText("停止恢复");
						resultTab.remove(0);
						unMakeImageFile = new UnMakeImageFile(file) {
							@Override
							public void drawTree(DefaultTreeModel model) {
								SwingUtilities.invokeLater(new Runnable() {
									public void run() {
										treePanel(model);
									}
								});
							};

							protected void process(List<DefaultMutableTreeNode> chunks) {
								if (chunks != null && chunks.size() > 0 && tree != null) {
									for (DefaultMutableTreeNode treeNode : chunks) {
										tree.setSelectionPath(new TreePath(treeNode.getPath()));
										int y = tree.getRowBounds(tree.getLeadSelectionRow()).y;
										int height = treeScrollPane.getHeight();
										JScrollBar scrollBar = treeScrollPane.getVerticalScrollBar();
										int value = y - (height / 2);
										if (value > scrollBar.getMaximum()) {
											value = scrollBar.getMaximum();
										} else if (value < scrollBar.getMinimum()) {
											value = scrollBar.getMinimum();
										}
										scrollBar.setValue(value);
									}
								}
								if (tree != null) {
									tree.updateUI();
								}
							};

							protected void done() {
								try {
									Boolean ret = get();
									if (ret) {
										JOptionPane.showMessageDialog(mainFrame, "影像文件整理恢复完成");
									} else {
										JOptionPane.showMessageDialog(mainFrame, "影像文件整理恢复失败");
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								unMakeImageFile = null;
								doImageFileConsolidationButton.setVisible(true);
								unMakeImageFilesButton.setText("影像文件整理恢复");
								resultTab.insertTab("运行日志", null, logsPanel, null, 0);
							}
						};
						unMakeImageFile.execute();
					}
				} else {
					unMakeImageFile.stop();
				}
			}
		});
	}

	private void treePanel(DefaultTreeModel model) {
		if (tree != null && treeScrollPane != null) {
			treeScrollPane.remove(tree);
			tree = null;
		}
		if (model != null) {
			tree = new JTree();
			tree.setModel(model);
		}
		if (treeScrollPane == null) {
			treePanel.setLayout(new BorderLayout());
			treePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			treeScrollPane = new JScrollPane();
			treePanel.add(treeScrollPane, BorderLayout.CENTER);
		}
		if (tree != null) {
			treeScrollPane.setViewportView(tree);
		}
	}

}
