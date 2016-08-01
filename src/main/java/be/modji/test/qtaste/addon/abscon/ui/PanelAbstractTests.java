package be.modji.test.qtaste.addon.abscon.ui;

import java.awt.Color;
import java.awt.Component;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import be.modji.test.qtaste.addon.abscon.manager.AbstractTestsManager;
import be.modji.test.qtaste.addon.abscon.manager.RefreshManager;
import be.modji.test.qtaste.addon.abscon.type.AbstractTestOperation;

/**
 * JPanel for abstract tests presentation
 * must extends JPanel for validation
 * must extends FormDebugPanel for debug GUI
 * @author jvanhec
 *
 */
public class PanelAbstractTests extends JPanel implements RefreshableElement{

	private static final long serialVersionUID = 2179434921817351970L;
	private JScrollPane tablePane;
	private JTable testsTable;
	private TestTableData testsTableData;
	protected JComboBox<String> ccbTests;	
	private JLabel labelTests;



	/**
	 * Constructor
	 * @author jvanhec
	 */
	public PanelAbstractTests(){
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
				"20px, right:pref, 20px, 200px, 5px:grow, 20px",
				"20px,20px,20px, 50px:grow, 20px"
				);
		this.setLayout(layout);

		//elements 
		labelTests = new JLabel( "Abstract tests:" );

		ccbTests = new JComboBox<String>();

		testsTableData = new TestTableData();
		testsTable = new JTable(testsTableData)
		{
			private static final long serialVersionUID = 1352457681891847543L;
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
				Component c = super.prepareRenderer(renderer, row, column);
				if (testsTableData.isAction(row)){
					c.setBackground(new Color(214,186,230));
				}
				else if (testsTableData.isAssert(row)){
					c.setBackground(Color.white);
				}
				return c;
			}
		};
		tablePane = new JScrollPane(testsTable);
		tablePane.setColumnHeaderView(testsTable.getTableHeader());		

		reloadTestsCbb();
		refreshTableTests();

		//add elements to the panel
		this.add( labelTests ,cc.xy(2,2));
		this.add( ccbTests ,cc.xyw(4,2,1));
		this.add( tablePane ,cc.xywh(2,4,4,1, CellConstraints.FILL, CellConstraints.FILL));
	}

	/**
	 * Reload the tests combobox 
	 * @author jvanhec
	 */
	private void reloadTestsCbb(){
		ccbTests.removeAllItems();

		for (int i =0; i<AbstractTestsManager.getSingleton().getTestsCount(); i++){
			ccbTests.addItem("test_" + String.format("%06d", i));
		}

		//add listener
		ccbTests.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				refreshTableTests();
			}
		});

		//select element
		if (ccbTests.getItemCount()>=1){
			ccbTests.setSelectedIndex(0);
		}

		ccbTests.revalidate();
		ccbTests.repaint();
	}

	/**
	 * Update the table with test operations according to the combobox selection
	 * @author jvanhec
	 */
	private void refreshTableTests(){
		int selecteIndex = ccbTests.getSelectedIndex();

		if (selecteIndex < 0)
			return;

		if (AbstractTestsManager.getSingleton().getTest(selecteIndex) == null){
			System.out.println("error while loading abstract tests combobox");
			return;
		}

		List<AbstractTestOperation> operations = AbstractTestsManager.getSingleton().getTest(selecteIndex).getOperations();
		testsTableData.setOperations(operations);
	}

	/**
	 * On refresh 
	 * @author jvanhec
	 */
	@Override
	public void refresh(RefreshManager.RefreshPart reason) {
		reloadTestsCbb();
		refreshTableTests();
	}

	/**
	 * Register to the refresh manager in categories "abstract tests files" and "import"
	 * @author jvanhec
	 */
	@Override
	public void register() {
		RefreshManager.getSingleton().register(this,RefreshManager.RefreshPart.ABSTRACTTESTFILE);	
		RefreshManager.getSingleton().register(this, RefreshManager.RefreshPart.IMPORT);	
	}

	/**
	 * Custom type for the table tests
	 * @author jvanhec
	 *
	 */
	private class TestTableData extends AbstractTableModel {

		private static final long serialVersionUID = -9009838281066608351L;
		private List<AbstractTestOperation> operations;

		public TestTableData(){
			super();
			operations = new LinkedList<AbstractTestOperation>();
		}

		public void setOperations(List<AbstractTestOperation> operations){
			this.operations = operations;
			this.fireTableChanged(null);
		}

		public boolean isAssert(int rowIndex){
			if (operations.get(rowIndex).isAssert())
				return true;
			return false;
		}

		public boolean isAction(int rowIndex){
			if (operations.get(rowIndex).isAction())
				return true;
			return false;
		}
		
		@Override
		public String getColumnName(int colIndex) {
			switch(colIndex){
			case 0: return "Operation type";
			case 1: return "Operation name";
			default:return null;
			}
		}

		@Override
		public int getColumnCount() {
			return 2; 
		}

		@Override
		public int getRowCount() {
			return operations.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int colIndex) {
			switch (colIndex){
			case 0: 
				if (operations.get(rowIndex).isAction())
					return "Action";
				else if (operations.get(rowIndex).isAssert())
					return "Assert";
				else
					return "Unknown operation";
			case 1: 
				return operations.get(rowIndex).getName();
			default: 
				return null;
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int colIndex){ 
			return false;
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int colIndex) {
		}
	}


}
