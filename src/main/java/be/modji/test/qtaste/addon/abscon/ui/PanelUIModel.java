package be.modji.test.qtaste.addon.abscon.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.StyledEditorKit;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import be.modji.test.qtaste.addon.abscon.manager.UIModelsManager;
import be.modji.test.qtaste.addon.abscon.manager.RefreshManager;
import be.modji.test.qtaste.addon.abscon.type.UIModel;

/**
 * JPanel for UI model definition
 * must extends JPanel for validation
 * must extends FormDebugPanel for debug GUI
 * @author jvanhec
 *
 */
public class PanelUIModel extends JPanel implements ActionListener,RefreshableElement{

	private static final long serialVersionUID = -4678486592658598962L;
	protected JComboBox<String> ccbModeles;
	private JButton btSave, btReload;
	private JLabel labelModele;
	private JScrollPane editorPane;
	private UIModel uIModelInEdition;
	private JEditorPane editor;

	/**
	 * Constructor
	 * @author jvanhec
	 */
	public PanelUIModel(){
		super();

		uIModelInEdition = null;
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
				"20px, right:pref, 20px, pref:grow,20px,100px,20px,100px, 20px",
				"20px, pref, 20px, 20px:grow, 20px"
				);
		this.setLayout(layout);

		//elements 
		labelModele = new JLabel( "Modify UI model:" );

		ccbModeles = new JComboBox<String>();
		reloadUIModelCbb();

		btSave = new JButton("Save");
		btSave.addActionListener(this);

		btReload = new JButton("Reload");
		btReload.addActionListener(this);

		jsyntaxpane.DefaultSyntaxKit.initKit();
		StyledEditorKit sek = new StyledEditorKit();
		editor = new JEditorPane();
		@SuppressWarnings("unused")
		JScrollPane scrPane = new JScrollPane(editor);//unused but needed, else the GUI crashed (?!)
		editor.setEditorKit(sek);
		editor.setContentType("text/python");   
		editor.setBackground(Color.white);
		editorPane = new JScrollPane();
		editorPane.getViewport().add(editor);

		//add elements to the panel
		this.add( labelModele ,cc.xy(2,2));
		this.add( ccbModeles ,cc.xyw(4,2,1));
		this.add( btReload ,cc.xy(6,2));
		this.add( btSave ,cc.xy(8,2));
		this.add( editorPane,cc.xywh(4,4,5,1,CellConstraints.FILL, CellConstraints.FILL));
	}

	/**
	 * Save the code of the UImodem locally (ONLY)
	 * @author jvanhec
	 * @param model
	 */
	private void saveUiModelCode(UIModel model){
		if (editor != null && editor.getText() != null && uIModelInEdition!=null){
			UIModelsManager.getSingleton().updateUIModelCode(uIModelInEdition, editor.getText());
		}
	}

	/**
	 * Reload the available UIModel combobox
	 * @author jvanhec
	 */
	private void reloadUIModelCbb(){
		ccbModeles.removeAllItems();
		for (UIModel mm : UIModelsManager.getSingleton().getSelectedUIModels()){
			ccbModeles.addItem(mm.getName());
		}

		//select item
		if (ccbModeles.getItemCount()>=1){
			ccbModeles.setSelectedIndex(0);
			for (UIModel a : UIModelsManager.getSingleton().getSelectedUIModels()){
				if(a.getName().equals(ccbModeles.getSelectedItem())){
					uIModelInEdition = a;
					editor.setText(uIModelInEdition.getCode());
				}
			}
		}
		
		//Add listener
		ccbModeles.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveUiModelCode(uIModelInEdition);
				for (UIModel mm : UIModelsManager.getSingleton().getSelectedUIModels()){
					if(mm.getName().equals(ccbModeles.getSelectedItem())){
						uIModelInEdition = mm.copy();
						editor.setText(mm.getCode());
					}
				}
			}
		});
	}

	/**
	 * Actions performed on the UI
	 * @author jvanhec
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		//Button "reload"
		if (source == btReload){
			//reset code
			UIModelsManager.getSingleton().resetUIModelCode(uIModelInEdition);
			
			//avoid update code when refresh
			uIModelInEdition= null;
			
			RefreshManager.getSingleton().refreshRegisteredElements(RefreshManager.RefreshPart.UIMODEL);
		}
		//Button "save"
		else if (source == btSave){
			
			//Save code locally and in file
			saveUiModelCode(uIModelInEdition);
            UIModelsManager.getSingleton().saveUIModelCode(uIModelInEdition);		
            
			RefreshManager.getSingleton().refreshRegisteredElements(RefreshManager.RefreshPart.UIMODEL);
		}
	}

	/**
	 * On refresh: save the code locally and reload the combobox
	 * @author jvanhec
	 */
	@Override
	public void refresh(RefreshManager.RefreshPart reason) {
		saveUiModelCode(uIModelInEdition);
		reloadUIModelCbb();
	}
	
	/**
	 * Register to the refresh manager in category "UI model"
	 * @author jvanhec
	 */
	@Override
	public void register() {
		RefreshManager.getSingleton().register(this, RefreshManager.RefreshPart.UIMODEL);	
		RefreshManager.getSingleton().register(this, RefreshManager.RefreshPart.IMPORT);	
	}

}
