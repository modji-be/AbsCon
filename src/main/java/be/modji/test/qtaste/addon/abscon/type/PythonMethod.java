package be.modji.test.qtaste.addon.abscon.type;

import java.util.LinkedList;
import java.util.List;

import be.modji.test.qtaste.addon.abscon.Utils;
import be.modji.test.qtaste.addon.abscon.manager.UIMappingsManager;

/**
 * This class represent a python method
 * @author jvanhec
 *
 */
public class PythonMethod {

	protected String methodName,code;
	protected List<PythonParameter> parameters;
	
	/**
	 * Constructor
	 * @author jvanhec
	 * @param methodName
	 */
	public PythonMethod(String methodName){
		this.methodName = methodName;
		this.parameters = new LinkedList<PythonParameter>();
	}
	
	/**
	 * @author jvanhec
	 * @return the methode name
	 */
	public String getName(){
		return methodName;
	}
	
	/**
	 * Add a parameter to this method
	 * If the parameter named "self", it is ignored
	 * @author jvanhec
	 * @param parameter
	 */
	public void addParameter(PythonParameter parameter){
		if (parameter.getName().equals("self"))
			return;
		
		parameters.add(parameter.copy());
	}
	
	/**
	 * Produce a copy of parameters
	 * @author jvanhec
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
	 * Set the code of this method
	 * @author jvanhec
	 * @param code
	 */
	public void setCode(String code){
		this.code = code;
	}
	
	/**
	 * Append code to the code of this method
	 * @author jvanhec
	 * @param codeLine
	 */
	public void appendCode(String codeLine){
		if (this.code == null || this.code.isEmpty())
			this.code = codeLine;
		else
			this.code += "\n" + codeLine;
	}

	/**
	 * Return the code of this.
	 * If there is no code yet, return a pre-defined code
	 * @author jvanhec
	 * @return the code of this method
	 */
	public String getCode(){
		if (this.code ==null || this.code.isEmpty()){
			this.code = Utils.emptyMethodCode;
		}
		return this.code;
	}
	
	/**
	 * Produce a copy of this
	 * @author jvanhec
	 * @return a copy of this
	 */
	public PythonMethod copy(){
		PythonMethod copy = new PythonMethod(this.methodName);
		
		for(PythonParameter param : parameters){
			copy.addParameter(param.copy());
		}
		copy.code = this.code;
		return copy;
	}

	/**
	 * Produce the python code for this method
	 * @author jvanhec
	 * @return the python code for this methid
	 */
	public String getPythonCode(){
		String output = "def " + this.methodName + "():\n";
		output += formatPythonCode(getCode())+ "\n\n";
		return output;
	}
	
	/**
	 * Format the code with idents and add libreary references
	 * @author jvanhec
	 * @param code
	 * @return formatted code
	 */
	private String formatPythonCode(String code){
		String output = "";
		Boolean inComment = false;
		
		//parse lines
		String[] lines = code.split("\n");
		for (String line : lines) {
			
			//replace spaces by tabs
			line = line.replace("    ", "\t");
			
			if (inComment == true && (line.startsWith("\"\"\"") || line.endsWith("\"\"\""))){
				output+= "\t" + line + "\n";
				inComment = false;
				continue;
			}

			if (inComment == true){
				output+= "\t" + line + "\n";
				continue;
			}

			if (line.startsWith("\"\"\"")){
				output+= "\t" + line + "\n";
				inComment = true;
				continue;
			}

			if (line.startsWith("#")){
				output+= "\t" + line + "\n";
				continue;
			}

			if (line.startsWith("from ")){
				output+= "\t" + line + "\n";
				continue;
			}

			//remove library references if extis
			line = line.replaceAll( Utils.guiMappingLibName + ".","");
			
			for (UIMapping m : UIMappingsManager.getSingleton().getGuiMappings()){
				//add library references
				line = line.replaceAll(m.getName(), Utils.guiMappingLibName + "." + m.getName());
			}
			output += "\t" + line + "\n";
		}
		return output;
	}
}
