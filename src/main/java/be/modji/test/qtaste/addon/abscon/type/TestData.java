package be.modji.test.qtaste.addon.abscon.type;

import java.util.LinkedList;
import java.util.List;

/**
 * This class represent a data used by QTaste in test execution
 * A data is defined by a name and contains nonde, one or several values
 * @author jvanhec
 *
 */
public class TestData {

	private String dataName;
	private List<String> dataValues;

	/**
	 * Constructor
	 * @author jvanhec
	 * @param dataName
	 */
	public TestData(String dataName){
		this.dataName = dataName;
		this.dataValues = new LinkedList<String>();
	}

	/**
	 * Add a value
	 * @author jvanhec
	 * @param value
	 */
	public void addValue(String value){
		this.dataValues.add(value);
	}

	/**
	 * Produce a copy of values
	 * @author jvanhec
	 * @return a copy of values
	 */
	public List<String> getDataValues(){
		List<String> copy = new LinkedList<String>();
		for (String s : dataValues){
			copy.add(s);
		}
		return copy;
	}

	/**
	 * @author jvanhec
	 * @return the amount of data values
	 */
	public int getDataValuesCount(){
		return this.dataValues.size();
	}

	/**
	 * @author jvanhec
	 * @param index
	 * @return return the values at a specific index
	 */
	public String getValue(int index){
		if (index>= this.dataValues.size())
			return null;	
		return this.dataValues.get(index);
	}

	/**
	 * @author jvanhec
	 * @return the name of this data
	 */
	public String getName(){
		return this.dataName;
	}

	/**
	 * Set the name of this data
	 * @author jvanhec
	 * @param name
	 */
	public void setName(String name){
		this.dataName = name;
	}

	/**
	 * Set a value to a specific index
	 * @author jvanhec
	 * @param value
	 * @param index
	 */
	public void setValue(String value, int index){
		//must update a value
		if (index<dataValues.size()){
			dataValues.set(index, value);
		}
		//must add a value
		else if (index== dataValues.size()){
			addValue(value);
		}
		//must add several values
		else{
			//fill with blank values
			for (int i = dataValues.size(); i < index ; i++){
				addValue("");
			}
			addValue(value);
		}
	}

	/**
	 * Produce a copy of this
	 * @author jvanhec
	 * @return a copy of this
	 */
	public TestData copy(){
		TestData copy = new TestData(this.dataName);
		for (String s : dataValues)
			copy.addValue(s);
		return copy;
	}
}
