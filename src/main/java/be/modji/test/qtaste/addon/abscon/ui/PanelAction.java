package be.modji.test.qtaste.addon.abscon.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.StyledEditorKit;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.qspin.qtaste.util.Environment;

import be.modji.test.qtaste.addon.abscon.manager.OperationsManager;
import be.modji.test.qtaste.addon.abscon.manager.RefreshManager;
import be.modji.test.qtaste.addon.abscon.type.PythonMethod;

/**
 * JPanel for actions definition
 * must extends JPanel for validation
 * must extends FormDebugPanel for debug GUI
 * @author jvanhec
 *
 */
public class PanelAction extends JPanel implements ActionListener,RefreshableElement{

	private static final long serialVersionUID = 7972485783292491633L;
	protected JComboBox<String> ccbModeles;
	private JButton btLoad;
	private JLabel labelModele;
	private JScrollPane editorPane;
	private PythonMethod actionInEdition;
	private JEditorPane editor;
	
	/**
	 * Constructor
	 * @author jvanhec
	 */
	public PanelAction(){
		super();
		
		actionInEdition = null;
		editor = new JEditorPane();
		editorPane = new JScrollPane();
		ccbModeles = new JComboBox<String>();

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
				"20px, right:pref, 20px, pref:grow, 20px,100px, 20px",
				"20px, pref, 20px, 20px:grow, 20px"
				);
		this.setLayout(layout);

		//elements 
		labelModele = new JLabel( "Modify Action:" );

		ccbModeles = new JComboBox<String>();
		reloadActionCbb();
		
		btLoad = new JButton("Load");
		btLoad.addActionListener(this);

		jsyntaxpane.DefaultSyntaxKit.initKit();
		StyledEditorKit sek = new StyledEditorKit();
		editor = new JEditorPane();
		@SuppressWarnings("unused")
		JScrollPane scrPane = new JScrollPane(editor); //unused but needed, else the GUI crashed (?!)
		editor.setEditorKit(sek);
		editor.setContentType("text/python");   
		editor.setBackground(Color.white);
		editorPane = new JScrollPane();
		editorPane.getViewport().add(editor);

		//add elements to the panel
		this.add( labelModele ,cc.xy(2,2));
		this.add( ccbModeles ,cc.xyw(4,2,1));
		this.add( btLoad ,cc.xy(6,2));
		this.add( editorPane,cc.xywh(4,4,3,1,CellConstraints.FILL, CellConstraints.FILL));

	}

	/**
	 * Save the code of the action in edition in the global context
	 * @author jvanhec
	 */
	private void saveActionCode(){
		if (editor != null && editor.getText() != null && !editor.getText().isEmpty() && actionInEdition!=null){
			actionInEdition.setCode(editor.getText());
			OperationsManager.getSingleton().updateMethodCode(actionInEdition);
		}
	}

	/**
	 * Reload the combobox that contains actions
	 * @author jvanhec
	 */
	private void reloadActionCbb(){
		ccbModeles.removeAllItems();
		for (PythonMethod ac : OperationsManager.getSingleton().getActionMethods()){
			ccbModeles.addItem(ac.getName());

		}
		
		//select item
		if (ccbModeles.getItemCount()>=1){
			ccbModeles.setSelectedIndex(0);
			for (PythonMethod a : OperationsManager.getSingleton().getActionMethods()){
				if(a.getName().equals(ccbModeles.getSelectedItem())){
					actionInEdition = a.copy();
					editor.setText(actionInEdition.getCode());
				}
			}
		}
		
		//add listener
		ccbModeles.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				if (actionInEdition != null && actionInEdition.getName().equals(ccbModeles.getSelectedItem())){
					return;
				}
				saveActionCode();
				for (PythonMethod a : OperationsManager.getSingleton().getActionMethods()){
					if(a.getName().equals(ccbModeles.getSelectedItem())){
						actionInEdition = a.copy();
						editor.setText(actionInEdition.getCode());
					}
				}
			}
		});
		
		ccbModeles.revalidate();
		ccbModeles.repaint();
	}

	/**
	 * Actions performed on the GUI
	 * @author jvanhec
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		//button "Load"
		if (source == btLoad){
			JFileChooser c = new JFileChooser(Environment.getEnvironment().getMainFrame().getTestCasePanel().getTestSuiteDirectory());
			FileNameExtensionFilter filter = new FileNameExtensionFilter( "Python files", "py");
		    c.setFileFilter(filter);
			int rVal = c.showOpenDialog(this);
			if (rVal == JFileChooser.APPROVE_OPTION) {
				
				//to avoid saving the current code
				actionInEdition = null;
				
				//load file
				int res = OperationsManager.getSingleton().parseAbstractActions(c.getSelectedFile().getAbsolutePath());
				
				//show popup according to result of parsing
				if (res == 1){
					JOptionPane.showMessageDialog(this, "'Action' operations parsed successfully.");
					RefreshManager.getSingleton().refreshRegisteredElements(RefreshManager.RefreshPart.ABSTRACTTESTFILE);
				}
				else if (res == 0){
					JOptionPane.showMessageDialog(this,"Unexpected error while parsing 'action' operations!", "Error while loading file", JOptionPane.ERROR_MESSAGE);
				}
				else if (res == -2){
					JOptionPane.showMessageDialog(this,"Specified file doesn't exist!", "Error while loading file", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 * On refresh : Save the code and reload the combobox with actions
	 * @author jvanhec
	 */
	@Override
	public void refresh(RefreshManager.RefreshPart reason) {
		saveActionCode();
		reloadActionCbb();
	}

	/**
	 * Register to the refresh manager in category "abstract test file"
	 * @author jvanhec
	 */
	@Override
	public void register() {
		RefreshManager.getSingleton().register(this,RefreshManager.RefreshPart.ABSTRACTTESTFILE);
		RefreshManager.getSingleton().register(this, RefreshManager.RefreshPart.IMPORT);		
	}

}
