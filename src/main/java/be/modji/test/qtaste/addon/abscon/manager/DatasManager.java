package be.modji.test.qtaste.addon.abscon.manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import be.modji.test.qtaste.addon.abscon.type.TestData;

/**
 * This class manages pieces of data of the common context
 * @author jvanhec
 */
public class DatasManager {
	private List<TestData> datas;
	static private DatasManager instance;

	/**
	 * Private constructor
	 * @author jvanhec
	 */
	private DatasManager(){
		datas = new LinkedList<TestData>();
	}

	/**
	 * Return a singleton of this class
	 * @author jvanhec
	 * @return a singleton
	 */
	static public DatasManager getSingleton(){
		if (instance == null){
			instance = new DatasManager();
		}
		return instance;
	}

	/**
	 * Produce a CSV formatted string that contains all pieces of data
	 * If a column contains less data, the las tone is duplicated
	 * @author jvanhec
	 * @return the CSV formatted string
	 */
	public String getCsvCode(){
		String output = "";
		int rowCount = 0;
		if (datas.size() <= 0) 
			return output;

		//Print title
		for (TestData data : datas){
			output+= data.getName() + ";";
		}
		output += "\n";

		//get rows count
		for (TestData data : datas){
			if (data.getDataValuesCount()>rowCount)
				rowCount = data.getDataValuesCount();
		}

		//print rows
		for (int i = 0; i< rowCount;i++){
			
			//check if this is an empty row
			boolean validRow = true;
			for (int j =0 ; j<datas.size();j++){
				if (datas.get(j).getValue(i)!= null && !datas.get(j).getValue(i).isEmpty())
					break;
				if (j == datas.size() -1)
					validRow = false;
			}
			
			if (!validRow)
				continue;
			
			for (TestData data : datas){
				String datavalue = "";
				//if the value is empty, take the last valid one
				if (data.getValue(i) == null || data.getValue(i).isEmpty()){
					for(int j = data.getDataValuesCount()-1 ; j>=0 ; j--){
						if (data.getValue(j) != null && !data.getValue(j).isEmpty()){
							datavalue = data.getValue(j);
							break;
						}
					}
				}else{
					datavalue = data.getValue(i);
				}
				output+= datavalue + ";";
			}
			output += "\n";
		}

		return output;
	}

	/**
	 * Produce a list with a copy of each data
	 * @author jvanhec
	 * @return copies of data
	 */
	public List<TestData> getDatas(){
		List<TestData> copies = new LinkedList<TestData>();

		for (TestData data : datas){
			copies.add(data.copy());
		}
		return copies;
	}

	/**
	 * Set the name of a given data
	 * @author jvanhec
	 * @param data
	 * @param name
	 */
	public void setDataName(TestData data, String name){
		for (TestData d : datas){
			if (d.getName().equals(data.getName())){
				d.setName(name);
				break;
			}
		}
	}

	/**
	 * Add a given data to the data set. If the data already exists, it will exit the function
	 * @author jvanhec
	 * @param data
	 */
	public void addData(TestData data){
		for (TestData d : datas){
			if (d.getName().equals(data.getName())){
				System.out.println("Error when adding data '" + data.getName() + "'. This name is already used");
				return;
			}
		}
		this.datas.add(data);
	}

	/**
	 * Add a value to a given data
	 * @author jvanhec
	 * @param data
	 * @param value
	 */
	public void addDataValue(TestData data, String value){
		for (TestData d : datas){
			if (d.getName().equals(data.getName())){
				d.addValue(value);
				return;
			}
		}
		System.out.println("Error when adding data value '" + data.getName() + "' - '" + value + "'. This name is not defined");
	}

	/**
	 * add a value at a given index to a given data
	 * @author jvanhec
	 * @param data
	 * @param value
	 * @param dataValueIndex
	 */
	public void addDataValue(TestData data, String value, int dataValueIndex){
		for (TestData d : datas){
			if (d.getName().equals(data.getName())){
				if (dataValueIndex>=d.getDataValuesCount()){
					d.addValue(value);
				}else{
					d.setValue(value, dataValueIndex);
				}
				return;
			}
		}
		System.out.println("Error when adding data value '" + data.getName() + "' - '" + value + "'. This name is not defined");
	}

	/**
	 * Add a value to all pieces of data
	 * @author jvanhec
	 * @param value
	 */
	public void addDataValue(String value){
		for (TestData d : datas){
			d.addValue(value);
		}
	}

	/**
	 * Clear the global "datas" list
	 * @author jvanhec
	 */
	public void eraseData(){
		datas = new LinkedList<TestData>();
	}

	/**
	 * Clear the "datas" list and parse the CSV file pointed 
	 * by "path" and then, update the new "datas" list.
	 * @param path to the file
	 * @return an integer:
	 *  1 : parsing success
	 *  0 : error when reading the file
	 *  -2: the path doesn't point a file
	 */
	public int parseTestDatas(String path){
		String fileContent;
		Boolean titlesDone = false;
		int columnCount = 0;
		
		//reset datas
		datas = new LinkedList<TestData>();

		//check file
		if (! new File(path).isFile())
			return -2;

		//read file
		try {
			fileContent= new String(Files.readAllBytes(Paths.get(path)));
		} catch (IOException e) {
			fileContent = "";
			e.printStackTrace();
			return 0;
		}

		//parse file
		String[] lines = fileContent.split("\\s*\n\\s*");
		for (String line : lines) {

			//check if this is a csv line
			if (line.split(";").length <= 1){
				continue;
			}

			//do title
			if (!titlesDone){
				for (int i=0; i< line.split(";").length;i++){
					datas.add(new TestData(line.split(";")[i].toUpperCase()));
					columnCount ++;
				}
				titlesDone = true;
				continue;
			}

			//do datas
			for(int i=0; i<columnCount ; i++){
				datas.get(i).addValue(line.split(";")[i]);
			}
		}
		return 1;
	}
}

