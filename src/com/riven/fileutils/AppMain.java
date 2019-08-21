package com.riven.fileutils;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.riven.fileutils.panels.CheckDuplicationFilesPanel;
import com.riven.fileutils.panels.ImageFileConsolidationPanel;

public class AppMain extends JFrame {
	private static final long serialVersionUID = 1L;

	public AppMain() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		new Config();
		setTitle("文件自动整理");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setDefaultLookAndFeelDecorated(true);
		setMinimumSize(new Dimension(800, 600));
		setLocationRelativeTo(null);

		JTabbedPane tab = new JTabbedPane();
		tab.add("影像文件整理", new ImageFileConsolidationPanel(this));
		tab.add("处理重复文件", new CheckDuplicationFilesPanel(this));
		add(tab); 
		pack();
		setVisible(true);
	}
 
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new AppMain();
			}
		});
	}

}
