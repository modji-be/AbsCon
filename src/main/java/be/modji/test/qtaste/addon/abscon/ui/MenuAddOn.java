package be.modji.test.qtaste.addon.abscon.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.qspin.qtaste.util.Environment;

import be.modji.test.qtaste.addon.abscon.manager.TestsSuiteManager;

/**
 * JPanel for the main panel definition
 * must extends JPanel for validation
 * must extends FormDebugPanel for debug GUI
 * @author jvanhec
 *
 */
public class MenuAddOn extends JMenu implements ActionListener{

	private static final long serialVersionUID = 5734386089682453066L;
	private JMenuItem importBt;

	/**
	 * Constructor
	 * @author jvanhec
	 */
	public MenuAddOn(){
		super();

		//build mennu
		buildMenu();

	}

	/**
	 * Build the GUI menu
	 * @author jvanhec
	 */
	private void buildMenu(){
		this.setText("AbsCon");
		importBt = new JMenuItem("Import project");
		importBt.addActionListener(this);
		this.add(importBt);
	}

	/**
	 * Actions performed on the GUI
	 * @author jvanhec
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		//Button "import"
		if(source == importBt){
			JFileChooser c = new JFileChooser(Environment.getEnvironment().getMainFrame().getTestCasePanel().getTestSuiteDirectory());
			FileNameExtensionFilter filter = new FileNameExtensionFilter("CTAR files", "ctar");
			c.setFileFilter(filter);
			int rVal = c.showOpenDialog(this);
			if (rVal == JFileChooser.APPROVE_OPTION) {
				//update gui
				int res = TestsSuiteManager.getSingleton().open(c.getSelectedFile().getAbsolutePath());

				//show popup according to parsing result
				if (res ==1){
					JOptionPane.showMessageDialog(this, "Concretes tests parsed successfully");
				}else if (res == 0){
					JOptionPane.showMessageDialog(this,"Unexpected error while parsing the file! Try to import files separately for more details", "Error while loading file", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}
