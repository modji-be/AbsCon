package be.modji.test.qtaste.addon.abscon.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import be.modji.test.qtaste.addon.abscon.exception.ConcretizerCreateFileException;
import be.modji.test.qtaste.addon.abscon.exception.ConcretizerCreateFolderException;
import be.modji.test.qtaste.addon.abscon.exception.ConcretizerRenameFolderException;
import be.modji.test.qtaste.addon.abscon.manager.RefreshManager;
import be.modji.test.qtaste.addon.abscon.manager.TestsSuiteManager;

/**
 * JPanel for generation of the test suite
 * must extends JPanel for validation
 * must extends FormDebugPanel for debug GUI
 * @author jvanhec
 *
 */
public class PanelQTasteTest extends JPanel implements ActionListener,RefreshableElement{

	private static final long serialVersionUID = 8628845157501509228L;
	private JLabel labelGenerate, labelLog;
	private JTextField fieldLog;
	private JButton btGenerate;

	/**
	 * Constructor
	 * @author jvanhec
	 */
	public PanelQTasteTest(){
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
				"20px, right:pref,20px,120px, 5px:grow, 20px", 
				"20px, pref, 20px, pref,20px, 5px:grow");
		this.setLayout(layout);

		//elements 
		labelGenerate = new JLabel( "Generate tests serie:" );

		btGenerate = new JButton("Generate");
		btGenerate.addActionListener(this);
		
		labelLog = new JLabel("Build message:");
		
		fieldLog = new JTextField();
		fieldLog.setEditable(false);
		fieldLog.setText("Build not started");
		fieldLog.setForeground(Color.BLACK);

		//add elements to the panel
		this.add( labelGenerate ,cc.xy(2,2));
		this.add( btGenerate ,cc.xy(4,2));
		this.add( labelLog ,cc.xy(2,4));
		this.add( fieldLog ,cc.xyw(4,4,2));	
	}

	/**
	 * Actions performed on the GUI
	 * @author jvanhec
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		//Button "generate"
		if(source == btGenerate){
			//Build and update the fieldLog according to result
			try{
				fieldLog.setText("Build . . .");
				fieldLog.setForeground(Color.BLACK);
				TestsSuiteManager.getSingleton().build();
				fieldLog.setText(String.format("Build succeed"));
				fieldLog.setForeground(Color.GREEN);
			}catch(ConcretizerCreateFileException e1){
				fieldLog.setText(String.format("Build failed! Error when create the file '%s'",e1.getMessage()));	
				fieldLog.setForeground(Color.RED);	
			} catch (ConcretizerCreateFolderException e2) {
				fieldLog.setText(String.format("Build failed! Error when create the folder '%s'",e2.getMessage()));
				fieldLog.setForeground(Color.RED);	
			} catch (ConcretizerRenameFolderException e3) {
				fieldLog.setText(String.format("Build failed! Error when renaming the folder '%s'. Check if it is not opened.",e3.getMessage()));
				fieldLog.setForeground(Color.RED);	
			}catch (Exception e4){
				fieldLog.setText(String.format("Build failed! An unexpected error append"));
				fieldLog.setForeground(Color.RED);	
				e4.printStackTrace();
			}
		}
	}

	/**
	 * On refresh: nothing
	 * @author jvanhec
	 */
	@Override
	public void refresh(RefreshManager.RefreshPart reason) {	
		//nothing
	}

	/**
	 * Don't register for any refresh
	 * @author jvanhec
	 */
	@Override
	public void register() {
		//nothing
	}

}
