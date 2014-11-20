package pascocai.java.web;

import javax.swing.SwingUtilities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Rectangle;
import java.io.File;

public class UrlScanGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;
	private int windowWidth = 640;
	private int windowHeight = 480;
	private Toolkit toolkit = getToolkit();
	private Dimension screenSize = toolkit.getScreenSize();
	static String importFile = "";
	JTextArea textarea = new JTextArea();

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				UrlScanGUI thisClass = new UrlScanGUI();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});
	}

	public UrlScanGUI() {
		super();
		initialize();
	}

	private void initialize() {		
		JButton btn1 = new JButton("Import File");

		JButton buttonRun = new JButton("Run");
		JButton close = new JButton("Close");
		
		JScrollPane scrollingResult = new JScrollPane(textarea);
		
		Font font = new Font("宋体", Font.PLAIN, 16);

		this.setTitle("URL Scan");
		this.setContentPane(getJContentPane());
		this.setSize(new Dimension(this.windowWidth, this.windowHeight));
		this.setLocation(screenSize.width / 2 - getWidth() / 2,
				screenSize.height / 2 - getHeight() / 2);

		JMenuBar menubar = new JMenuBar();

		JMenu file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);

		JMenuItem fileOpen = new JMenuItem("Open");
		fileOpen.setMnemonic(KeyEvent.VK_O);
		fileOpen.setToolTipText("Open file");
		fileOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JFileChooser fileopen = new JFileChooser(new File("."));
				FileFilter filter = new FileNameExtensionFilter("xls files", "xls");
				fileopen.addChoosableFileFilter(filter);
				int ret = fileopen.showOpenDialog(UrlScanGUI.this);
				if (ret == JFileChooser.APPROVE_OPTION) {
					File file = fileopen.getSelectedFile();
					importFile = file.getPath();
					textarea.insert("importFile:\n"+importFile+"\n", textarea.getText().length());
				}
			}
		});

		JMenuItem fileClose = new JMenuItem("Close");
		fileClose.setMnemonic(KeyEvent.VK_C);
		fileClose.setToolTipText("Exit application");
		fileClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});

		file.add(fileOpen);
		file.add(fileClose);
		menubar.add(file);
		setJMenuBar(menubar);
		
		btn1.setBounds(new Rectangle(30, 15, 120, 30));
		btn1.setToolTipText("import file");
		btn1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JFileChooser fileopen = new JFileChooser(new File("."));
				FileFilter filter = new FileNameExtensionFilter("xls files", "xls");
				fileopen.addChoosableFileFilter(filter);
				int ret = fileopen.showOpenDialog(UrlScanGUI.this);
				if (ret == JFileChooser.APPROVE_OPTION) {
					File file = fileopen.getSelectedFile();
					importFile = file.getPath();
					textarea.insert("import file:\n"+importFile+"\n", textarea.getText().length());
				}
			}
		});
		
		buttonRun.setBounds(new Rectangle(230, 15, 80, 30));
		buttonRun.setToolTipText("Run");
		buttonRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if(!importFile.equals("")) {
					boolean nextStep = true;
					UrlScan us = new UrlScan();
					
					textarea.insert("Checking config file, please wait...\n", textarea.getText().length());
					jContentPane.paintImmediately(jContentPane.getBounds());
					if(us.checkConfig()){
						textarea.insert("Config file ok.\n", textarea.getText().length());
						jContentPane.paintImmediately(jContentPane.getBounds());
					} else {
						textarea.insert("Config file error.\n", textarea.getText().length());
						jContentPane.paintImmediately(jContentPane.getBounds());
						nextStep = false;
					}
					if(!nextStep)
						return;
					
					textarea.insert("Checking import file, please wait...\n", textarea.getText().length());
					jContentPane.paintImmediately(jContentPane.getBounds());
					if(us.checkImportFile(importFile)){
						textarea.insert("Import file ok.\n", textarea.getText().length());
						jContentPane.paintImmediately(jContentPane.getBounds());
					} else {
						textarea.insert("Import file error.\n", textarea.getText().length());
						jContentPane.paintImmediately(jContentPane.getBounds());
						nextStep = false;
					}
					if(!nextStep)
						return;
					
					textarea.insert("Scan running, please wait...\n", textarea.getText().length());
					jContentPane.paintImmediately(jContentPane.getBounds());
					if(us.runScan()){
						textarea.insert("Scan Complete.\n", textarea.getText().length());
						jContentPane.paintImmediately(jContentPane.getBounds());
					} else {
						textarea.insert("Scan fail.\n", textarea.getText().length());
						jContentPane.paintImmediately(jContentPane.getBounds());
					}
					
					us = null;
				} else if(importFile.equals("")) {
					textarea.insert("Please select import file.\n", textarea.getText().length());
					jContentPane.paintImmediately(jContentPane.getBounds());
				}
			}
		});

		close.setBounds(new Rectangle(525, 15, 80, 30));
		close.setToolTipText("Close");
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});
		
		textarea.setBounds(30, 65, 575, 335);
		textarea.setEnabled(false);
		textarea.setDisabledTextColor(Color.BLACK);
		textarea.setFont(font);
		
		scrollingResult.setBounds(30, 65, 575, 335);
		jContentPane.add(btn1);

		jContentPane.add(buttonRun);
		jContentPane.add(close);
		
		jContentPane.add(scrollingResult);
	}

	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
		}
		return jContentPane;
	}
}
