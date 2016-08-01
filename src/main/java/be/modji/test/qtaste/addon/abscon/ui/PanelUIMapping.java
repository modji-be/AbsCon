package be.modji.test.qtaste.addon.abscon.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.qspin.qtaste.util.Environment;

import be.modji.test.qtaste.addon.abscon.Utils;
import be.modji.test.qtaste.addon.abscon.manager.UIMappingsManager;
import be.modji.test.qtaste.addon.abscon.manager.UIModelsManager;
import be.modji.test.qtaste.addon.abscon.manager.RefreshManager;
import be.modji.test.qtaste.addon.abscon.type.UIMapping;
import be.modji.test.qtaste.addon.abscon.type.UIModel;
import be.modji.test.qtaste.addon.abscon.type.PythonClass;
import be.modji.test.qtaste.addon.abscon.type.PythonParameter;

/**
 * JPanel for GUI mapping definition
 * must extends JPanel for validation
 * must extends FormDebugPanel for debug GUI
 * @author jvanhec
 *
 */
public class PanelUIMapping extends JPanel implements ActionListener,RefreshableElement{

	private static final long serialVersionUID = -2173751160827904744L;
	protected JComboBox<String> ccbClasses;
	private JLabel labelElement, labelVariables;
	private JTextField fieldId;
	private JButton btDeclare,btLoad,btRemoveLast,btRemove;
	private JScrollPane tablePane, variablesPane;
	private JTable paramTable;
	private JTextArea variables;
	private JSeparator separator;
	private ParameterTable paramTableData;
	private File pythonLibDir;

	/**
	 * Constructor
	 * @author jvanhec
	 */
	public PanelUIMapping(){
		super();

		//create python libraries directory if not exists
		pythonLibDir = new File(Environment.getEnvironment().getMainFrame().getTestCasePanel().getTestSuiteDirectory() + "\\pythonlib\\");
		if (!pythonLibDir.exists()) {
			boolean result = false;

			try{
				pythonLibDir.mkdir();
				result = true;
			} 
			catch(SecurityException se){
				System.out.println("Error while trying to create python library directory!"); 
				return;
			}        
			if(result) 
				System.out.println("python library directory created");    
		}

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
				"20px, right:pref,20px, 200px,20px, pref:grow,20px, 120px, 20px",
				"20px, pref,20px,60px,20px ,20px,20px ,20px,30px,20px, 20px:grow,20px,20px,20px, 20px"
				);
		this.setLayout(layout);

		//elements 
		labelElement = new JLabel( "Add UI element:" );
		labelVariables = new JLabel("Defined variables:");

		fieldId = new JTextField();
		fieldId.setText("uniqueId");

		btDeclare = new JButton("Declare");
		btDeclare.setToolTipText("declare the mapping with the given name & class");
		btDeclare.addActionListener(this);


		btRemove = new JButton("Remove");
		btRemove.setToolTipText("delete the mapping with the given name");
		btRemove.addActionListener(this);

		btLoad = new JButton("Load");
		btLoad.addActionListener(this);

		btRemoveLast = new JButton("Remove last");
		btRemoveLast.addActionListener(this);

		ccbClasses = new JComboBox<String>();

		paramTableData = new ParameterTable();
		paramTable = new JTable(paramTableData)
		{
			private static final long serialVersionUID = 3578347835027240825L;

			public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
				Component c = super.prepareRenderer(renderer, row, column);
				if (column ==0){
					c.setBackground(new Color(214,186,230));
				}
				else {
					c.setBackground(Color.white);
				}
				return c;
			}
		};
		refreshTableParameters();
		tablePane = new JScrollPane(paramTable);
		tablePane.setColumnHeaderView(paramTable.getTableHeader());

		variables = new JTextArea();
		variables.setEditable(false);
		variablesPane = new JScrollPane(variables);


		separator = new JSeparator(SwingConstants.HORIZONTAL);
		separator.setBackground(Color.white);

		reloadClassesCbb();

		//add elements to the panel
		this.add( labelElement ,cc.xy(2,2));
		this.add( ccbClasses ,cc.xyw(4,2,1));
		this.add( fieldId ,cc.xyw(6,2,1));
		this.add( tablePane ,cc.xywh(4,4,3,4, CellConstraints.FILL, CellConstraints.FILL));
		this.add( btRemove ,cc.xy(8,5,CellConstraints.FILL, CellConstraints.BOTTOM));
		this.add( btDeclare ,cc.xy(8,7,CellConstraints.FILL, CellConstraints.BOTTOM));
		this.add( separator,cc.xywh(2,9,7,1));
		this.add( labelVariables ,cc.xy(2,11,CellConstraints.RIGHT, CellConstraints.TOP));
		this.add( variablesPane,cc.xywh(4,11,3,4,CellConstraints.FILL, CellConstraints.FILL));
		this.add( btLoad ,cc.xy(8,12));
		this.add( btRemoveLast ,cc.xy(8,14));
	}

	/**
	 * Reload the metamodels classes combobox and change the enable status of the 
	 * 'declare' button
	 * @author jvanhec
	 */
	private void reloadClassesCbb(){
		ccbClasses.removeAllItems();
		btDeclare.setEnabled(false);

		for (UIModel m : UIModelsManager.getSingleton().getSelectedUIModels()){
			for (PythonClass pc : m.getClasses()){
				ccbClasses.addItem(m.getName() + "." + pc.getName());
			}
		}

		//add listener
		ccbClasses.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				refreshTableParameters();
			}
		});

		//select element
		if (ccbClasses.getItemCount()>=1){
			ccbClasses.setSelectedIndex(0);
			btDeclare.setEnabled(true);
		}

		ccbClasses.revalidate();
		ccbClasses.repaint();

		refreshTableParameters();
	}

	/**
	 * Update the table with parameters according to the combobox selection
	 * @author jvanhec
	 */
	private void refreshTableParameters(){
		List<PythonParameter> params = new LinkedList<PythonParameter>();
		String metamodelSelected="";
		String classSelected = "";
		String selectedValue = ((String)ccbClasses.getSelectedItem());
		boolean found = false;

		//find the parameters for the selected class (constructor)
		for (UIModel m : UIModelsManager.getSingleton().getSelectedUIModels()){
			for (PythonClass pc : m.getClasses()){

				//combobox content must follows the format "metamodel.class". Else, we skip.
				if ( selectedValue!= null && selectedValue.split("\\.").length >= 2){
					metamodelSelected = selectedValue.split("\\.")[0];
					classSelected = selectedValue.split("\\.")[1];

					if (m.getName().equals(metamodelSelected) && pc.getName().equals(classSelected)){
						for (PythonParameter pp : pc.getConstructor().getParameters()){
							params.add(pp);
						}
						found = true;
						break;
					}
				}
			}
			if (found) 
				break;
		}

		paramTableData.setData(params);
	}

	/**
	 * Actions performed on the GUI
	 * @author jvanhec
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		//Button "declare"
		if (source == btDeclare){
			
			//combobox content must follows the format "metamodel.class". Else, we skip.
			if (((String)ccbClasses.getSelectedItem()).split("\\.").length>2)
				return;
			
			//check if given name is a valid vairable name (if it fit for java, it'll fit for python)
			if (!Utils.isAValidName(fieldId.getText().trim())){
				JOptionPane.showMessageDialog(this,"Please give a valid variable name (no space, begin with letter, no special character)", "Cannot declare mapping", JOptionPane.ERROR_MESSAGE);
				return;
			}

			//declare a new mapping but check if parameters have been filled
			if (!paramTableData.checkData()){
				JOptionPane.showMessageDialog(this,"Please give a value to all parameters", "Cannot declare mapping", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			//declare new
			UIMapping v = new UIMapping(
					((String)ccbClasses.getSelectedItem()).split("\\.")[0],
					((String)ccbClasses.getSelectedItem()).split("\\.")[1], 
					fieldId.getText().trim(), 
					paramTableData.getData()
					);
			if (!UIMappingsManager.getSingleton().addVariable(v)){
				JOptionPane.showMessageDialog(this,"This name is already used with another class/UImodel name", "Cannot declare mapping", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			RefreshManager.getSingleton().refreshRegisteredElements(RefreshManager.RefreshPart.UIMAPPING);
			paramTableData.clearValues();
		}
		//Button "remove"
		else if (source == btRemove){
			//check if given name is a valid vairable name (if it fit for java, it'll fit for python)
			if (!Utils.isAValidName(fieldId.getText().trim())){
				JOptionPane.showMessageDialog(this,"Please give a valid variable name (no space, begin with letter, no special character)", "Cannot declare mapping", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			UIMappingsManager.getSingleton().removeVariable(fieldId.getText().trim());
			RefreshManager.getSingleton().refreshRegisteredElements(RefreshManager.RefreshPart.UIMAPPING);
			paramTableData.clearValues();
		}	
		//Button "load"
		else if (source == btLoad){
			JFileChooser c = new JFileChooser(Environment.getEnvironment().getMainFrame().getTestCasePanel().getTestSuiteDirectory());
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Python files", "py");
			c.setFileFilter(filter);
			int rVal = c.showOpenDialog(this);

			if (rVal == JFileChooser.APPROVE_OPTION) {
				
				//parse file
				int res = UIMappingsManager.getSingleton().parseUIMapping(c.getSelectedFile().getAbsolutePath());
				
				//display popup according to the parsing result
				if (res == 1){
					JOptionPane.showMessageDialog(this, "UI mapping parsed successfully.");
					RefreshManager.getSingleton().refreshRegisteredElements(RefreshManager.RefreshPart.UIMAPPING);
				}
				else if (res == 0){
					JOptionPane.showMessageDialog(this,"Unexpected error while parsing UI mapping!", "Error while loading file", JOptionPane.ERROR_MESSAGE);
				}
				else if (res == -2){
					JOptionPane.showMessageDialog(this,"Specified file doesn't exist!", "Error while loading file", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		//Button "remove last"
		else if (source == btRemoveLast){
			UIMappingsManager.getSingleton().removeLastVariable();
			RefreshManager.getSingleton().refreshRegisteredElements(RefreshManager.RefreshPart.UIMAPPING);
		}
	}

	/**
	 * On refresh metamodel: reload the comboboc
	 * On refresh gui mapping: reload the variables text area
	 * @author jvanhec
	 */
	@Override
	public void refresh(RefreshManager.RefreshPart reason) {
		if (reason == RefreshManager.RefreshPart.UIMODEL)
			reloadClassesCbb();
		else if (reason == RefreshManager.RefreshPart.UIMAPPING)
			variables.setText(UIMappingsManager.getSingleton().print());
		else if (reason == RefreshManager.RefreshPart.IMPORT){
			reloadClassesCbb();
			variables.setText(UIMappingsManager.getSingleton().print());
		}
	}
	
	/**
	 * Register to the refresh manager in categories "metamodel" and "gui mapping"
	 * @author jvanhec
	 */
	@Override
	public void register() {
		RefreshManager.getSingleton().register(this,RefreshManager.RefreshPart.UIMODEL);	
		RefreshManager.getSingleton().register(this,RefreshManager.RefreshPart.UIMAPPING);	
		RefreshManager.getSingleton().register(this, RefreshManager.RefreshPart.IMPORT);	
	}

	/**
	 * Custom type for the table parameters
	 * @author jvanhec
	 *
	 */
	private class ParameterTable extends AbstractTableModel {

		private static final long serialVersionUID = -5172099070240373451L;
		private List<PythonParameter> parameters;

		public ParameterTable(){
			super();
		}

		public void setData(List<PythonParameter> parameters){
			this.parameters = parameters;
			this.fireTableChanged(null);
		}

		@Override
		public String getColumnName(int colIndex) {
			switch(colIndex){
			case 0: return "Parameter";
			case 1: return "Value";
			default:return null;
			}
		}

		public List<PythonParameter> getData(){
			List<PythonParameter> paramCopy = new LinkedList<PythonParameter>();
			for (PythonParameter p : parameters){
				paramCopy.add(p.copy());
			}
			return paramCopy;
		}
		
		public boolean checkData(){
			boolean check = true;
			for (PythonParameter p : parameters){
				if (p.getValue() == null || p.getValue().isEmpty()){
					check = false;
					break;
				}
			}
			return check;
		}

		public void clearValues(){
			for (PythonParameter pp : parameters){
				pp.setValue(null);
			}
			this.fireTableChanged(null);
		}

		@Override
		public int getColumnCount() {
			return 2; 
		}

		@Override
		public int getRowCount() {
			return parameters.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int colIndex) {
			switch (colIndex){
			case 0: return parameters.get(rowIndex).getName();
			case 1: return parameters.get(rowIndex).getValue();
			default: return null;
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int colIndex){ 
			switch (colIndex){
			case 0: return false;
			case 1: return true;
			default: return false;
			} 
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int colIndex) {
			PythonParameter modifiedParam = new PythonParameter(parameters.get(rowIndex).getName());
			modifiedParam.setValue((String)value);
			parameters.set(rowIndex, modifiedParam);
			this.fireTableCellUpdated(rowIndex, colIndex);
		}
	}


}
