package be.modji.test.qtaste.addon.abscon.type;

import java.util.LinkedList;
import java.util.List;

import be.modji.test.qtaste.addon.abscon.Utils;

/**
 * This class represent a concrete element of the GUI to test
 * @author jvanhec
 *
 */
public class UIMapping {

	private String name;
	private String uiModelClass,uiModelName;
	private List<PythonParameter> parameters;
	
	/**
	 * Constructor
	 * @author jvanhec
	 * @param uiModelName
	 * @param uiModelClass
	 * @param mappingName
	 * @param parameters
	 */
	public UIMapping(String uiModelName, String uiModelClass, String mappingName, List<PythonParameter> parameters){
		this.parameters = parameters;
		this.name = mappingName;
		this.uiModelName = uiModelName;
		this.uiModelClass = uiModelClass;
	}
	
	/**
	 * Produce a string with the content of this
	 * @author jvanhec
	 * @return a formatted string
	 */
	public String print(){
		String output = name + " = " +  uiModelName + "." + uiModelClass + "[";
		for(PythonParameter param : parameters){
			output += param.getName() + " : " + param.getValue() + " , ";
		}
		output = output.lastIndexOf(',') >= 0 ? output.substring(0,output.lastIndexOf(',')) : output;
		output += "]";
		return output;
	}
	
	/**
	 * Produce the python code for this mapping
	 * @author jvanhec
	 * @return the python code
	 */
	public String getPythonCode(){
		String output = name + " = " + Utils.metamodelPrefix + uiModelName + "." + uiModelClass + "(";
		for(PythonParameter param : parameters){
			output += param.getValue() + " , ";
		}
		output = output.lastIndexOf(',')  >= 0 ? output.substring(0,output.lastIndexOf(',')) : output;
		output += ")";
		return output;
	}

	/**
	 * @author jvanhec
	 * @return the name of this mapping
	 */
	public String getName(){
		return this.name;
	}

	/**
	 * Replace the current parameters list with the copy of given parameters
	 * @author jvanhec
	 * @param parameters
	 */
	public void setParameters(List<PythonParameter> parameters){
		this.parameters = new LinkedList<PythonParameter>();
		for (PythonParameter p : parameters){
			this.parameters.add(p.copy());
		}
	}

	/**
	 * Produce copies of parameters
	 * @return a copy of parameters
	 */
	public List<PythonParameter> getParameters(){
		List<PythonParameter> copies = new LinkedList<PythonParameter>();
		for (PythonParameter p : parameters){
			copies.add(p.copy());
		}
		return copies;
	}
	
	/**
	 * @author jvanhec
	 * @return the mapped metamodel name
	 */
	public String getMetamodelName(){
		return this.uiModelName;
	}
	
	/**
	 * @author jvanhec
	 * @return the mapped class name
	 */
	public String getClassName(){
		return this.uiModelClass;
	}

	/**
	 * Produce a copy of this
	 * @author jvanhec
	 * @return a copy of this
	 */
	public UIMapping copy(){
		UIMapping copy = new UIMapping(this.uiModelName, this.uiModelClass, this.name, new LinkedList<PythonParameter>());
		List<PythonParameter> parameterscopy = new LinkedList<PythonParameter>();
		for (PythonParameter p : parameters){
			parameterscopy.add(p.copy());
		}
		copy.setParameters(parameterscopy);
		return copy;
	}
}
