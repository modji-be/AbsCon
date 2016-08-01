package be.modji.test.qtaste.addon.abscon.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.CaretListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import be.modji.test.qtaste.addon.abscon.Utils;
import be.modji.test.qtaste.addon.abscon.manager.AbstractTestsManager;
import be.modji.test.qtaste.addon.abscon.manager.UIModelsManager;
import be.modji.test.qtaste.addon.abscon.manager.RefreshManager;
import be.modji.test.qtaste.addon.abscon.manager.TestsSuiteManager;
import be.modji.test.qtaste.addon.abscon.type.UIModel;

/**
 * JPanel for the main panel definition
 * must extends JPanel for validation
 * must extends FormDebugPanel for debug GUI
 * @author jvanhec
 *
 */
public class PanelLoad extends JPanel implements ActionListener,RefreshableElement{

	private static final long serialVersionUID = 908391613764301345L;
	private JLabel labelName, labelPath, labelMetaModel, picArchitecture;
	private JTextField fieldName, fieldPath;
	private JButton buttonBrowse, buttonAddModel;
	private  Box metamodelBox;

	/**
	 * Constructor
	 * @author jvanhec
	 */
	public PanelLoad(){
		super();

		//build panel
		buildPanel();

		//register
		register();
	}
	
	/**
	 * Build the GUI panel
	 * @author jvanhec
	 */
	private void buildPanel(){
		CellConstraints cc = new CellConstraints();
		FormLayout layout = new FormLayout(
				"20px, right:pref,20px,150px, 550px:grow,20px, 100px, 20px", 
				"20px, pref, 20px, pref,20px,30px,20px, 100px,20px,300px, 5px:grow");
		this.setLayout(layout);

		//elements 
		labelName = new JLabel( "Name for this test serie:" );
		labelPath = new JLabel( "Abstract test file path:" );
		labelMetaModel = new JLabel("UI model(s) to use:");

		fieldName = new JTextField();
		CaretListener caretupdate = new CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent e) {
                TestsSuiteManager.getSingleton().setName(fieldName.getText());
            }
        };
        fieldName.addCaretListener(caretupdate);
		fieldName.setText("Your test serie name");
        TestsSuiteManager.getSingleton().setName(fieldName.getText());
        
		fieldPath = new JTextField();
		fieldPath.setEditable(false);
		fieldPath.setText("click on browser and select the XML tests file");

		buttonBrowse = new JButton("Browse");
		buttonBrowse.addActionListener(this);
		buttonAddModel = new JButton("Add new");
		buttonAddModel.addActionListener(this);

		picArchitecture = new JLabel(Utils.getPicture("RecapArchitecturePourQtaste.png"));

		metamodelBox = Box.createVerticalBox();
		JScrollPane jscrlpBox = new JScrollPane(metamodelBox);
		jscrlpBox.setPreferredSize(new Dimension(140, 90));
		reloadMetamodelsList();

		//add elements to the panel
		this.add( labelName ,cc.xy(2,2));
		this.add( labelPath ,cc.xy(2,4));
		this.add( labelMetaModel ,cc.xy(2,6));
		this.add( fieldName ,cc.xyw(4,2,2));
		this.add( fieldPath ,cc.xyw(4,4,2));
		this.add( buttonBrowse ,cc.xy(7,4));
		this.add( buttonAddModel ,cc.xy(7,6));
		this.add( jscrlpBox ,cc.xywh(4,6,1,3,CellConstraints.FILL, CellConstraints.FILL));
		this.add( picArchitecture ,cc.xywh(4,10,2,1,CellConstraints.CENTER, CellConstraints.CENTER));
	}

	/**
	 * Rebuild the list of available metamodels
	 * @author jvanhec
	 */
	private void reloadMetamodelsList() {
		metamodelBox.removeAll();

		for (UIModel m : UIModelsManager.getSingleton().getAvailableUIModels()){
			JCheckBox cb = new JCheckBox(m.getName());
			cb.setSelected(m.isSelected());
			cb.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					Object source = evt.getSource();
					JCheckBox c = (JCheckBox) source;

					//update global context
					UIModelsManager.getSingleton().selectUIModel(c.getText(), c.isSelected());

					//refresh
					RefreshManager.getSingleton().refreshRegisteredElements(RefreshManager.RefreshPart.UIMODEL);
				}
			});
			metamodelBox.add(cb);
		}

		metamodelBox.revalidate();
		metamodelBox.repaint();
	}

	/**
	 * Actions performed on the GUI
	 * @author jvanhec
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		//Button "browse"
		if(source == buttonBrowse){
			JFileChooser c = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files", "xml");
			c.setFileFilter(filter);
			int rVal = c.showOpenDialog(this);
			if (rVal == JFileChooser.APPROVE_OPTION) {
				
				//update gui
				fieldPath.setText(c.getSelectedFile().getAbsolutePath());
				
				//load file
				int res = AbstractTestsManager.getSingleton().parseAbstractTests(c.getSelectedFile().getAbsolutePath());
				
				//show popup according to parsing result
				if (res ==1){
					fieldPath.setForeground(Color.GREEN);
					JOptionPane.showMessageDialog(this, "Abstract tests file parsed successfully.");
					RefreshManager.getSingleton().refreshRegisteredElements(RefreshManager.RefreshPart.ABSTRACTTESTFILE);
				}else if (res == 0){
					fieldPath.setForeground(Color.RED);
					JOptionPane.showMessageDialog(this,"Unexpected error while parsing tests file!", "Error while loading file", JOptionPane.ERROR_MESSAGE);
				}else if (res == -1){
					fieldPath.setForeground(Color.RED);
					JOptionPane.showMessageDialog(this,"XML error while parsing tests file!", "Error while loading file", JOptionPane.ERROR_MESSAGE);
				}else if (res == -2){
					JOptionPane.showMessageDialog(this,"The specified file doesn't exist!", "Error while loading file", JOptionPane.ERROR_MESSAGE);
				}else if (res == -3){
					fieldPath.setForeground(Color.RED);
					JOptionPane.showMessageDialog(this," Error in the XML structure. Tests must be enclosed in <test></test> balises!", "Error while loading file", JOptionPane.ERROR_MESSAGE);
				}else if (res == -4){
					fieldPath.setForeground(Color.RED);
					JOptionPane.showMessageDialog(this," Error in operations naming. Operations name must follow JAVA rules", "Error while loading file", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		//Button "add model"
		else if (source == buttonAddModel){
			
			//ask for the name
			String name = JOptionPane.showInputDialog("Enter new meta-model name");
			if (name ==null)
				return;
			
			//Create file
			UIModelsManager.getSingleton().createUIModel(name);
			UIModelsManager.getSingleton().refreshAvailableUIModels();
			reloadMetamodelsList();

			//refresh
			RefreshManager.getSingleton().refreshRegisteredElements(RefreshManager.RefreshPart.UIMODEL);
		}
	}

	/**
	 * On refresh
	 * @author jvanhec
	 */
	@Override
	public void refresh(RefreshManager.RefreshPart reason) {	
		reloadMetamodelsList();
		fieldName.setText(TestsSuiteManager.getSingleton().getName());
	}

	/**
	 * register for metamodels event
	 * @author jvanhec
	 */
	@Override
	public void register() {
		RefreshManager.getSingleton().register(this,RefreshManager.RefreshPart.IMPORT);
	}

}
