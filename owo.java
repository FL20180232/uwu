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

import vatron.moldexpert.breakoutexpert.ltv.common.Constants;
import vatron.utils.logger.MessageLogger;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;
import java.awt.event.ActionEvent;

public class ExportDialog extends JDialog {
	private final JPanel contentPanel = new JPanel();
	private JPanelVariation mainWindow;
	private JRadioButton rdo_ini;
	private Analyzer c_Analyzer;

	/**
	 * Create the dialog.
	 */
	public ExportDialog(JPanelVariation mainWindow, Analyzer c_Analyzer) {
		this.mainWindow = mainWindow;
		this.c_Analyzer = c_Analyzer;

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

						int ret = fcExporter.showSaveDialog(c_Analyzer);

						if (JFileChooser.APPROVE_OPTION == ret) {
							((Analyzer) c_Analyzer).jButtonSaveConfig_actionPerformed(null);
							File exportIniFile = fcExporter.getSelectedFile();
							String exportReport = mainWindow.exportToIni(exportIniFile );
							Analyzer.getUserProperties()
									.setStandardExportIniPath(exportIniFile.getParentFile().getAbsolutePath());
							new ExportReportDialog((Analyzer) c_Analyzer, exportReport);
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
