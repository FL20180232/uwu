package vatron.moldexpert.breakoutexpert.analyzer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import vatron.utils.logger.MessageLogger;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.awt.event.ActionEvent;

public class ExportDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JPanel mainWindow;
	private JRadioButton rdo_ini;

	/**
	 * Create the dialog.
	 */
	public ExportDialog(JPanel mainWindow) {
		this.mainWindow = mainWindow;

		setTitle("Export to...");
		setBounds(100, 100, 250, 200);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] { 91, 193, 0 };
		gbl_contentPanel.rowHeights = new int[] { 20, 0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_contentPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		contentPanel.setLayout(gbl_contentPanel);
		{
			rdo_ini = new JRadioButton(".ini", false);
			rdo_ini.setHorizontalAlignment(SwingConstants.LEFT);
			GridBagConstraints gbc_rdo_ini = new GridBagConstraints();
			gbc_rdo_ini.fill = GridBagConstraints.VERTICAL;
			gbc_rdo_ini.anchor = GridBagConstraints.WEST;
			gbc_rdo_ini.insets = new Insets(1, 4, 1, 2);
			gbc_rdo_ini.gridwidth = 3;
			gbc_rdo_ini.gridheight = 3;
			// gbc_rdo_ini.gridx = 2;
			// gbc_rdo_ini.gridy = 1;
			contentPanel.add(rdo_ini, gbc_rdo_ini);

			JRadioButton rdo_inter = new JRadioButton(".internals", false);
			rdo_inter.setHorizontalAlignment(SwingConstants.LEFT);

			// gbc_rdo_inter.gridx = 3;
			// gbc_rdo_ini.gridy = 2;
			contentPanel.add(rdo_inter, gbc_rdo_ini);
			ButtonGroup group = new ButtonGroup();
			group.add(rdo_inter);
			group.add(rdo_ini);
		}

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		{
			JButton okButton = new JButton("OK");
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (rdo_ini.isSelected() == true) {

						JFileChooser fcExporter = new JFileChooser() {

							public boolean accept(File f) {
								return (f.getName().toLowerCase().endsWith(".ini") || f.isDirectory());
							}
						};
						fcExporter
								.setCurrentDirectory(new File(Analyzer.getUserProperties().getStandardExportIniPath()));
						fcExporter.setDialogTitle("Ini-File selection");

						Component c_analyzer = null;
						int ret = fcExporter.showSaveDialog(c_analyzer);

						if (JFileChooser.APPROVE_OPTION == ret) {
							((Analyzer) c_analyzer).jButtonSaveConfig_actionPerformed(null);
							File exportIniFile = fcExporter.getSelectedFile();
							String exportReport = exportToIni(exportIniFile);
							Analyzer.getUserProperties()
									.setStandardExportIniPath(exportIniFile.getParentFile().getAbsolutePath());
							new ExportReportDialog((Analyzer) c_analyzer, exportReport);
						}

					} else {
						// internals methode


						JFileChooser fcExporter = new JFileChooser() {

							public boolean accept(File f) {
								return (f.getName().toLowerCase().endsWith(".ini") || f.isDirectory());
							}
						};
						fcExporter
								.setCurrentDirectory(new File(Analyzer.getUserProperties().getStandardExportIniPath()));
						fcExporter.setDialogTitle("Ini-File selection");

						Component c_analyzer = null;
						int ret = fcExporter.showSaveDialog(c_analyzer);

						if (JFileChooser.APPROVE_OPTION == ret) {
							((Analyzer) c_analyzer).jButtonSaveConfig_actionPerformed(null);
							File exportIniFile = fcExporter.getSelectedFile();
							String exportReport = exportToIni(exportIniFile);
							Analyzer.getUserProperties()
									.setStandardExportIniPath(exportIniFile.getParentFile().getAbsolutePath());
							new ExportReportDialog((Analyzer) c_analyzer, exportReport);
						}

					
					}

				}
			});

			okButton.setActionCommand("OK");
			buttonPane.add(okButton);
			getRootPane().setDefaultButton(okButton);
		}
		{
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
					dispose();
				}
			});
			cancelButton.setActionCommand("Cancel");
			buttonPane.add(cancelButton);
		}
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}

	public String exportToIni(File iniFile, Object jTreeParameter) {
		String keyToReplace = null;
		StringBuilder exportReport = new StringBuilder();
		StringBuilder missingKeysString = new StringBuilder();
		StringBuilder errorString = new StringBuilder();
		missingKeysString.append("These Keys could not be found in the ini-File:").append("\n");
		ArrayList<String> missingKeys = new ArrayList<String>();

		StringBuilder changedKeys = new StringBuilder();
		changedKeys.append("These Keys were changed at the ini-File:").append("\n");
		return keyToReplace;
		
		   try {
		   
		   BufferedReader reader = new BufferedReader(new InputStreamReader(new
		   FileInputStream(iniFile), "UTF8"));
		   
		   String line = reader.readLine();
		   
		   StringBuilder contents = new StringBuilder(); 
		   // Read the whole content of the ini-File
		   while (null != line) { contents.append(line);
		   contents.append(System.getProperty("line.separator")); line =
		   reader.readLine(); reader.close(); } 
		   
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
		            }

		            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(iniFile)));
		                //Charset.forName("UTF-8")));

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
		   
		

    private DefaultMutableTreeNode getSelectedVariationTreeNode(Object jTreeParameter) {
		// TODO Auto-generated method stub
		return null;
	}

	public String exportToInternals(File internalsFile) {
	    String keyToReplace = null;
	    StringBuilder missingKeysString = new StringBuilder();
	    StringBuilder errorString = new StringBuilder();
	    missingKeysString.append("These Keys could not be found in the internals-File:").append("\n");
	    ArrayList<String> missingKeys = new ArrayList<String>();

	    StringBuilder changedKeys = new StringBuilder();
	    changedKeys.append("These Keys were changed at the internals-File:").append("\n");
	    return keyToReplace;
	    /**try {

	      BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(internalsFile), "UTF8"));

	      String line = reader.readLine();

	      StringBuilder contents = new StringBuilder();
	      // Read the whole content of the internals-File
	      while (null != line) {
	        contents.append(line);
	        contents.append(System.getProperty("line.separator"));
	        line = reader.readLine();

	      }
  reader.close();**/
}

}
