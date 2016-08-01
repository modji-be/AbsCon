package be.modji.test.qtaste.addon.abscon.type;

/**
 * this class represent a parameter of a python method
 * @author jvanhec
 *
 */
public class PythonParameter {

	protected String parameterName;
	protected String value;
	
	/**
	 * Constructor
	 * @author jvanhec
	 * @param parameterName
	 */
	public PythonParameter(String parameterName){
		this.parameterName = parameterName;
		this.value = "";
	}
	
	/**
	 * @author jvanhec
	 * @return the name of the parameter
	 */
	public String getName(){
		return parameterName;
	}
	
	/**
	 * Set the value of the parameter
	 * @author jvanhec
	 * @param value
	 */
	public void setValue(String value){
		this.value = value;
	}
	
	/**
	 * @author jvanhec
	 * @return the value of this parameter
	 */
	public String getValue(){
		return this.value;
	}
	
	/**
	 * Produce a copy of this
	 * @author jvanhec
	 * @return a copy of this
	 */
	public PythonParameter copy(){
		PythonParameter copy = new PythonParameter(this.parameterName);
		copy.value = this.value;
		return copy;
	}
}
