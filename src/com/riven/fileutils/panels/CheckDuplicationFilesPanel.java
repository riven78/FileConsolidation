package com.riven.fileutils.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultTreeModel;

import com.riven.fileutils.AppMain;
import com.riven.fileutils.AutoRemoveDuplicateFiles;
import com.riven.fileutils.CheckDuplicateFiles;
import com.riven.fileutils.DigestUtils.FileEx;
import com.riven.fileutils.ProcessMsg;

public class CheckDuplicationFilesPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private AppMain mainFrame;
	private JPanel buttonPanel, chooseFolderPanel, logsPanel, resultTablePanel;
	private JTabbedPane resultTab;
	private JButton browseButton, checkDuplicationFilesButton, removeDuplicateFilesButton;
	private JTextField folderPath;
	private JTextArea logs;
	private JTable resultTable;
	private JScrollPane tableScrollPane;
	private JProgressBar progressBar;

	public CheckDuplicationFilesPanel(AppMain mainFrame) {
		this.mainFrame = mainFrame;
		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		chooseFolderPanel = new JPanel();
		add(chooseFolderPanel, gbc);

		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 50;
		gbc.gridx = 0;
		gbc.gridy = 1;
		resultTab = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		logsPanel = new JPanel();
		resultTab.add("运行日志", logsPanel);
		resultTablePanel = new JPanel();
		add(resultTab, gbc);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridx = 0;
		gbc.gridy = 2;
		buttonPanel = new JPanel();
		add(buttonPanel, gbc);

		chooseFolderPanel();
		logsPanel();
		resultTablePanel(null);
		buttonPanel();

//		resultTab.addChangeListener(new ChangeListener() {
//			@Override
//			public void stateChanged(ChangeEvent e) {
//				JTabbedPane tab = (JTabbedPane) e.getSource();
//				switch (tab.getSelectedIndex()) {
//				case 0:
//					checkDuplicationFilesButton.setVisible(true);
//					removeDuplicateFilesButton.setVisible(false);
//					break;
//				case 1:
//					checkDuplicationFilesButton.setVisible(true);
//					removeDuplicateFilesButton.setVisible(true);
//					break;
//				default:
//					break;
//				}
//			}
//		});
		resultTab.setSelectedIndex(0);
		removeDuplicateFilesButton.setVisible(false);
	}

	private void logsPanel() {
		logsPanel.setLayout(new BorderLayout(5, 5));
		logsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		JScrollPane scrollPane = new JScrollPane();
		logs = new JTextArea();
		logs.setLineWrap(true);
		scrollPane.setViewportView(logs);
		logsPanel.add(scrollPane, BorderLayout.CENTER);
	}

	private void chooseFolderPanel() {
		chooseFolderPanel.setLayout(new BorderLayout(5, 5));
		chooseFolderPanel.add(new JLabel("待整理的目录:"), BorderLayout.WEST);
		folderPath = new JTextField();
		chooseFolderPanel.add(folderPath, BorderLayout.CENTER);
		browseButton = new JButton("浏览...");
		final JFileChooser fileDialog = new JFileChooser();
		fileDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		browseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = fileDialog.showOpenDialog(mainFrame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileDialog.getSelectedFile();
					folderPath.setText(file.getAbsolutePath());
				}
			}
		});
		chooseFolderPanel.add(browseButton, BorderLayout.EAST);
	}

	private void buttonPanel() {
		buttonPanel.setLayout(new BorderLayout());
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		initCheckDuplicationFiles();
		panel.add(checkDuplicationFilesButton);
		initRemoveDuplicateFiles();
		panel.add(removeDuplicateFilesButton);
		buttonPanel.add(panel, BorderLayout.EAST);
	}

	private AutoRemoveDuplicateFiles autoRemoveDuplicateFiles = null;

	private void initRemoveDuplicateFiles() {
		removeDuplicateFilesButton = new JButton("自动按时间删除重复文件");
		removeDuplicateFilesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (autoRemoveDuplicateFiles == null) {
					if (resultTable != null && resultTable.getRowCount() > 0) {
						String saveFolder = folderPath.getText();
						if (saveFolder != null && saveFolder.length() > 0) {
							File saveFile = new File(saveFolder);
							if (saveFile.exists()) {
								progressBar.setValue(0);
								resultTab.setSelectedIndex(1);
								removeDuplicateFilesButton.setText("停止删除");
								checkDuplicationFilesButton.setVisible(false);
								resultTab.remove(0);
								autoRemoveDuplicateFiles = new AutoRemoveDuplicateFiles(resultTable, saveFile) {
									@Override
									public void drawTree(DefaultTreeModel model) {
										SwingUtilities.invokeLater(new Runnable() {
											public void run() {
												resultTable.repaint();
												resultTable.setEnabled(true);
											}
										});
									}

									protected void process(List<ProcessMsg> chunks) {
										if (chunks != null && chunks.size() > 0) {
											for (ProcessMsg msg : chunks) {
												if (msg.count > 0) {
													progressBar.setValue((msg.index + 1) * 100 / msg.count);
												}
											}
										}
									};

									protected void done() {
										try {
											Boolean ret = get();
											if (ret) {
												progressBar.setValue(100);
												JOptionPane.showMessageDialog(mainFrame, "删除重复文件完成");
											} else {
												JOptionPane.showMessageDialog(mainFrame, "删除重复文件失败");
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
										autoRemoveDuplicateFiles = null;
										removeDuplicateFilesButton.setText("自动按时间删除重复文件");
										resultTab.insertTab("运行日志", null, logsPanel, null, 0);
										resultTab.setSelectedIndex(1);
										checkDuplicationFilesButton.setVisible(true);
									}
								};
								autoRemoveDuplicateFiles.execute();
							}
						}
					}
				} else {
					autoRemoveDuplicateFiles.stop();
				}
			}
		});
		removeDuplicateFilesButton.setVisible(false);
	}

	private CheckDuplicateFiles checkDuplicateFiles = null;

	private void initCheckDuplicationFiles() {
		checkDuplicationFilesButton = new JButton("检查文件重复");
		checkDuplicationFilesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String executeFolder = folderPath.getText();
				if (executeFolder != null && executeFolder.length() > 0) {
					File executeFile = new File(executeFolder);
					if (executeFile.exists()) {
						if (checkDuplicateFiles == null) {
							logs.setText("");
							resultTab.setSelectedIndex(0);
							if (resultTab.getTabCount() == 2) {
								resultTab.remove(1);
							}
							checkDuplicationFilesButton.setText("停止检查");
							removeDuplicateFilesButton.setVisible(false);
							checkDuplicateFiles = new CheckDuplicateFiles(executeFile) {
								@Override
								public void drawTabel(final ArrayList<ArrayList<FileEx>> duplicateFilesList) {
									SwingUtilities.invokeLater(new Runnable() {
										public void run() {
											if (duplicateFilesList.size() > 0) {
												resultTablePanel(duplicateFilesList);
												resultTab.add("检查结果列表", resultTablePanel);
												resultTab.setSelectedIndex(1);
												removeDuplicateFilesButton.setVisible(true);
											}
										}
									});
								};

								protected void process(List<String> chunks) {
									if (chunks != null && chunks.size() > 0) {
										for (String msg : chunks) {
											logs.append(msg);
										}
									}
								};

								protected void done() {
									try {
										Boolean ret = get();
										if (ret) {
											logs.append("\nCompleted");
											JOptionPane.showMessageDialog(mainFrame, "检查文件重复完成");
										} else {
											logs.append("\nERROR!");
											JOptionPane.showMessageDialog(mainFrame, "检查文件重复失败");
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
									checkDuplicateFiles = null;
									checkDuplicationFilesButton.setText("检查文件重复");
								}
							};
							checkDuplicateFiles.execute();
						} else {
							checkDuplicateFiles.stop();
						}
					}
				}
			}
		});
	}

	@SuppressWarnings("serial")
	private void resultTablePanel(ArrayList<ArrayList<FileEx>> duplicateFilesList) {
		if (resultTable != null && tableScrollPane != null) {
			tableScrollPane.remove(resultTable);
			resultTable = null;
		}
		if (duplicateFilesList != null) {
			resultTable = new JTable(new DuplicationFilesTableModel(duplicateFilesList)) {
				@Override
				public void tableChanged(TableModelEvent e) {
					super.tableChanged(e);
					repaint();
				}
			};
			DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
						int row, int column) {
					Component ret = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					if (column == 0) {
						if (ret == null || !(ret instanceof JCheckBox)) {
							ret = new JCheckBox();
						}
						((JCheckBox) ret).setSelected((Boolean) value);
					}
					int group = Integer.valueOf((String) table.getValueAt(row, 1));
					if (group >= 0) {
						if (group % 2 == 0) {
							setBackground(new Color(0xd2d0d0));
						} else {
							setBackground(Color.WHITE);
						}
					} else {
						setBackground(Color.RED);
					}
					return ret;
				}
			};
			int columnCount = resultTable.getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				TableColumn tc = resultTable.getColumn(resultTable.getColumnName(i));
				tc.setCellRenderer(tcr);
				switch (i) {
				case 0:
					tc.setPreferredWidth(30);
					tc.setCellEditor(new DefaultCellEditor(new JCheckBox()));
					break;
				case 1:
					tc.setPreferredWidth(30);
					break;
				case 2:
					tc.setPreferredWidth(200);
					break;
				case 3:
					tc.setPreferredWidth(400);
					break;
				case 4:
					tc.setPreferredWidth(120);
					break;
				case 5:
					tc.setPreferredWidth(80);
					break;
				default:
					break;
				}
			}
			resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			resultTable.setCellSelectionEnabled(false);
		}
		if (tableScrollPane == null) {
			resultTablePanel.setLayout(new BorderLayout());
			resultTablePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			tableScrollPane = new JScrollPane();
			resultTablePanel.add(tableScrollPane, BorderLayout.CENTER);
			progressBar = new JProgressBar(0, 100);
			progressBar.setValue(0);
			progressBar.setStringPainted(true);
			resultTablePanel.add(progressBar, BorderLayout.SOUTH);
		}
		if (resultTable != null) {
			tableScrollPane.setViewportView(resultTable);
		}
	}

}
