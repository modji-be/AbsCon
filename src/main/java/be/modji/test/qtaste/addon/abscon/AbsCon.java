package be.modji.test.qtaste.addon.abscon;

import java.awt.BorderLayout;

import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.qspin.qtaste.addon.AddOn;
import com.qspin.qtaste.addon.AddOnException;
import com.qspin.qtaste.addon.AddOnMetadata;
import com.qspin.qtaste.util.Environment;

import be.modji.test.qtaste.addon.abscon.ui.MenuAddOn;
import be.modji.test.qtaste.addon.abscon.ui.PanelAbstractTests;
import be.modji.test.qtaste.addon.abscon.ui.PanelAction;
import be.modji.test.qtaste.addon.abscon.ui.PanelAssert;
import be.modji.test.qtaste.addon.abscon.ui.PanelData;
import be.modji.test.qtaste.addon.abscon.ui.PanelUIMapping;
import be.modji.test.qtaste.addon.abscon.ui.PanelUIModel;
import be.modji.test.qtaste.addon.abscon.ui.PanelLoad;
import be.modji.test.qtaste.addon.abscon.ui.PanelQTasteTest;

public class AbsCon extends AddOn
{
	private JPanel mConfigurationPane;
	private JMenu mMenu;
	private	JTabbedPane tabbedPane;
	
	/** 
	 * Constructor of the Addon
	 * @param pMetaData
	 * @author jvanhec
	 */
    public AbsCon(AddOnMetadata pMetaData) {
		super(pMetaData);
	}

    /**
     * Load the addon and the menu bar
     * @author jvanhec
     * @return true
     */
	@Override
	public boolean loadAddOn() throws AddOnException {
		System.out.println("Load AddOn 'AbsCon'");
		if (mMenu ==null)
			mMenu = new MenuAddOn();
		Environment.getEnvironment().getMainMenuBar().add(mMenu);
		return true;
	}

	/**
	 * Unload the addon
	 * @author jvanhec
	 * @return true
	 */
	@Override
	public boolean unloadAddOn() throws AddOnException {
		System.out.println("Unload AddOn 'AbsCon'");
		Environment.getEnvironment().getMainMenuBar().remove(mMenu);
		mMenu = null;
		return true;
	}

	/**
	 * Says that the addon has a configuration
	 * @author jvanhec
	 * @return true
	 */
	@Override
	public boolean hasConfiguration() {
		return true;
	}

	/**
	 * Return the configuration GUI
	 * @author jvanhec
	 * @return the JPanel that contains the GUI
	 */
	@Override
	public JPanel getConfigurationPane() {

		// Create the tabbed pane
		if ( mConfigurationPane == null ){
			JPanel topPanel = new JPanel();
			topPanel.setLayout(new BorderLayout());
			tabbedPane = new JTabbedPane();
			tabbedPane.addTab( "Load", null, new PanelLoad(), "Load abstract test file and select metamodels");
			tabbedPane.addTab( "Abstract tests", null, new PanelAbstractTests(), "Analyze abstract tests");
			tabbedPane.addTab( "UI model", null,new PanelUIModel(), "Edit the UI models" );
			tabbedPane.addTab( "UI mapping",null, new PanelUIMapping(),"Edit the UI mapping");
			tabbedPane.addTab( "Actions",null, new PanelAction(),"Write the Python code for used actions" );
			tabbedPane.addTab( "Asserts",null, new PanelAssert(),"Write the Python code for used asserts" );
			tabbedPane.addTab( "Data",null, new PanelData(),"Define the data that will be used during tests");
			tabbedPane.addTab( "QTaste tests", null, new PanelQTasteTest(), "Generate QTaste tests" );
			topPanel.add( tabbedPane, BorderLayout.CENTER );
		    mConfigurationPane = topPanel;
		}
		return mConfigurationPane;
	}
}
