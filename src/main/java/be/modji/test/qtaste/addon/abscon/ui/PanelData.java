package be.modji.test.qtaste.addon.abscon.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.qspin.qtaste.util.Environment;

import be.modji.test.qtaste.addon.abscon.manager.DatasManager;
import be.modji.test.qtaste.addon.abscon.manager.RefreshManager;
import be.modji.test.qtaste.addon.abscon.type.TestData;

/**
 * JPanel for datas definition
 * must extends JPanel for validation
 * must extends FormDebugPanel for debug GUI
 * @author jvanhec
 *
 */
public class PanelData extends JPanel implements ActionListener,RefreshableElement{

	private static final long serialVersionUID = 2781851678936273886L;
	private JLabel labelData;
	private JButton btAddRow, btAddCol, btLoad, btReset;
	private JScrollPane tablePane;
	private JTable tableForData;
	private DataTable dataTableData;

	/**
	 * Constructor
	 * @author jvanhec
	 */
	public PanelData(){
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
				"20px, right:pref,20px, 120px,20px,120px,20px,120px,20px,120px, pref:grow,20px",
				"20px, pref,20px, pref:grow,20px"
				);
		this.setLayout(layout);

		//elements 
		labelData = new JLabel( "Data definition:" );

		dataTableData = new DataTable();
		tableForData = new JTable(dataTableData)
		{
			private static final long serialVersionUID = 617175260656191762L;
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
				Component c = super.prepareRenderer(renderer, row, column);
				if (column ==0 || row == 0){
					c.setBackground(new Color(214,186,230));
				}
				else {
					c.setBackground(Color.white);
				}
				return c;
			}
		};
		tableForData.putClientProperty("terminateEditOnFocusLost", true);
		tablePane = new JScrollPane(tableForData, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tablePane.setColumnHeaderView(tableForData.getTableHeader());
		tableForData.getParent().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				if (tableForData.getPreferredSize().width < tableForData.getParent().getWidth()) {
					tableForData.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
				} else {
					tableForData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					tableForData.setMinimumSize(new Dimension(400,20));
				}
			}
		});

		btAddRow = new JButton("Add value");
		btAddRow.addActionListener(this);
		btAddCol = new JButton("Add data");
		btAddCol.addActionListener(this);
		btLoad = new JButton("Load");
		btLoad.addActionListener(this);
		btReset = new JButton("Reset");
		btReset.addActionListener(this);


		//add elements to the panel
		this.add( labelData ,cc.xy(2,2));
		this.add( btAddRow ,cc.xy(4,2));
		this.add( btAddCol ,cc.xy(6,2));
		this.add( btLoad ,cc.xy(8,2));
		this.add( btReset ,cc.xy(10,2));
		this.add( tablePane ,cc.xywh(4,4,8,1, CellConstraints.FILL, CellConstraints.FILL));
	}


	/**
	 * Reload the table that contains datas with global datas
	 * @author jvanhec
	 */
	private void refreshTableData(){
		dataTableData.setData(DatasManager.getSingleton().getDatas());
	}

	/**
	 * Actions performed on the GUI
	 * @author jvanhec
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		//Button "Load"
		if (source == btLoad){
			JFileChooser c = new JFileChooser(Environment.getEnvironment().getMainFrame().getTestCasePanel().getTestSuiteDirectory());
			FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
			c.setFileFilter(filter);
			int rVal = c.showOpenDialog(this);

			if (rVal == JFileChooser.APPROVE_OPTION) {

				//load file
				int res = DatasManager.getSingleton().parseTestDatas(c.getSelectedFile().getAbsolutePath());

				//show popup acording to parsing result
				if (res == 1){
					JOptionPane.showMessageDialog(this, "Data parsed successfully.");
					RefreshManager.getSingleton().refreshRegisteredElements(RefreshManager.RefreshPart.TESTDATA);
				}
				else if (res == 0){
					JOptionPane.showMessageDialog(this,"Unexpected error while opening file!", "Error while loading file", JOptionPane.ERROR_MESSAGE);
				}
				else if (res == -2){
					JOptionPane.showMessageDialog(this,"Specified file doesn't exist!", "Error while loading file", JOptionPane.ERROR_MESSAGE);
				}
			}
			//Button "add column"
		}else if (source == btAddCol){
			String name = JOptionPane.showInputDialog("What is the Data name?");
			DatasManager.getSingleton().addData(new TestData(name.toUpperCase()));
			RefreshManager.getSingleton().refreshRegisteredElements(RefreshManager.RefreshPart.TESTDATA);
		}
		//Button "add row"
		else if (source == btAddRow){
			DatasManager.getSingleton().addDataValue("");
			RefreshManager.getSingleton().refreshRegisteredElements(RefreshManager.RefreshPart.TESTDATA);
		}
		//Button "reset"
		else if (source == btReset){
			int res = JOptionPane.showConfirmDialog(this, "Are you sure? It will erease the full table!", "alert", JOptionPane.OK_CANCEL_OPTION);
			if (res == JOptionPane.OK_OPTION){
				DatasManager.getSingleton().eraseData();
				RefreshManager.getSingleton().refreshRegisteredElements(RefreshManager.RefreshPart.TESTDATA);
			}
		}
	}

	/**
	 * On refresh : Reload the table datas
	 * @author jvanhec
	 */
	@Override
	public void refresh(RefreshManager.RefreshPart reason) {
		refreshTableData();
	}

	/**
	 * Register to the refresh manager in category "test data"
	 * @author jvanhec
	 */
	@Override
	public void register() {
		RefreshManager.getSingleton().register(this,RefreshManager.RefreshPart.TESTDATA);	
		RefreshManager.getSingleton().register(this, RefreshManager.RefreshPart.IMPORT);	
	}

	/**
	 * Custom type for the table datas
	 * !!! There is a strong link between this type and the global context (Managers) !!!
	 * @author jvanhec
	 *
	 */
	private class DataTable extends AbstractTableModel {

		private static final long serialVersionUID = 3316912003707386310L;
		private List<TestData> datas;

		public DataTable(){
			super();
			datas = new LinkedList<TestData>();
		}

		public void setData(List<TestData> datas){
			this.datas = datas;
			this.fireTableChanged(null);
		}

		@SuppressWarnings("unused")
		public void addNewData(TestData data){
			this.datas.add(data);
			this.fireTableChanged(null);
		}

		@SuppressWarnings("unused")
		public void addNewValue(){
			if(this.datas.size() == 0){
				System.out.println("unable to add row, there is no data defined");
				return;
			}
			this.datas.get(0).addValue(null);
			this.fireTableChanged(null);
		}

		@Override
		public String getColumnName(int colIndex) {
			switch(colIndex){
			case 0: return "";
			default:return null;
			}
		}

		@SuppressWarnings("unused")
		public List<TestData> getData(){
			List<TestData> dataCopy = new LinkedList<TestData>();
			for (TestData t : datas){
				dataCopy.add(t.copy());
			}
			return dataCopy;
		}

		@Override
		public int getColumnCount() {
			return datas.size() + 1; 
		}

		@Override
		public int getRowCount() {
			int nb = 0;
			for (int i=0; i< datas.size(); i++){
				if (datas.get(i).getDataValuesCount() > nb)
					nb = datas.get(i).getDataValuesCount() ;
			}
			return nb+1;
		}

		@Override
		public Object getValueAt(int rowIndex, int colIndex) {
			if (colIndex ==0 && rowIndex ==0){
				return "Data : ";
			}else if (colIndex ==0){
				return "execution " + rowIndex;
			}else if (rowIndex == 0){
				return datas.get(colIndex-1).getName();
			}else{
				return datas.get(colIndex-1).getValue(rowIndex-1);
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int colIndex){ 
			if (colIndex ==0)
				return false;

			return true;
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int colIndex) {
			if (rowIndex == 0){
				DatasManager.getSingleton().setDataName(datas.get(colIndex-1), ((String)value).toUpperCase());
			}else{

				DatasManager.getSingleton().addDataValue(datas.get(colIndex-1), (String)value, rowIndex -1);
			}
			RefreshManager.getSingleton().refreshRegisteredElements(RefreshManager.RefreshPart.TESTDATA);
		}
	}


}
