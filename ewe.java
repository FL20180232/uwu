//=================================================================================================
//
// package: vatron.moldexpert.breakoutexpert.analyzer
// class:   JPanelVariation
//
// ----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 by VOEST-ALPINE Industrieanlagenbau GmbH & Co, A-4031 Linz
//
// ALL RIGHTS RESERVED.
// Consult your license regarding permissions and restrictions.
//
// Permission to use, copy, modify, distribute and sell this software and its
// documentation is under the terms of the license agreement.
//
// ----------------------------------------------------------------------------------
// <description>
// created on 2002
// 
// ------------------------------------------------------------------------------------------------
// @author VAI/ATI3, vatron
//
// $Workfile: JPanelVariation.java $   $Revision: 1.5 $
// $Author: Christian Ortner (ESC) (ortchri) $   $Modtime: 2.11.05 12:03 $
//
// $Archive: /CL2/DEV/vai/moldexpert/breakoutexpert/analyzer/JPanelVariation.java $
// $Date: 2008/07/03 13:07:08CEST $
//
//=================================================================================================

package vatron.moldexpert.breakoutexpert.analyzer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import vatron.moldexpert.breakoutexpert.ltv.common.Constants;

import vatron.utils.logger.MessageLogger;

public class JPanelVariation extends JPanel {
	private static final long serialVersionUID = 1L;

	private DefaultMutableTreeNode rootNode;
	private DefaultTreeModel parameterTreeModel;
	private Properties baiProperties = null;
	private File reportDirectory = null;
	private JFileChooser fcReport = new JFileChooser();
	private File realEventFile = null;
	private JFileChooser fcRealEvent = new JFileChooser();
	private JFileChooser fcImport = new JFileChooser();

	private BorderLayout borderLayoutVariation = new BorderLayout();
	// Toolbar Top
	private JToolBar jToolBarVariationTop = new JToolBar();
	private JTextField jTextFieldActualParameter = new JTextField();
	private JButton jButtonVariationOn = new JButton();
	private JButton jButtonVariationOff = new JButton();
	private JButton jButtonVariationDelete = new JButton();
	private JButton jButtonVariationRemove = new JButton();
	private JButton jButtonVariationRename = new JButton();
	private JButton jButtonVariationReset = new JButton();
	private JButton jButtonVariationOk = new JButton();
	private JButton jButtonVariationImport = new JButton();
	private JButton jButtonVariationNew = new JButton();
	private JButton jButtonVariationExport = new JButton();
	private JButton jButtonVariationShowDiff = new JButton();
	// Center
	private JScrollPane jScrollPaneTreeParameter = new JScrollPane();
	private JTree jTreeParameter = new JTree();

	// Toolbar Bottom
	private JPanel jPanelVariationBottom = new JPanel();
	private GridLayout gridLayoutVariationBottom = new GridLayout();
	// Toolbar for Report
	private JToolBar jToolBarVariationBottom1 = new JToolBar();
	private JLabel jLabelReport = new JLabel();
	private JTextField jTextFieldReport = new JTextField();
	private JButton jButtonReport = new JButton();
	// Toolbar for realalarm file
	private JToolBar jToolBarVariationBottom2 = new JToolBar();
	private JLabel jLabelRealEvent = new JLabel();
	private JTextField jTextFieldRealEvent = new JTextField();
	private JButton jButtonRealEvent = new JButton();
	private JToolBar jToolBarVariationBottom3 = new JToolBar();
	private JLabel jLabelCropAlarm = new JLabel();
	private JTextField jTextFieldCropAlarm = new JTextField();
	private JLabel jLabelGroupFalseAlarm = new JLabel();
	private JTextField jTextFieldGroupFalseAlarm = new JTextField();
	private JLabel jLabelSupressAlarm = new JLabel();
	private JTextField jTextFieldSupressAlarm = new JTextField();
	private JLabel jLabelSupressFAlarm = new JLabel();
	private JTextField jTextFieldSupressFAlarm = new JTextField();
	private boolean rename = false;

	private Analyzer c_analyzer;
	private List<Variation> variations;

	public List<Variation> getVariations() {
		return variations;
	}

	public void setVariations(List<Variation> variations) {
		this.variations = variations;
	}

	private DefaultMutableTreeNode defaultBOPVariationRootNode;
	private DefaultMutableTreeNode default1BOPVariationRootNode;

	public JPanelVariation(Analyzer analyzer) {

		c_analyzer = analyzer;
		try {
			preInit();
			jbInit();
			postInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return configuration file for BOPalgorithm (kernel)
	 * @param variation name of variation
	 */
	public String createBOPiniFile(String variation) {
		Enumeration<TreeNode> eVar = rootNode.children();
		while (eVar.hasMoreElements()) {
			DefaultMutableTreeNode varNode = (DefaultMutableTreeNode) eVar.nextElement();
			if (Variation.class == varNode.getUserObject().getClass()) {
				Variation varName = (Variation) varNode.getUserObject();
				if (0 == variation.compareTo(varName.getVariationName())) {
					String iniFile = new String("#BOP kernel ini file\n#Automatically generated by Analyzer\n");
					Enumeration<TreeNode> eSec = varNode.children();
					while (eSec.hasMoreElements()) {
						DefaultMutableTreeNode secNode = (DefaultMutableTreeNode) eSec.nextElement();
						VariationSection sec = (VariationSection) secNode.getUserObject();
						iniFile += "\n[" + sec.getSection() + "]\n";
						Enumeration<TreeNode> eKey = secNode.children();
						while (eKey.hasMoreElements()) {
							VariationKey key = (VariationKey) ((DefaultMutableTreeNode) eKey.nextElement())
									.getUserObject();
							iniFile += key.getKey() + "=" + key.getValue() + "\n";
						}
					}
					return iniFile;
				}
			}
		}
		return null;
	}

	private void preInit() throws Exception {
		rootNode = new DefaultMutableTreeNode("Variation");
		parameterTreeModel = new DefaultTreeModel(rootNode);
		jTreeParameter = new JTree(parameterTreeModel);
		variations = new ArrayList<Variation>();
	}

	private void postInit() throws Exception {

		jButtonVariationOn.setIcon(new ImageIcon(ConstantsAnalyzer.getIconsFilePath(ConstantsAnalyzer.FUNCTION_ON)));
		jButtonVariationOff.setIcon(new ImageIcon(ConstantsAnalyzer.getIconsFilePath(ConstantsAnalyzer.FUNCTION_OFF)));
		jButtonVariationOk.setIcon(new ImageIcon(ConstantsAnalyzer.getIconsFilePath(ConstantsAnalyzer.FUNCTION_OK)));

		jButtonVariationNew
				.setIcon(new ImageIcon(ConstantsAnalyzer.getIconsFilePath(ConstantsAnalyzer.FUNCTION_VARIATION_CLONE)));
		jButtonVariationExport
				.setIcon(new ImageIcon(ConstantsAnalyzer.getIconsFilePath(ConstantsAnalyzer.FUNCTION_EXPORT)));

		jButtonVariationDelete
				.setIcon(new ImageIcon(ConstantsAnalyzer.getIconsFilePath(ConstantsAnalyzer.FUNCTION_DELETE)));
		jButtonVariationRemove.setIcon(
				new ImageIcon(ConstantsAnalyzer.getIconsFilePath(ConstantsAnalyzer.FUNCTION_REMOVE_VARIATION)));

		jButtonVariationRename.setIcon(
				new ImageIcon(ConstantsAnalyzer.getIconsFilePath(ConstantsAnalyzer.FUNCTION_RENAME_VARIATION)));

		jButtonVariationShowDiff
				.setIcon(new ImageIcon(ConstantsAnalyzer.getIconsFilePath(ConstantsAnalyzer.FUNCTION_DIFF_VARIATION)));

		jButtonVariationImport.setIcon(
				new ImageIcon(ConstantsAnalyzer.getIconsFilePath(ConstantsAnalyzer.FUNCTION_VARIATION_IMPORT)));
		jButtonReport.setIcon(new ImageIcon(ConstantsAnalyzer.getIconsFilePath(ConstantsAnalyzer.FUNCTION_REPORT)));
		jButtonRealEvent
				.setIcon(new ImageIcon(ConstantsAnalyzer.getIconsFilePath(ConstantsAnalyzer.FUNCTION_REALEVENT)));

		jTreeParameter.setRootVisible(false);
		jTreeParameter.setEditable(false);
		jTreeParameter.setShowsRootHandles(true);
		jTreeParameter.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				parameterTreeSelectionChanged(e);
			}
		});
		jTreeParameter.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
			}

			public void mouseEntered(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {
			}

			public void mousePressed(MouseEvent arg0) {
			}

			public void mouseReleased(MouseEvent arg0) {
			}
		});
		JPanelVariation_TreeCellRenderer renderer = new JPanelVariation_TreeCellRenderer();
		jTreeParameter.setCellRenderer(renderer);
		jTextFieldActualParameter.setText("");
		jTextFieldActualParameter.setEnabled(false);
		jTextFieldActualParameter.invalidate();
		jButtonVariationOn.setVisible(false);
		jButtonVariationOff.setVisible(false);
		jButtonVariationDelete.setVisible(false);
		jButtonVariationRemove.setVisible(false);
		jButtonVariationRename.setVisible(false);
		jButtonVariationShowDiff.setVisible(false);
		jButtonVariationReset.setVisible(false);
		jButtonVariationOk.setVisible(true);
		jButtonVariationNew.setVisible(false);
		jButtonVariationExport.setVisible(false);
		jButtonVariationImport.setVisible(false);
	}

	private void jbInit() throws Exception {
		Font saveFont = new Font("Arial", 0, 10);

		jButtonVariationDelete.setFont(saveFont);
		jButtonVariationDelete.setText("Del.");
		jButtonVariationDelete.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButtonVariationDelete_actionPerformed(e);
			}
		});
		jButtonVariationRemove.setFont(saveFont);
		jButtonVariationRemove.setText("Rem.");
		jButtonVariationRemove.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButtonVariationRemove_actionPerformed(e);
			}
		});

		jButtonVariationShowDiff.setFont(saveFont);
		jButtonVariationShowDiff.setText("Diff");
		jButtonVariationShowDiff.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButtonVariationShowDiff_actionPerformed(e);
			}
		});
		jButtonVariationRename.setFont(saveFont);
		jButtonVariationRename.setText("Ren.");
		jButtonVariationRename.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButtonVariationRename_actionPerformed(e);
			}
		});

		jButtonVariationOn.setFont(saveFont);
		jButtonVariationOn.setText("On");
		jButtonVariationOn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButtonVariationOn_actionPerformed(e);
			}
		});
		jButtonVariationOk.setFont(saveFont);
		jButtonVariationOk.setToolTipText("");
		jButtonVariationOk.setText("Ok");
		jButtonVariationOk.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButtonVariationOk_actionPerformed();
			}
		});
		jButtonVariationNew.setFont(saveFont);
		jButtonVariationNew.setToolTipText("");
		jButtonVariationNew.setText("Copy");
		jButtonVariationNew.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButtonVariationNew_actionPerformed(e);
			}
		});
		jButtonVariationExport.setFont(saveFont);
		jButtonVariationExport.setToolTipText("");
		jButtonVariationExport.setText("Exp.");
		jButtonVariationExport.addActionListener(new java.awt.event.ActionListener() {
			// ExportDialog export = new ExportDialog(jPanelVariationBottom);
			public void actionPerformed(ActionEvent e) {
				jButtonVariationExport_actionPerformed(e);
			}
		});

		jButtonVariationOff.setFont(saveFont);
		jButtonVariationOff.setText("Off");
		jButtonVariationOff.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButtonVariationOff_actionPerformed(e);
			}
		});
		jButtonVariationImport.setFont(saveFont);
		jButtonVariationImport.setText("Imp.");
		jButtonVariationImport.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButtonVariationImport_actionPerformed(e);
			}
		});

		setFont(saveFont);

		jTextFieldReport.setText("<dir>");
		jButtonVariationReset.setText("reset");
		jButtonVariationReset.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButtonVariationReset_actionPerformed(e);
			}
		});
		jButtonReport.setActionCommand("report");
		jButtonReport.setText("browse...");
		jButtonReport.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButtonReport_actionPerformed(e);
			}
		});
		jLabelReport.setText("Report directory: ");
		this.setLayout(borderLayoutVariation);
		jPanelVariationBottom.setLayout(gridLayoutVariationBottom);
		gridLayoutVariationBottom.setRows(3);
		jLabelRealEvent.setText("Real alarm file:");
		jTextFieldRealEvent.setText("<file>");
		jButtonRealEvent.setActionCommand("realalarm");
		jButtonRealEvent.setText("browse..");
		jButtonRealEvent.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButtonRealEvent_actionPerformed(e);
			}
		});
		jLabelCropAlarm.setText("Crop alarms before:");
		jTextFieldCropAlarm.setText("<time>");
		jTextFieldCropAlarm.setMinimumSize(new Dimension(30, jTextFieldCropAlarm.getY()));

		jLabelGroupFalseAlarm.setText("Group false alarm time:");
		jTextFieldGroupFalseAlarm.setText("<time>");
		jTextFieldGroupFalseAlarm.setMinimumSize(new Dimension(30, jTextFieldGroupFalseAlarm.getY()));

		jLabelSupressAlarm.setText("Supress time after detected sticker:");
		jTextFieldSupressAlarm.setText("<time>");
		jTextFieldSupressAlarm.setMinimumSize(new Dimension(30, jTextFieldSupressAlarm.getY()));

		jLabelSupressFAlarm.setText("Supress time false alarms:");
		jTextFieldSupressFAlarm.setText("<time>");
		jTextFieldSupressFAlarm.setMinimumSize(new Dimension(30, jTextFieldSupressFAlarm.getY()));

		jToolBarVariationBottom1.add(jLabelReport, null);
		jToolBarVariationBottom1.add(jTextFieldReport, null);
		jToolBarVariationBottom1.add(jButtonReport, null);
		jPanelVariationBottom.add(jToolBarVariationBottom1, null);
		jPanelVariationBottom.add(jToolBarVariationBottom2, null);
		jPanelVariationBottom.add(jToolBarVariationBottom3, null);
		this.add(jPanelVariationBottom, BorderLayout.SOUTH);
		this.add(jToolBarVariationTop, BorderLayout.NORTH);
		jTextFieldActualParameter.addKeyListener(new java.awt.event.KeyListener() {
			public void keyPressed(KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.VK_ENTER) {
					jButtonVariationOk_actionPerformed();
				}
			}

			public void keyReleased(KeyEvent event) {
			}

			public void keyTyped(KeyEvent event) {
			}
		});
		jToolBarVariationTop.add(jTextFieldActualParameter, null);
		jToolBarVariationTop.add(jButtonVariationOn, null);
		jToolBarVariationTop.add(jButtonVariationOff, null);
		jToolBarVariationTop.add(jButtonVariationDelete, null);
		jToolBarVariationTop.add(jButtonVariationRemove, null);
		jToolBarVariationTop.add(jButtonVariationRename, null);
		jToolBarVariationTop.add(jButtonVariationShowDiff, null);

		jToolBarVariationTop.add(jButtonVariationReset, null);
		jToolBarVariationTop.add(jButtonVariationOk, null);
		jToolBarVariationTop.add(jButtonVariationNew, null);
		jToolBarVariationTop.add(jButtonVariationExport, null);
		jToolBarVariationTop.add(jButtonVariationImport, null);

		this.add(jScrollPaneTreeParameter, BorderLayout.CENTER);
		jScrollPaneTreeParameter.getViewport().add(jTreeParameter, null);
		jToolBarVariationBottom2.add(jLabelRealEvent, null);
		jToolBarVariationBottom2.add(jTextFieldRealEvent, null);
		jToolBarVariationBottom2.add(jButtonRealEvent, null);
		jToolBarVariationBottom3.add(jLabelCropAlarm, null);
		jToolBarVariationBottom3.add(jTextFieldCropAlarm, null);
		jToolBarVariationBottom3.add(jLabelGroupFalseAlarm, null);
		jToolBarVariationBottom3.add(jTextFieldGroupFalseAlarm, null);
		jToolBarVariationBottom3.add(jLabelSupressAlarm, null);
		jToolBarVariationBottom3.add(jTextFieldSupressAlarm, null);
		jToolBarVariationBottom3.add(jLabelSupressFAlarm, null);
		jToolBarVariationBottom3.add(jTextFieldSupressFAlarm, null);

	}

	void jButtonVariationOn_actionPerformed(ActionEvent e) {
		jButtonVariationOnOff_actionPerformed(e, true);
	}

	void jButtonVariationOff_actionPerformed(ActionEvent e) {
		jButtonVariationOnOff_actionPerformed(e, false);
	}

	void jButtonVariationRemove_actionPerformed(ActionEvent e) {
		DefaultMutableTreeNode currentNode = getSelectedVariationTreeNode(jTreeParameter);
		if (null != currentNode) {
			MutableTreeNode parent = (MutableTreeNode) (currentNode.getParent());
			String nodeVariationPath = ((Variation) currentNode.getUserObject()).getVariationPath();
			if (parent != null) {
				parameterTreeModel.removeNodeFromParent(currentNode);
				boolean removed = false;
				for (int i = 0; i < variations.size() && !removed; i++) {
					String variationPath = variations.get(i).getVariationPath();
					if (variationPath.equals(nodeVariationPath)) {
						variations.remove(i);
						removed = true;
					}
				}
			}
		}
	}

	void jButtonVariationShowDiff_actionPerformed(ActionEvent e) {
		new DiffOfVariationsViewer(getSelectedVariations(jTreeParameter));
	}

	void jButtonVariationRename_actionPerformed(ActionEvent e) {
		jButtonVariationNew_actionPerformed(null);
		rename = true;
	}

	void jButtonVariationDelete_actionPerformed(ActionEvent e) {
		Object options[] = { "OK", "CANCEL" };
		int index = JOptionPane.showOptionDialog(null, // parent
				"Are you sure you want to delete this variation from the harddisk?", // message
				// object
				"Delete Variation", // string title
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, // Icon
				options, options[0]);

		if (index == 0) {
			deleteVariation();
		}
	}

	private void deleteVariation() {

		DefaultMutableTreeNode currentNode = getSelectedVariationTreeNode(jTreeParameter);
		if (null != currentNode) {
			MutableTreeNode parent = (MutableTreeNode) (currentNode.getParent());
			if (parent != null) {
				parameterTreeModel.removeNodeFromParent(currentNode);
				String nodeVariationPath = ((Variation) currentNode.getUserObject()).getVariationPath();

				boolean removed = false;
				for (int i = 0; i < variations.size() && !removed; i++) {
					String variationPath = variations.get(i).getVariationPath();
					if (variationPath.equals(nodeVariationPath)) {
						variations.remove(i);
						removed = true;
					}
				}
				deleteDirectory(new File(nodeVariationPath));
				return;
			}
		}

	}

	private boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	void jButtonVariationOk_actionPerformed() {
		DefaultMutableTreeNode currentNode = getSelectedVariationTreeNode(jTreeParameter);
		if (null != currentNode) {
			Object keyObj = currentNode.getUserObject();
			if (null != keyObj) {
				if (VariationKey.class == keyObj.getClass()) {
					VariationKey updatedVariationKey = ((VariationKey) keyObj);
					updatedVariationKey.setValue(jTextFieldActualParameter.getText());
					Properties previouseBOPKernelProperties = updatedVariationKey.getVariation()
							.getBopKernelProperties();
					String oldValue = null;
					if (previouseBOPKernelProperties != null) {
						oldValue = previouseBOPKernelProperties
								.getProperty(updatedVariationKey.getSection() + "." + updatedVariationKey.getKey());
					}
					if (oldValue != null) {
						previouseBOPKernelProperties.setProperty(
								updatedVariationKey.getSection() + "." + updatedVariationKey.getKey(),
								jTextFieldActualParameter.getText());
					}

					Properties previouseBOPPrecondtionProperties = updatedVariationKey.getVariation()
							.getBopPreconditionProperties();
					if (previouseBOPPrecondtionProperties != null) {
						oldValue = previouseBOPPrecondtionProperties
								.getProperty(updatedVariationKey.getSection() + "." + updatedVariationKey.getKey());
					}
					if (oldValue != null) {
						previouseBOPPrecondtionProperties.setProperty(
								updatedVariationKey.getSection() + "." + updatedVariationKey.getKey(),
								jTextFieldActualParameter.getText());
					}

					Properties previouseFPDKernelProperties = updatedVariationKey.getVariation()
							.getFpdKernelProperties();
					if (previouseFPDKernelProperties != null) {
						oldValue = previouseFPDKernelProperties
								.getProperty(updatedVariationKey.getSection() + "." + updatedVariationKey.getKey());
					}
					if (oldValue != null) {
						previouseFPDKernelProperties.setProperty(
								updatedVariationKey.getSection() + "." + updatedVariationKey.getKey(),
								jTextFieldActualParameter.getText());
					}

					Properties previouseFPDPrecondtionProperties = updatedVariationKey.getVariation()
							.getFpdPreconditionProperties();
					if (previouseFPDPrecondtionProperties != null) {
						oldValue = previouseFPDPrecondtionProperties
								.getProperty(updatedVariationKey.getSection() + "." + updatedVariationKey.getKey());
					}
					if (oldValue != null) {
						previouseFPDPrecondtionProperties.setProperty(
								updatedVariationKey.getSection() + "." + updatedVariationKey.getKey(),
								jTextFieldActualParameter.getText());
					}

					Properties previouseHFKernelProperties = updatedVariationKey.getVariation().getHfKernelProperties();
					if (previouseHFKernelProperties != null) {
						oldValue = previouseHFKernelProperties
								.getProperty(updatedVariationKey.getSection() + "." + updatedVariationKey.getKey());
					}
					if (oldValue != null) {
						previouseHFKernelProperties.setProperty(
								updatedVariationKey.getSection() + "." + updatedVariationKey.getKey(),
								jTextFieldActualParameter.getText());
					}

					Properties previouseHFrecondtionProperties = updatedVariationKey.getVariation()
							.getHfPreconditionProperties();
					if (previouseHFrecondtionProperties != null) {
						oldValue = previouseHFrecondtionProperties
								.getProperty(updatedVariationKey.getSection() + "." + updatedVariationKey.getKey());
					}
					if (oldValue != null) {
						previouseHFrecondtionProperties.setProperty(
								updatedVariationKey.getSection() + "." + updatedVariationKey.getKey(),
								jTextFieldActualParameter.getText());
					}

					checkTreeSectionNames();
					jTreeParameter.invalidate();
					jTreeParameter.repaint();
				}
				if (Variation.class == keyObj.getClass()) {
					String variationName = jTextFieldActualParameter.getText();
					if (variationName.indexOf(",") > -1 || variationName.indexOf(" ") > -1
							|| variationName.indexOf(".") > -1 || variationName.indexOf("[") > -1
							|| variationName.indexOf("]") > -1 || variationName.indexOf("=") > -1
							|| variationName.indexOf("#") > -1) {
						JOptionPane.showMessageDialog(c_analyzer,
								"',' ' ' '.' ':' '=' '[' ']' and '#' is not allowed in variation name.", "Warning",
								JOptionPane.ERROR_MESSAGE);
					} else {

						if (null != baiProperties) {
							Variation sourceVariation = (Variation) keyObj;
							boolean isDefaultVariation1 = sourceVariation.isDefaultVariation1();
							boolean isChildOfDefaultVariation1 = sourceVariation.isChildOfDefaultVariation1();
							File sourceVariationDir = new File(((Variation) keyObj).getVariationPath());
							File destinationDirectory = new File(
									sourceVariationDir.getParentFile().getAbsolutePath() + "\\" + variationName
											+ ((isDefaultVariation1 || isChildOfDefaultVariation1) ? "_config1" : ""));
							String variationPath = destinationDirectory.getAbsolutePath() + "\\";
							try {
								copyDirectory(sourceVariationDir, destinationDirectory);
							} catch (IOException e) {
								e.printStackTrace();
							}
							Properties sourceProperties = ((Variation) currentNode.getUserObject())
									.getBopKernelProperties();
							Properties newBOPKernelProperties = createNewProperties(sourceProperties);

							sourceProperties = ((Variation) currentNode.getUserObject()).getBopPreconditionProperties();
							Properties newBOPPreconditionProperties = createNewProperties(sourceProperties);

							sourceProperties = ((Variation) currentNode.getUserObject()).getFpdKernelProperties();
							Properties newFPDKernelProperties = createNewProperties(sourceProperties);

							sourceProperties = ((Variation) currentNode.getUserObject()).getFpdPreconditionProperties();
							Properties newFPDPreconditionProperties = createNewProperties(sourceProperties);

							sourceProperties = ((Variation) currentNode.getUserObject()).getHfKernelProperties();
							Properties newHFKernelProperties = createNewProperties(sourceProperties);

							sourceProperties = ((Variation) currentNode.getUserObject()).getHfPreconditionProperties();
							Properties newHFPreconditionProperties = createNewProperties(sourceProperties);

							Variation variation = new Variation(variationPath, false, newBOPKernelProperties,
									newBOPPreconditionProperties, newFPDKernelProperties, newFPDPreconditionProperties,
									newHFKernelProperties, newHFPreconditionProperties);
							if (rename) {
								deleteVariation();
								rename = false;
							}

							rootNode.add(createVariationTree(baiProperties, variation, new ArrayList<String>()));
						}
						checkTreeSectionNames();
						parameterTreeModel.reload();
						jTreeParameter.invalidate();
						jTreeParameter.repaint();
					}
				}
			}
		}
	}

	private synchronized Properties createNewProperties(Properties sourceProperties) {
		Properties newProperties = null;
		Enumeration<?> e = null;
		try {
			e = sourceProperties.propertyNames();
			newProperties = new Properties();
			while ((e != null && e.hasMoreElements())) {
				String key = (String) e.nextElement();
				newProperties.put(key, sourceProperties.getProperty(key));

			}

		} catch (Exception e1) {
		}
		return newProperties;
	}

	// If targetLocation does not exist, it will be created.
	public void copyDirectory(File sourceLocation, File targetLocation) throws IOException {

		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}

			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
			}
		} else {

			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}

	void jButtonVariationNew_actionPerformed(ActionEvent e) {
		jButtonVariationOn.setVisible(false);
		jButtonVariationOff.setVisible(false);
		jButtonVariationOk.setVisible(true);
		jButtonVariationNew.setVisible(false);
		jButtonVariationExport.setVisible(false);
		jButtonVariationImport.setVisible(false);
		jButtonVariationDelete.setVisible(false);
		jButtonVariationRemove.setVisible(false);
		jButtonVariationRename.setVisible(false);
		jButtonVariationShowDiff.setVisible(false);
		jTextFieldActualParameter.setEnabled(true);
		DefaultMutableTreeNode currentNode = getSelectedVariationTreeNode(jTreeParameter);
		// A VariationNode have to be choosen for the Export!
		if (null != currentNode) {
			Object keyObj = currentNode.getUserObject();
			if (null != keyObj) {
				if (Variation.class == keyObj.getClass()) {
					Variation variation = (Variation) keyObj;
					jTextFieldActualParameter.setText(variation.getVariationName());
				}
			}
		}

	}

	public void jButtonVariationExport_actionPerformed(ActionEvent e) {
		ExportDialog export = new ExportDialog(this, c_analyzer);
	}

	
	protected String exportToInternals(File propertyfile) {

		String keyToReplace = null;
	    StringBuilder exportReport = new StringBuilder();
	    StringBuilder missingKeysString = new StringBuilder();
	    StringBuilder errorString = new StringBuilder();
	    missingKeysString.append("These Keys could not be found in the ini-File:").append("\n");
	    ArrayList<String> missingKeys = new ArrayList<String>();
	 
	    StringBuilder changedKeys = new StringBuilder();
	    changedKeys.append("These Keys were changed at the ini-File:").append("\n");
	    try {
	 
	      BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(propertyfile), "UTF8"));
	 
	      String line = reader.readLine();
	 
	      StringBuilder contents = new StringBuilder();
	      // Read the whole content of the ini-File
	      int i = 0;  
		 ArrayList<String> fileList = new ArrayList<String>(i);
	      while (null != line) {
	    	
	        fileList.add(line);
	        fileList.add(System.getProperty("line.separator"));
	        String[] strFileList = fileList.get(i).split(".");
	        System.out.println(fileList.get(i));
	        i++;
//	        if(line.indexOf("=") > 0) {
//	      while (null != line) {
//		  contents.append(line);
//		  //contents.append(System.getProperty("line.separator"));  
//	      String contentsString = contents.toString();
//	      String [] str = contentsString.split("=");
//	     
//	      }
	      }
	      DefaultMutableTreeNode currentNode = getSelectedVariationTreeNode(jTreeParameter);
	      // A VariationNode have to be choosen for the Export!
	      if (null != currentNode) {
	        Object keyObj = currentNode.getUserObject();
	        if (null != keyObj) {
	          if (Variation.class == keyObj.getClass()) {
	            Variation exportVariation = (Variation) keyObj;
	            Properties kernelProperties = exportVariation.getBopKernelProperties();
	            Properties preconditionProperties = exportVariation.getBopPreconditionProperties();
	            @SuppressWarnings("rawtypes") Enumeration kernelPropertiesEnum = kernelProperties.propertyNames();
	            @SuppressWarnings("rawtypes") Enumeration preconditionPropertiesEnum = preconditionProperties.propertyNames();
	            // Enumerate through all parameters of the kernel
	            while (kernelPropertiesEnum.hasMoreElements()) {
	            	
	            	
	            	
	            	
	            	
	              String sourceKey = kernelPropertiesEnum.nextElement().toString();
	              // varaiationPath does not exist in ini (internal usage in analyzer only
	           
	            	  
	            	  String value = kernelProperties.getProperty(sourceKey);
	            	  
	            	 // System.out.println(sourceKey + " " + value);
	            	  
	            	  
	            	  
	            	  
	            	  
	            
	            }
	            // Iterate through all parameters of the Preconditions
	            while (preconditionPropertiesEnum.hasMoreElements()) {
	              String sourceKey = preconditionPropertiesEnum.nextElement().toString();
	              // varaiationPath does not exist in ini (internal usage in analyzer only
	              String value = preconditionProperties.getProperty(sourceKey);
	              keyToReplace = sourceKey.replace('.', '_');
	              keyToReplace = "=$" + keyToReplace + "$";
	              int startIndex = contents.indexOf(keyToReplace);
	              // If the key was found in the iniFile replace it
	              if (startIndex > 0) {
	                startIndex = contents.indexOf(";", startIndex);
	                int endIndex = contents.indexOf("\r\n", startIndex);
	                if (endIndex == -1) {
	                  endIndex = contents.indexOf("\n", startIndex);
	                }
	                String valueInIniFile = contents.substring(startIndex + 1, endIndex);
	                contents.replace(startIndex + 1, endIndex, value);
	 
	                if (!value.trim().equals(valueInIniFile.trim())) {
	                  changedKeys.append(keyToReplace + " from: " + valueInIniFile + " to: " + value).append("\r\n");
	                }
	              }
	              else {
	                missingKeys.add(keyToReplace);
	              }
	            }}
	 
	            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(propertyfile),
	                Charset.forName("UTF-8")));
	 
	            output.write(contents.toString());
	            output.close();
	            removeBOM(propertyfile.getAbsolutePath());
	          }
	        }
	      
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	      errorString.append("Error at Exporting key: " + keyToReplace);
	    }
	 
	    String[] missingKeysArray = new String[missingKeys.size()];
	    missingKeys.toArray(missingKeysArray);
	    // sort keys
	    for (int i = 0; i < missingKeysArray.length - 1; i++) {
	      for (int j = i + 1; j < missingKeysArray.length; j++) {
	        if (0 > missingKeysArray[j].compareTo(missingKeysArray[i])) {
	          String temp = missingKeysArray[i];
	          missingKeysArray[i] = missingKeysArray[j];
	          missingKeysArray[j] = temp;
	        }
	      }
	    }
	    for (int i = 0; i < missingKeysArray.length; i++) {
	      missingKeysString.append(missingKeysArray[i]).append("\r\n");
	    }
	 
	    if (errorString != null && errorString.length() > 0) {
	      exportReport.append(errorString);
	    }
	 
	    exportReport.append(changedKeys).append("\n").append(missingKeysString);
	    return exportReport.toString();

	
	}
	
	
	protected String exportToIni(File iniFile) {
		String keyToReplace = null;
	    StringBuilder exportReport = new StringBuilder();
	    StringBuilder missingKeysString = new StringBuilder();
	    StringBuilder errorString = new StringBuilder();
	    missingKeysString.append("These Keys could not be found in the ini-File:").append("\n");
	    ArrayList<String> missingKeys = new ArrayList<String>();
	 
	    StringBuilder changedKeys = new StringBuilder();
	    changedKeys.append("These Keys were changed at the ini-File:").append("\n");
	    try {
	 
	      BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(iniFile), "UTF8"));
	 
	      String line = reader.readLine();
	 
	      StringBuilder contents = new StringBuilder();
	      // Read the whole content of the ini-File
	      while (null != line) {
	        contents.append(line);
	        contents.append(System.getProperty("line.separator"));
	        line = reader.readLine();
	 
//	        if(line.indexOf("=") > 0) {
//	      while (null != line) {
//		  contents.append(line);
//		  //contents.append(System.getProperty("line.separator"));  
//	      String contentsString = contents.toString();
//	      String [] str = contentsString.split("=");
//	     
//	      }
//	      }
	      DefaultMutableTreeNode currentNode = getSelectedVariationTreeNode(jTreeParameter);
	      // A VariationNode have to be choosen for the Export!
	      if (null != currentNode) {
	        Object keyObj = currentNode.getUserObject();
	        if (null != keyObj) {
	          if (Variation.class == keyObj.getClass()) {
	            Variation exportVariation = (Variation) keyObj;
	            Properties kernelProperties = exportVariation.getBopKernelProperties();
	            Properties preconditionProperties = exportVariation.getBopPreconditionProperties();
	            @SuppressWarnings("rawtypes") Enumeration kernelPropertiesEnum = kernelProperties.propertyNames();
	            @SuppressWarnings("rawtypes") Enumeration preconditionPropertiesEnum = preconditionProperties.propertyNames();
	            // Enumerate through all parameters of the kernel
	            while (kernelPropertiesEnum.hasMoreElements()) {
	            	
	            	
	            	
	            	
	            	
	              String sourceKey = kernelPropertiesEnum.nextElement().toString();
	              // varaiationPath does not exist in ini (internal usage in analyzer only
	              if (!sourceKey.equals("variationPath")) {
	                String value = kernelProperties.getProperty(sourceKey);
	 
	                // keys have different format
	                keyToReplace = sourceKey.replace('.', '_');
	                if (keyToReplace.startsWith("bop_")) {
	                  keyToReplace = keyToReplace.substring(4, keyToReplace.length());
	                }
	 
	                // Ausnahmebehandlungen. Main_Algorithm1Active heisst im ini BOP_algorithm1active
	                if (keyToReplace.startsWith("Main_")) {
	                  keyToReplace = keyToReplace.substring(5, keyToReplace.length());
	                  keyToReplace = "BOP_" + keyToReplace;
	                }
	                // Ausnahmebehandlungen. conditionDuration.alarmDuration heisst im ini nur alarmDuration
	                if (keyToReplace.startsWith("conditionDuration_")) {
	                  keyToReplace = keyToReplace.substring(18, keyToReplace.length());
	                }
	 
	                // look if it is a value of min max e.g.: dTmaxLimit=1.2, 3.0
	                int seperator = value.indexOf(',');
	 
	                // min and max are available--> can be a different proceeding Different proceeding
	                boolean keyAvaliable = (contents.indexOf("=$" + keyToReplace + "$")) > -1;
	 
	                if (seperator > 0 && !keyAvaliable) {
	                  String minValue = value.substring(0, seperator).trim();
	                  String maxValue = value.substring(seperator + 1, value.length()).trim();
	                  // --> 2 keys
	                  String newMinKey = "=$" + keyToReplace + "_min" + "$";
	                  String newMaxKey = "=$" + keyToReplace + "_max" + "$";
	                  int startIndex = contents.indexOf(newMinKey);
	                  int endIndex;
	                  // update min key
	                  if (startIndex > 0) {
	                    startIndex = contents.indexOf(";", startIndex);
	                    endIndex = contents.indexOf("\r\n", startIndex);
	                    if (endIndex == -1) {
	                      endIndex = contents.indexOf("\n", startIndex);
	                    }
	                    String valueInIniFile = contents.substring(startIndex + 1, endIndex);
	 
	                    contents.replace(startIndex + 1, endIndex, minValue);
	 
	                    if (!minValue.trim().equals(valueInIniFile.trim())) {
	                      valueInIniFile.replaceAll("[\r\n]", "");
	                      changedKeys.append(newMinKey + " from: " + valueInIniFile + " to: " + minValue).append("\r\n");
	                    }
	                  }
	                  // Key was not found in the File
	                  else {
	                    missingKeys.add(newMinKey);
	                  }
	 
	                  startIndex = contents.indexOf(newMaxKey);
	                  // update max key
	                  if (startIndex > 0) {
	                    startIndex = contents.indexOf(";", startIndex);
	                    endIndex = contents.indexOf("\r\n", startIndex);
	                    if (endIndex == -1) {
	                      endIndex = contents.indexOf("\n", startIndex);
	                    }
	                    String valueInIniFile = contents.substring(startIndex + 1, endIndex);
	                    contents.replace(startIndex + 1, endIndex, maxValue);
	 
	                    if (!maxValue.trim().equals(valueInIniFile.trim())) {
	                      valueInIniFile.replaceAll("[\r\n]", "");
	                      changedKeys.append(newMaxKey + " from: " + valueInIniFile + " to: " + maxValue).append("\r\n");
	                    }
	                  }
	                  // Key was not found in the File
	                  else {
	                    missingKeys.add(newMaxKey);
	                  }
	                }
	                else {
	                  keyToReplace = "=$" + keyToReplace + "$";
	                  int startIndex = contents.indexOf(keyToReplace);
	                  if (startIndex > 0) {
	                    startIndex = contents.indexOf(";", startIndex);
	                    int endIndex = contents.indexOf("\r\n", startIndex);
	                    if (endIndex == -1) {
	                      endIndex = contents.indexOf("\n", startIndex);
	                    }
	                    String valueInIniFile = contents.substring(startIndex + 1, endIndex);
	                    contents.replace(startIndex + 1, endIndex, value);
	 
	                    if (!value.trim().equals(valueInIniFile.trim())) {
	                      valueInIniFile.replaceAll("[\r\n]", "");
	                      changedKeys.append(keyToReplace + " from: " + valueInIniFile + " to: " + value).append("\r\n");
	                    }
	                  }
	                  // Key was not found in the File
	                  else {
	                    missingKeys.add(keyToReplace);
	                  }
	                }
	              }
	            }
	            // Iterate through all parameters of the Preconditions
	            while (preconditionPropertiesEnum.hasMoreElements()) {
	              String sourceKey = preconditionPropertiesEnum.nextElement().toString();
	              // varaiationPath does not exist in ini (internal usage in analyzer only
	              String value = preconditionProperties.getProperty(sourceKey);
	              keyToReplace = sourceKey.replace('.', '_');
	              keyToReplace = "=$" + keyToReplace + "$";
	              int startIndex = contents.indexOf(keyToReplace);
	              // If the key was found in the iniFile replace it
	              if (startIndex > 0) {
	                startIndex = contents.indexOf(";", startIndex);
	                int endIndex = contents.indexOf("\r\n", startIndex);
	                if (endIndex == -1) {
	                  endIndex = contents.indexOf("\n", startIndex);
	                }
	                String valueInIniFile = contents.substring(startIndex + 1, endIndex);
	                contents.replace(startIndex + 1, endIndex, value);
	 
	                if (!value.trim().equals(valueInIniFile.trim())) {
	                  changedKeys.append(keyToReplace + " from: " + valueInIniFile + " to: " + value).append("\r\n");
	                }
	              }
	              else {
	                missingKeys.add(keyToReplace);
	              }
	            }}
	 
	            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(iniFile),
	                Charset.forName("UTF-8")));
	 
	            output.write(contents.toString());
	            output.close();
	            removeBOM(iniFile.getAbsolutePath());
	          }
	        }
	      }
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	      errorString.append("Error at Exporting key: " + keyToReplace);
	    }
	 
	    String[] missingKeysArray = new String[missingKeys.size()];
	    missingKeys.toArray(missingKeysArray);
	    // sort keys
	    for (int i = 0; i < missingKeysArray.length - 1; i++) {
	      for (int j = i + 1; j < missingKeysArray.length; j++) {
	        if (0 > missingKeysArray[j].compareTo(missingKeysArray[i])) {
	          String temp = missingKeysArray[i];
	          missingKeysArray[i] = missingKeysArray[j];
	          missingKeysArray[j] = temp;
	        }
	      }
	    }
	    for (int i = 0; i < missingKeysArray.length; i++) {
	      missingKeysString.append(missingKeysArray[i]).append("\r\n");
	    }
	 
	    if (errorString != null && errorString.length() > 0) {
	      exportReport.append(errorString);
	    }
	 
	    exportReport.append(changedKeys).append("\n").append(missingKeysString);
	    return exportReport.toString();

	}

	public void removeBOM(String name) {
		try {
			File file = new File(name);
			// File length
			int size = (int) file.length();
			if (size > Integer.MAX_VALUE) {
				MessageLogger.error(Constants.logger, "File is to larger at removing BOM");
			}
			byte[] bytes = new byte[size];
			DataInputStream dis = new DataInputStream(new FileInputStream(file));
			int read = 0;
			int numRead = 0;
			while (read < bytes.length && (numRead = dis.read(bytes, read, bytes.length - read)) >= 0) {
				read = read + numRead;
			}
			MessageLogger.debug(Constants.logger, "File size: " + read);
			// Ensure all the bytes have been read in
			if (read < bytes.length) {
				MessageLogger.debug(Constants.logger, "Could not completely read: " + file.getName());
			}
			// sicking 12_2012 close InputStream
			dis.close();

			if (bytes[0] == -17 && bytes[1] == -69 && bytes[2] == -65) {
				byte[] newbytes = new byte[bytes.length - 3];
				newbytes = Arrays.copyOfRange(bytes, 3, bytes.length - 1);
				FileOutputStream fos = new FileOutputStream(name);
				fos.write(newbytes);
				fos.close();
			}

		} catch (Exception e) {
			e.getMessage();
		}
	}

	void jButtonVariationReset_actionPerformed(ActionEvent e) {
		DefaultMutableTreeNode currentNode = getSelectedVariationTreeNode(jTreeParameter);
		if (null != currentNode) {
			Object keyObj = currentNode.getUserObject();
			if (null != keyObj) {
				if (VariationKey.class == keyObj.getClass()) {
					((VariationKey) keyObj).setValue(((VariationKey) keyObj).getDefaultValue().getValue());
					checkTreeSectionNames();
					jTreeParameter.invalidate();
					jTreeParameter.repaint();
				}
			}
		}
	}

	void jButtonVariationImport_actionPerformed(ActionEvent e) {
		DefaultMutableTreeNode node = getSelectedVariationTreeNode(jTreeParameter);
		if (null != node) {
			importSection(node);
		}
		jTreeParameter.invalidate();
		jTreeParameter.repaint();
	}

	private void importSection(DefaultMutableTreeNode node) {

		fcImport.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		DefaultMutableTreeNode currentNode = getSelectedVariationTreeNode(jTreeParameter);
		if (currentNode.getUserObject() instanceof Variation) {
			fcImport.setCurrentDirectory(
					new File(((Variation) currentNode.getUserObject()).getVariationPath()).getParentFile());
		}

		int ret = fcImport.showOpenDialog(this.getParent());

		if (JFileChooser.APPROVE_OPTION == ret) {
			File selectedVaraiationDirectory = fcImport.getSelectedFile();
			File[] filesInDirectory = selectedVaraiationDirectory.listFiles();
			if (filesInDirectory.length > 1) {
				boolean internalFileFound = false;
				boolean preconditionFileFound = false;
				for (int i = 0; i < filesInDirectory.length; i++) {
					if (filesInDirectory[i].getName().equals(ConstantsAnalyzer.BREAKOUT_KERNEL_INTERNALS_FILENAME)
							|| filesInDirectory[i].getName()
									.equals(ConstantsAnalyzer.HEATFLUX_KERNEL_INTERNALS_FILENAME)) {
						internalFileFound = true;
					} else if (filesInDirectory[i].getName()
							.equals(ConstantsAnalyzer.BREAKOUT_KERNEL_PRECONDITIONS_FILENAME)
							|| filesInDirectory[i].getName()
									.equals(ConstantsAnalyzer.HEATFLUX_KERNEL_PRECONDITIONS_FILENAME)) {
						preconditionFileFound = true;
					}
				}
				if (internalFileFound && preconditionFileFound) {

					importVariation(fcImport.getSelectedFile());
				}
			}
		}
	}

	public void importVariation(File fImport) {
		Properties tmpVariationBOPKernelProperties = null;
		Properties tmpVariationBOPPreconditionProperties = null;
		Properties tmpVariationFPDKernelProperties = null;
		Properties tmpVariationFPDPreconditionProperties = null;
		Properties tmpVariationHFKernelProperties = null;
		Properties tmpVariationHFPreconditionProperties = null;

		if (new File(fImport.getAbsolutePath() + "\\" + ConstantsAnalyzer.BREAKOUT_KERNEL_INTERNALS_FILENAME)
				.exists()) {
			tmpVariationBOPKernelProperties = c_analyzer.importIniFile(
					fImport.getAbsolutePath() + "\\" + ConstantsAnalyzer.BREAKOUT_KERNEL_INTERNALS_FILENAME, "bop");

			tmpVariationBOPPreconditionProperties = c_analyzer.importIniFile(
					fImport.getAbsolutePath() + "\\" + ConstantsAnalyzer.BREAKOUT_KERNEL_PRECONDITIONS_FILENAME, "bop");
		}
		if (new File(fImport.getAbsolutePath() + "\\" + ConstantsAnalyzer.FRICTION_KERNEL_INTERNALS_FILENAME)
				.exists()) {
			tmpVariationFPDKernelProperties = c_analyzer.importIniFile(
					fImport.getAbsolutePath() + "\\" + ConstantsAnalyzer.FRICTION_KERNEL_INTERNALS_FILENAME, "fpd");

			tmpVariationFPDPreconditionProperties = c_analyzer.importIniFile(
					fImport.getAbsolutePath() + "\\" + ConstantsAnalyzer.FRICTION_KERNEL_PRECONDITIONS_FILENAME, "fpd");

		}
		if (new File(fImport.getAbsolutePath() + "\\" + ConstantsAnalyzer.HEATFLUX_KERNEL_INTERNALS_FILENAME)
				.exists()) {
			tmpVariationHFKernelProperties = c_analyzer.importIniFile(
					fImport.getAbsolutePath() + "\\" + ConstantsAnalyzer.HEATFLUX_KERNEL_INTERNALS_FILENAME, "hf");

			tmpVariationHFPreconditionProperties = c_analyzer.importIniFile(
					fImport.getAbsolutePath() + "\\" + ConstantsAnalyzer.HEATFLUX_KERNEL_PRECONDITIONS_FILENAME, "hf");

		}
		String variationPath = fImport.getAbsolutePath() + "\\";
		Variation newVariation = new Variation(variationPath, false, tmpVariationBOPKernelProperties,
				tmpVariationBOPPreconditionProperties, tmpVariationFPDKernelProperties,
				tmpVariationFPDPreconditionProperties, tmpVariationHFKernelProperties,
				tmpVariationHFPreconditionProperties);

		variations.add(newVariation);

		rootNode.add(createVariationTree(baiProperties, newVariation, new ArrayList<String>()));

		checkTreeSectionNames();
		parameterTreeModel.reload();
		jTreeParameter.invalidate();
		jTreeParameter.repaint();
	}

	private DefaultMutableTreeNode findKeyNode(DefaultMutableTreeNode variation, String section, String key) {
		if (null == variation) {
			return null;
		}
		DefaultMutableTreeNode sectionNode = findSectionNode(variation, section);
		if (null != sectionNode) {
			return findKeyNode(sectionNode, key);
		}
		return null;
	}

	private DefaultMutableTreeNode findKeyNode(DefaultMutableTreeNode section, String key) {
		Enumeration<TreeNode> enumeration = section.children();
		while (enumeration.hasMoreElements()) {
			DefaultMutableTreeNode keyNode = (DefaultMutableTreeNode) enumeration.nextElement();
			if (VariationKey.class == keyNode.getUserObject().getClass()) {
				if (((VariationKey) (keyNode.getUserObject())).getVariationKey().equalsIgnoreCase(key)) {
					return keyNode;
				}
			}
		}
		return null;
	}

	// Finds the path in tree as specified by the array of names. The names array is
	// a
	// sequence of names where names[0] is the root and names[i] is a child of
	// names[i-1].
	// Comparison is done using String.equals(). Returns null if not found.
	public TreePath findByName(JTree tree, String[] names) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		return find2(tree, new TreePath(root), names, 0, false);
	}

	@SuppressWarnings("rawtypes")
	private TreePath find2(JTree tree, TreePath parent, Object[] nodes, int depth, boolean byName) {
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		Object o = node;

		// If by name, convert node to a string
		if (byName) {
			o = o.toString();
		}

		// If equal, go down the branch
		if (o.equals(nodes[depth])) {
			// If at end, return match
			if (depth == nodes.length - 1) {
				return parent;
			}

			// Traverse children
			if (node.getChildCount() >= 0) {
				for (Enumeration e = node.children(); e.hasMoreElements();) {
					TreeNode n = (TreeNode) e.nextElement();
					TreePath path = parent.pathByAddingChild(n);
					TreePath result = find2(tree, path, nodes, depth + 1, byName);
					// Found a match
					if (result != null) {
						return result;
					}
				}
			}
		}

		// No match at this branch
		return null;
	}

	private DefaultMutableTreeNode findSectionNode(DefaultMutableTreeNode variation, String section) {
		Enumeration<TreeNode> enumeration = variation.children();
		while (enumeration.hasMoreElements()) {
			DefaultMutableTreeNode sectionNode = (DefaultMutableTreeNode) enumeration.nextElement();
			if (VariationSection.class == sectionNode.getUserObject().getClass()) {
				if (((VariationSection) (sectionNode.getUserObject())).getSection().equalsIgnoreCase(section)) {
					return sectionNode;
				}
			}
		}
		return null;
	}

	void jButtonReport_actionPerformed(ActionEvent e) {
		if (null != reportDirectory) {
			fcReport.setCurrentDirectory(reportDirectory);
		}
		fcReport.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int ret = fcReport.showOpenDialog(this.getParent());
		if (JFileChooser.APPROVE_OPTION == ret) {
			reportDirectory = fcReport.getSelectedFile();
			jTextFieldReport.setText(reportDirectory.getAbsolutePath());
		}
	}

	void jButtonRealEvent_actionPerformed(ActionEvent e) {
		if (null != realEventFile) {
			fcRealEvent.setCurrentDirectory(realEventFile);
		}
		fcRealEvent.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int ret = fcRealEvent.showOpenDialog(this.getParent());
		if (JFileChooser.APPROVE_OPTION == ret) {
			realEventFile = fcRealEvent.getSelectedFile();
			jTextFieldRealEvent.setText(realEventFile.getAbsolutePath());
			c_analyzer.openRealEvents();
		}
	}

	private void jButtonVariationOnOff_actionPerformed(ActionEvent e, boolean on) {
		DefaultMutableTreeNode node = getSelectedVariationTreeNode(jTreeParameter);
		if (null != node) {
			Object variation = node.getUserObject();
			if (variation.getClass() == Variation.class) {
				((Variation) variation).enable(on);
				jTreeParameter.invalidate();
				jTreeParameter.repaint();
			}
		}
	}

	public DefaultMutableTreeNode getSelectedVariationTreeNode(JTree parameter) {
		DefaultMutableTreeNode ret = null;
		TreePath parentPath = parameter.getSelectionPath();
		if (null != parentPath) {
			ret = ((DefaultMutableTreeNode) (parentPath.getLastPathComponent()));
		}
		return ret;
	}

	public ArrayList<Variation> getSelectedVariations(JTree parameter) {
		ArrayList<Variation> ret = new ArrayList<Variation>();
		TreePath[] parentPath = parameter.getSelectionPaths();
		for (int i = 0; i < parentPath.length; i++) {
			if (null != parentPath[i]) {
				DefaultMutableTreeNode currentNode = ((DefaultMutableTreeNode) (parentPath[i].getLastPathComponent()));
				if (null != currentNode) {
					Object keyObj = currentNode.getUserObject();
					if (null != keyObj) {
						if (Variation.class == keyObj.getClass()) {
							Variation variation = (Variation) keyObj;

							ret.add(variation);
						}
					}
				}
			}
		}
		return ret;
	}

	private void parameterTreeSelectionChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTreeParameter.getLastSelectedPathComponent();
		if (null == node) {
			return;
		}
		Object nodeInfo = node.getUserObject();
		// Edit key values
		if (nodeInfo.getClass() == VariationKey.class) {
			jTextFieldActualParameter.setText(((VariationKey) nodeInfo).getValue());
			jTextFieldActualParameter.setEnabled(true);
			jTextFieldActualParameter.invalidate();
			jButtonVariationOn.setVisible(false);
			jButtonVariationOff.setVisible(false);
			jButtonVariationDelete.setVisible(false);
			jButtonVariationRemove.setVisible(false);
			jButtonVariationRename.setVisible(false);

			jButtonVariationShowDiff.setVisible(false);
			jButtonVariationReset.setVisible(!(((VariationKey) nodeInfo).isDefaultVariation()
					|| ((VariationKey) nodeInfo).isDefaultVariation1()));
			jButtonVariationOk.setVisible(true);
			jButtonVariationNew.setVisible(false);
			jButtonVariationExport.setVisible(false);
			jButtonVariationImport.setVisible(false);
			return;
		}
		// Edit existing variations
		if (nodeInfo.getClass() == Variation.class) {
			jTextFieldActualParameter.setText(((Variation) nodeInfo).getVariationName());
			jTextFieldActualParameter.setEnabled(false);
			jTextFieldActualParameter.invalidate();
			jButtonVariationOn.setVisible(true);
			jButtonVariationOff.setVisible(true);
			jButtonVariationDelete.setVisible(
					!(((Variation) nodeInfo).isDefaultVariation() || ((Variation) nodeInfo).isDefaultVariation1()));
			jButtonVariationRemove.setVisible(
					!(((Variation) nodeInfo).isDefaultVariation() || ((Variation) nodeInfo).isDefaultVariation1()));
			jButtonVariationRename.setVisible(
					!(((Variation) nodeInfo).isDefaultVariation() || ((Variation) nodeInfo).isDefaultVariation1()));
			jButtonVariationShowDiff.setVisible(true);
			jButtonVariationReset.setVisible(false);
			jButtonVariationOk.setVisible(false);
			jButtonVariationNew.setVisible(true);
			jButtonVariationExport.setVisible(true);
			jButtonVariationImport.setVisible(true);
			rename = false;
			return;
		}
		jTextFieldActualParameter.setText("");
		jTextFieldActualParameter.setEnabled(false);
		jTextFieldActualParameter.invalidate();
		jButtonVariationOn.setVisible(false);
		jButtonVariationOff.setVisible(false);
		jButtonVariationDelete.setVisible(false);
		jButtonVariationRemove.setVisible(false);
		jButtonVariationRename.setVisible(false);
		jButtonVariationReset.setVisible(false);
		jButtonVariationOk.setVisible(true);
		jButtonVariationShowDiff.setVisible(false);
		jButtonVariationNew.setVisible(false);
		jButtonVariationExport.setVisible(false);
		jButtonVariationImport.setVisible(false);
	}

	private void checkTreeSectionNames() {
		Enumeration<TreeNode> eVar = rootNode.children();
		while (eVar.hasMoreElements()) {
			DefaultMutableTreeNode v = (DefaultMutableTreeNode) eVar.nextElement();
			Enumeration<TreeNode> eSec = v.children();
			while (eSec.hasMoreElements()) {
				DefaultMutableTreeNode s = (DefaultMutableTreeNode) eSec.nextElement();
				Enumeration<TreeNode> eKey = s.children();
				boolean variety = false;
				while (eKey.hasMoreElements()) {
					variety |= ((VariationKey) ((DefaultMutableTreeNode) eKey.nextElement()).getUserObject())
							.isVariety();
				}
				((VariationSection) s.getUserObject()).setVarieties(variety);
			}
		}
	}

	private DefaultMutableTreeNode createVariationTree(Properties baiProperties, Variation variation,
			List<String> disabledVars) {
		variations.add(variation);
		String variationPath = variation.getVariationPath();

		DefaultMutableTreeNode ret = new DefaultMutableTreeNode(variation);

		ret.add(new DefaultMutableTreeNode(new VariationSection(variation, variationPath)));

		createPartOfVariationTree(ret, variation, variation.getBopKernelProperties());
		createPartOfVariationTree(ret, variation, variation.getBopPreconditionProperties());
		createPartOfVariationTree(ret, variation, variation.getFpdKernelProperties());
		createPartOfVariationTree(ret, variation, variation.getFpdPreconditionProperties());
		createPartOfVariationTree(ret, variation, variation.getHfKernelProperties());
		createPartOfVariationTree(ret, variation, variation.getHfPreconditionProperties());

		if (baiProperties.getProperty(BAIproperties.DEFAULT_VARIATIO) != null) {
			String defaultVariationInBai = baiProperties.getProperty(BAIproperties.DEFAULT_VARIATIO);
			if ((defaultVariationInBai.charAt(defaultVariationInBai.length() - 1)) == ',') {
				defaultVariationInBai = defaultVariationInBai.substring(0, defaultVariationInBai.length() - 1);
			}
			if (defaultVariationInBai.equals(variation.getVariationPath())) {
				variation.setDefaultVariation(true);
				defaultBOPVariationRootNode = ret;
			}
		}
		if (baiProperties.getProperty(BAIproperties.DEFAULT_1_VARIATIO) != null) {
			String defaultVariation1InBai = baiProperties.getProperty(BAIproperties.DEFAULT_1_VARIATIO);
			if ((defaultVariation1InBai.charAt(defaultVariation1InBai.length() - 1)) == ',') {
				defaultVariation1InBai = defaultVariation1InBai.substring(0, defaultVariation1InBai.length() - 1);
			}
			if (defaultVariation1InBai.equals(variation.getVariationPath())) {
				default1BOPVariationRootNode = ret;
			}
		}

		return ret;
	}

	private void createPartOfVariationTree(DefaultMutableTreeNode ret, Variation variation,
			Properties sourceProperties) {
		boolean childOfDefaultVariation1 = variation.isChildOfDefaultVariation1();
		boolean isDefaultVariation1 = variation.isDefaultVariation1();
		if (sourceProperties != null) {
			ArrayList<String> variationSectionKeys = new ArrayList<String>();
			ArrayList<String> variationKeys = new ArrayList<String>();

			Variation.extractKeys(variationSectionKeys, variationKeys, sourceProperties.keys());

			// create array of String with key
			String[] key = new String[variationKeys.size()];
			for (int i = 0; i < key.length; i++) {
				key[i] = variationKeys.get(i);
			}

			// sort keys
			Arrays.sort(key);

			// create array of String with section
			String[] section = new String[variationSectionKeys.size()];
			for (int i = 0; i < section.length; i++) {
				section[i] = variationSectionKeys.get(i);
			}

			Arrays.sort(section);
			// sort section

			for (int i = 0; i < section.length; i++) {
				DefaultMutableTreeNode sNode = new DefaultMutableTreeNode(new VariationSection(variation, section[i]));
				ret.add(sNode);
				for (int j = 0; j < key.length; j++) {
					if (key[j].startsWith(section[i]) && key[j].charAt(section[i].length()) == '.') {
						String value = null;
						if (sourceProperties.containsKey(key[j])) {
							value = sourceProperties.getProperty(key[j], null);
						}

						VariationKey defaultValue = null;
						VariationKey ActualKey = null;

						if (childOfDefaultVariation1 || isDefaultVariation1) {
							if (null != default1BOPVariationRootNode) {
								DefaultMutableTreeNode default1Node = findKeyNode(default1BOPVariationRootNode,
										section[i], key[j]);
								if (null != default1Node) {
									defaultValue = ((VariationKey) default1Node.getUserObject());
								} else {
									MessageLogger.info(Constants.logger,
											"ERROR default1KeyNotFound: " + section[i] + "." + key[j]);
								}
							}
						} else {
							if (null != defaultBOPVariationRootNode) {
								DefaultMutableTreeNode defaultNode = findKeyNode(defaultBOPVariationRootNode,
										section[i], key[j]);
								if (null != defaultNode) {
									defaultValue = ((VariationKey) defaultNode.getUserObject());
								} else {
									MessageLogger.info(Constants.logger,
											"ERROR defaultKeyNotFound: " + section[i] + "." + key[j]);
								}
							}
						}
						String keyName = key[j].substring(section[i].length() + 1);

						if ((value == null) && (ActualKey != null)) {
							value = ActualKey.getValue();
						} else {
						}

						sNode.add(new DefaultMutableTreeNode(
								new VariationKey(variation, section[i], keyName, value, defaultValue)));
					}
				}
			}
		}
	}

	public void setData(Properties baiProperties) {
		this.baiProperties = (Properties) baiProperties.clone();

		if (baiProperties.getProperty(BAIproperties.REPORT_DIRECTORY) != null) {
			reportDirectory = new File(baiProperties.getProperty(BAIproperties.REPORT_DIRECTORY));
		}

		if (reportDirectory != null) {
			jTextFieldReport.setText(reportDirectory.getAbsolutePath());
		}

		if (baiProperties.getProperty(BAIproperties.REAL_ALARM_FILE) != null) {
			realEventFile = new File(baiProperties.getProperty(BAIproperties.REAL_ALARM_FILE));
		}

		if (realEventFile != null) {
			jTextFieldRealEvent.setText(realEventFile.getAbsolutePath());
		}

		jTextFieldCropAlarm.setText(baiProperties.getProperty(BAIproperties.CROP_ALARMS_BEFORE, "180"));
		jTextFieldSupressAlarm.setText(baiProperties.getProperty(BAIproperties.SUPRESS_ALARMS, "180"));
		jTextFieldSupressFAlarm.setText(baiProperties.getProperty(BAIproperties.SUPRESS_FALARMS, "180"));
		jTextFieldGroupFalseAlarm.setText(baiProperties.getProperty(BAIproperties.GROUPE_FALSE_ALARMS, "30"));

		// get disabled variations
		String disStr = baiProperties.getProperty(BAIproperties.DISABLED_VARIATION);
		List<String> disVec = new ArrayList<String>();
		int idx = 0;
		int len = 0;
		// 2003-07-21 AP: fixed bug where part of next statement was accedentally
		// deleted
		if (disStr != null) {
			len = disStr.length();
		}

		while (idx < len) {
			int pos = disStr.indexOf(',', idx);
			if (0 > pos) {
				pos = len;
			}
			disVec.add(disStr.substring(idx, pos).trim());
			idx = pos + 1;
		}
		// get ready for nodes
		rootNode.removeAllChildren();
		// get all variations
		String variationPaths = baiProperties.getProperty(BAIproperties.VARIATION_PATHS);

		readAndBuildVariationProperties(variationPaths, disVec);

		// jTreeParameter.scrollPathToVisible(new TreePath(newVar.getPath()));
		checkTreeSectionNames();
		parameterTreeModel.reload();
		jTreeParameter.invalidate();
		jTreeParameter.repaint();
	}

	private void readAndBuildVariationProperties(String variationPaths, List<String> disVec) {
		int idx = 0;
		int len = variationPaths.length();
		while (idx < len) {
			int pos = variationPaths.indexOf(',', idx);
			if (0 > pos) {
				pos = len;
			}
			String variationPath = variationPaths.substring(idx, pos).trim();
			if (new File(variationPath).exists()) {

				Properties tmpVariationBOPKernelProperties = null;
				Properties tmpVariationBOPPreconditionProperties = null;
				Properties tmpVariationFPDKernelProperties = null;
				Properties tmpVariationFPDPreconditionProperties = null;
				Properties tmpVariationHFKernelProperties = null;
				Properties tmpVariationHFPreconditionProperties = null;

				if (new File(variationPath + ConstantsAnalyzer.BREAKOUT_KERNEL_INTERNALS_FILENAME).exists()) {
					tmpVariationBOPKernelProperties = c_analyzer
							.importIniFile(variationPath + ConstantsAnalyzer.BREAKOUT_KERNEL_INTERNALS_FILENAME, "bop");
					tmpVariationBOPPreconditionProperties = c_analyzer.importIniFile(
							variationPath + ConstantsAnalyzer.BREAKOUT_KERNEL_PRECONDITIONS_FILENAME, "bop");

				}
				if (new File(variationPath + ConstantsAnalyzer.FRICTION_KERNEL_INTERNALS_FILENAME).exists()) {
					tmpVariationFPDKernelProperties = c_analyzer
							.importIniFile(variationPath + ConstantsAnalyzer.FRICTION_KERNEL_INTERNALS_FILENAME, "fpd");
					tmpVariationFPDPreconditionProperties = c_analyzer.importIniFile(
							variationPath + ConstantsAnalyzer.FRICTION_KERNEL_PRECONDITIONS_FILENAME, "fpd");
				}
				if (new File(variationPath + ConstantsAnalyzer.HEATFLUX_KERNEL_INTERNALS_FILENAME).exists()) {
					tmpVariationHFKernelProperties = c_analyzer
							.importIniFile(variationPath + ConstantsAnalyzer.HEATFLUX_KERNEL_INTERNALS_FILENAME, "hf");
					tmpVariationHFPreconditionProperties = c_analyzer.importIniFile(
							variationPath + ConstantsAnalyzer.HEATFLUX_KERNEL_PRECONDITIONS_FILENAME, "hf");
				}

				Variation variation = new Variation(variationPath, disVec.contains(variationPath),
						tmpVariationBOPKernelProperties, tmpVariationBOPPreconditionProperties,
						tmpVariationFPDKernelProperties, tmpVariationFPDPreconditionProperties,
						tmpVariationHFKernelProperties, tmpVariationHFPreconditionProperties);

				rootNode.add(createVariationTree(baiProperties, variation, disVec));
			}
			idx = pos + 1;
		}
	}

	public void getData(Properties baiProperties) {
		// Directories + Files

		reportDirectory = new File(jTextFieldReport.getText());
		realEventFile = new File(jTextFieldRealEvent.getText());

		baiProperties.setProperty(BAIproperties.REPORT_DIRECTORY, reportDirectory.getAbsolutePath());
		baiProperties.setProperty(BAIproperties.REAL_ALARM_FILE, realEventFile.getAbsolutePath());

		// crop alarms
		baiProperties.setProperty(BAIproperties.CROP_ALARMS_BEFORE, jTextFieldCropAlarm.getText());
		baiProperties.setProperty(BAIproperties.GROUPE_FALSE_ALARMS, jTextFieldGroupFalseAlarm.getText());
		baiProperties.setProperty(BAIproperties.SUPRESS_ALARMS, jTextFieldSupressAlarm.getText());

		// Variations
		String varString = null;
		String varDisabled = null;
		Enumeration<TreeNode> variationEnum = rootNode.children();
		DefaultMutableTreeNode varNode;
		Properties tmpVariationBOPKernelProperties = null;
		Properties tmpVariationBOPPreconditionProperties = null;
		Properties tmpVariationFPDKernelProperties = null;
		Properties tmpVariationFPDPreconditionProperties = null;
		Properties tmpVariationHFKernelProperties = null;
		Properties tmpVariationHFPreconditionProperties = null;
		Variation variation;
		Enumeration<TreeNode> eKey;
		Enumeration<TreeNode> variationKeysEnum;
		VariationKey key;
		DefaultMutableTreeNode secNode;

		while (variationEnum.hasMoreElements()) {
			varNode = (DefaultMutableTreeNode) variationEnum.nextElement();
			if (Variation.class == varNode.getUserObject().getClass()) {
				variation = (Variation) varNode.getUserObject();
				tmpVariationBOPKernelProperties = null;
				tmpVariationBOPPreconditionProperties = null;
				tmpVariationFPDKernelProperties = null;
				tmpVariationFPDPreconditionProperties = null;
				tmpVariationHFKernelProperties = null;
				tmpVariationHFPreconditionProperties = null;
				for (int i = 0; i < variations.size(); i++) {
					if (variation.getVariationPath().equals(variations.get(i).getVariationPath())) {
						tmpVariationBOPKernelProperties = variations.get(i).getBopKernelProperties();
						tmpVariationBOPPreconditionProperties = variations.get(i).getBopPreconditionProperties();
						tmpVariationFPDKernelProperties = variations.get(i).getFpdKernelProperties();
						tmpVariationFPDPreconditionProperties = variations.get(i).getFpdPreconditionProperties();
						tmpVariationHFKernelProperties = variations.get(i).getHfKernelProperties();
						tmpVariationHFPreconditionProperties = variations.get(i).getHfPreconditionProperties();
					}
				}

				if (null == varString) {
					varString = variation.getVariationPath();
				} else {
					varString = varString + ", " + variation.getVariationPath();
				}

				if (!variation.isEnabled()) {
					if (null == varDisabled) {
						varDisabled = variation.getVariationPath();
					} else {
						varDisabled = varDisabled + ", " + variation.getVariationPath();
					}
				}

				variationKeysEnum = varNode.children();
				while (variationKeysEnum.hasMoreElements()) {
					secNode = (DefaultMutableTreeNode) variationKeysEnum.nextElement();
					eKey = secNode.children();
					while (eKey.hasMoreElements()) {
						key = (VariationKey) ((DefaultMutableTreeNode) eKey.nextElement()).getUserObject();
						if (tmpVariationBOPKernelProperties != null
								&& tmpVariationBOPKernelProperties.containsKey(key.getVariationKey())) {
							tmpVariationBOPKernelProperties.setProperty(key.getVariationKey(), key.getValue());
						} else if (tmpVariationBOPPreconditionProperties != null
								&& tmpVariationBOPPreconditionProperties.containsKey(key.getVariationKey())) {
							tmpVariationBOPPreconditionProperties.setProperty(key.getVariationKey(), key.getValue());
						} else if (tmpVariationFPDKernelProperties != null
								&& tmpVariationFPDKernelProperties.containsKey(key.getVariationKey())) {
							tmpVariationFPDKernelProperties.setProperty(key.getVariationKey(), key.getValue());
						} else if (tmpVariationFPDPreconditionProperties != null
								&& tmpVariationFPDPreconditionProperties.containsKey(key.getVariationKey())) {
							tmpVariationFPDPreconditionProperties.setProperty(key.getVariationKey(), key.getValue());
						} else if (tmpVariationHFKernelProperties != null
								&& tmpVariationHFKernelProperties.containsKey(key.getVariationKey())) {
							tmpVariationHFKernelProperties.setProperty(key.getVariationKey(), key.getValue());
						} else if (tmpVariationHFPreconditionProperties != null
								&& tmpVariationHFPreconditionProperties.contains(key.getVariationKey())) {
							tmpVariationHFPreconditionProperties.setProperty(key.getVariationKey(), key.getValue());
						}
						key = null;
					}

					eKey = null;
					secNode = null;
				}
				variationKeysEnum = null;
				varNode = null;
				tmpVariationBOPKernelProperties = null;
				tmpVariationBOPPreconditionProperties = null;
				tmpVariationFPDKernelProperties = null;
				tmpVariationFPDPreconditionProperties = null;
				tmpVariationHFKernelProperties = null;
				tmpVariationHFPreconditionProperties = null;
				variation = null;
			}

		}
		if (null == varString) {
			varString = "";
		}
		if (null == varDisabled) {
			varDisabled = "";
		}

		baiProperties.setProperty(BAIproperties.VARIATION_PATHS, varString);
		baiProperties.setProperty(BAIproperties.DISABLED_VARIATION, varDisabled);
		varString = null;
		varDisabled = null;
	}

	public void saveVariations() {
		try {
			File f;
			for (int i = 0; i < variations.size(); i++) {
				Variation variation = variations.get(i);
				String variationPath = variation.getVariationPath();
				f = new File(variationPath + ConstantsAnalyzer.BREAKOUT_KERNEL_INTERNALS_FILENAME);
				if (f.exists()) {

					iniFileUpdater(new File(variationPath + ConstantsAnalyzer.BREAKOUT_KERNEL_INTERNALS_FILENAME),
							variation.getBopKernelProperties(), "bop");

				}
				f = new File(variationPath + ConstantsAnalyzer.FRICTION_KERNEL_INTERNALS_FILENAME);
				if (f.exists()) {
					iniFileUpdater(new File(variationPath + ConstantsAnalyzer.FRICTION_KERNEL_INTERNALS_FILENAME),
							variation.getFpdKernelProperties(), "fpd");
				}
				f = new File(variationPath + ConstantsAnalyzer.BREAKOUT_KERNEL_PRECONDITIONS_FILENAME);
				if (f.exists()) {
					iniFileUpdater(new File(variationPath + ConstantsAnalyzer.BREAKOUT_KERNEL_PRECONDITIONS_FILENAME),
							variation.getBopPreconditionProperties(), "bop");
				}
				f = new File(variationPath + ConstantsAnalyzer.FRICTION_KERNEL_PRECONDITIONS_FILENAME);
				if (f.exists()) {
					iniFileUpdater(new File(variationPath + ConstantsAnalyzer.FRICTION_KERNEL_PRECONDITIONS_FILENAME),
							variation.getFpdPreconditionProperties(), "fpd");
				}
				f = new File(variationPath + ConstantsAnalyzer.HEATFLUX_KERNEL_INTERNALS_FILENAME);
				if (f.exists()) {
					iniFileUpdater(new File(variationPath + ConstantsAnalyzer.HEATFLUX_KERNEL_INTERNALS_FILENAME),
							variation.getHfKernelProperties(), "hf");
				}
				f = new File(variationPath + ConstantsAnalyzer.HEATFLUX_KERNEL_PRECONDITIONS_FILENAME);
				if (f.exists()) {
					iniFileUpdater(new File(variationPath + ConstantsAnalyzer.HEATFLUX_KERNEL_PRECONDITIONS_FILENAME),
							variation.getHfPreconditionProperties(), "hf");
				}
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	/**
	 * Will update all Properties in the File. every line is searched for a key. If
	 * a key is found it will be searched in the Properties for the equivalent key.
	 * If the keya was found in the Properties the Value will be set at the File.
	 * 
	 * @param iniFile
	 * @param props
	 */
	private void iniFileUpdater(File iniFile, Properties props, String prefix) {
		try {
			FileReader in = new FileReader(iniFile);
			BufferedReader reader = new BufferedReader(in);
			String line = reader.readLine();
			String section = null;

			StringBuilder contents = new StringBuilder();
			String key;
			String sectionKey;
			int idx;
			int len;
			while (null != line) {
				if (line.startsWith("[")) {
					len = line.indexOf(']');
					if (0 < len) {
						section = line.substring(1, len);
					}
				}
				if ((!line.startsWith("#")) && (null != section)) {
					idx = line.indexOf('=');
					if (0 < idx) {
						key = line.substring(0, idx);
						sectionKey = key;
						if (section != null) {
							sectionKey = section + "." + key;
						}
						line = key + "=" + props.getProperty(prefix + "." + sectionKey);
					}
				}
				contents.append(line);
				contents.append(System.getProperty("line.separator"));
				line = reader.readLine();
			}
			in.close();
			reader.close();

			Writer output = new BufferedWriter(new FileWriter(iniFile));

			output.write(contents.toString());

			output.close();
			in = null;
			reader = null;
			line = null;
			section = null;
			output = null;
			contents = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String[] getActiveVariationNames() {
		List<String> name = new ArrayList<String>();
		Enumeration<TreeNode> eVar = rootNode.children();
		while (eVar.hasMoreElements()) {
			DefaultMutableTreeNode varNode = (DefaultMutableTreeNode) eVar.nextElement();
			if (Variation.class == varNode.getUserObject().getClass()) {
				Variation varName = (Variation) varNode.getUserObject();
				if (varName.isEnabled()) {
					name.add(varName.getVariationName());
				}
			}
		}

		String[] ret = new String[name.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = name.get(i);
		}
		return ret;
	}

	public double getCropAlarmTime() {
		return Double.valueOf(jTextFieldCropAlarm.getText()).doubleValue();
	}

	public double getGroupFalseAlarmTime() {
		return Double.valueOf(jTextFieldGroupFalseAlarm.getText()).doubleValue();
	}

	public double getSupressAlarmTime() {
		return Double.valueOf(jTextFieldSupressAlarm.getText()).doubleValue();
	}

	/**
	 * @return configuration file for BOPalgorithm (kernel)
	 * @param variation name of variation
	 */
	public Variation getVariation(String variation) {
		Enumeration<TreeNode> eVar = rootNode.children();
		while (eVar.hasMoreElements()) {
			DefaultMutableTreeNode varNode = (DefaultMutableTreeNode) eVar.nextElement();
			if (Variation.class == varNode.getUserObject().getClass()) {
				Variation sourceVariation = (Variation) varNode.getUserObject();
				if (0 == variation.compareTo(sourceVariation.getVariationName())) {
					return sourceVariation;
				}
			}
		}
		return null;
	}

	public File getRealEventFile() {
		return realEventFile;
	}

	public JScrollPane getJScrollPaneTreeParameter() {
		return jScrollPaneTreeParameter;
	}
	/**
	 * public class ExportToWindow extends JDialog{
	 * 
	 * public void exportDialog (String[] args) { JDialog dialogWindow = new
	 * JDialog(); dialogWindow.setTitle("export to...");
	 * dialogWindow.setSize(200,200); dialogWindow.setModal(true);
	 * dialogWindow.setVisible(true); }
	 * 
	 * JRadioButton rdo_ini = new JRadioButton (".ini", true); JRadioButton
	 * rdo_inter = new JRadioButton (".internals", false); /**public static void
	 * main(String[] argv) throws Exception { int i = okcancel("Are your sure ?");
	 * System.out.println("ret : " + i);}
	 * 
	 * public int okcancel(String theMessage) { int result =
	 * JOptionPane.showConfirmDialog((Component) null, theMessage, "alert",
	 * JOptionPane.OK_CANCEL_OPTION); return result; } }
	 **/

}

// =================================================================================================
/**
 * $History: JPanelVariation.java $
 * 
 * ***************** Version 11 ***************** User: Rieseax Date: 2.11.05
 * Time: 15:15 Updated in $/CL2/DEV/vai/moldexpert/breakoutexpert/analyzer Java
 * 1.5 changes
 * 
 * ***************** Version 10 ***************** User: Schustem Date: 26.08.05
 * Time: 9:43 Updated in $/CL2/DEV/vai/moldexpert/breakoutexpert/analyzer
 * Schuster 2005/08/26: Extension, create variation from variation, react on
 * enter in parameter-value-Textfield, check variationname for " " and ","
 * 
 * ***************** Version 9 ***************** User: Pesekand Date: 15.06.05
 * Time: 9:36 Updated in $/CL2/DEV/vai/moldexpert/breakoutexpert/analyzer
 * modified header
 * 
 * ***************** Version 8 ***************** User: Rieseax Date: 10.06.05
 * Time: 10:48 Updated in $/CL2/DEV/vai/moldexpert/breakoutexpert/analyzer
 * Martin Schuster: Extension for multiple row-usage (Sub-Algorithms)
 */
// =================================================================================================
