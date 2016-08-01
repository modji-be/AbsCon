package be.modji.test.qtaste.addon.abscon.manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import be.modji.test.qtaste.addon.abscon.Utils;
import be.modji.test.qtaste.addon.abscon.type.UIMapping;
import be.modji.test.qtaste.addon.abscon.type.UIModel;
import be.modji.test.qtaste.addon.abscon.type.PythonClass;
import be.modji.test.qtaste.addon.abscon.type.PythonParameter;

/**
 * This class manages GUI mappings of the common context
 * @author jvanhec
 */
public class UIMappingsManager {
	private List<UIMapping> variables;
	static private UIMappingsManager instance;

	/**
	 * Private constructor
	 * @author jvanhec
	 */
	private UIMappingsManager(){
		variables = new LinkedList<UIMapping>();
	}

	/**
	 * Return a singleton of this class
	 * @author jvanhec
	 * @return a singleton
	 */
	static public UIMappingsManager getSingleton(){
		if (instance == null){
			instance = new UIMappingsManager();
		}
		return instance;
	}

	/**
	 * Add a GUI mapping to the global list "variables"
	 * If the mapping name already exists but with others types, it exit the function by returning false
	 * If the mapping name already exists with same types, it update the mapping
	 * @author jvanhec
	 * @param variable
	 * @return true if the variable has been added/updated
	 */
	public boolean addVariable(UIMapping variable){
		for (UIMapping v : variables){
			if (v.getName().equals(variable.getName())){
				if (v.getMetamodelName().equals(variable.getMetamodelName()) && v.getClassName().equals(variable.getClassName())){

					//we update the variable values
					v.setParameters(variable.getParameters());
					System.out.println("the variable mapping '" + variable.getName() +
							"' is already used with the same type. Update the parameters values.");
					return true;
				}else{
					//another variable use this name but is another type
					System.out.println("the variable mapping '" + variable.getMetamodelName() + "." + 
							variable.getClassName() + "." + variable.getName() + "' is already used with another type ('" +
							v.getMetamodelName()+"."+ v.getClassName() + "')!");
					return false;
				}
			}
		}
		variables.add(variable);
		return true;
	}

	/**
	 * Remove a variable from the global list "variables"
	 * @author jvanhec
	 * @param variable
	 */
	public void removeVariable(UIMapping variable){
		for (int i=0; i< variables.size() ; i++){
			if (variables.get(i).getName().equals(variable.getName())){
				variables.remove(i);
				break;
			}		
		}
	}

	/**
	 * Remove a variable from the global list "variables"
	 * @author jvanhec
	 * @param variable
	 */
	public void removeVariable(String variableName){
		for (int i=0; i< variables.size() ; i++){
			if (variables.get(i).getName().equals(variableName)){
				variables.remove(i);
				break;
			}		
		}
	}

	/**
	 * Remove the last variable of the list "variables"
	 * @author jvanhec
	 */
	public void removeLastVariable(){
		if (variables.size() == 0)
			return;
		variables.remove(variables.size() - 1);
	}

	/**
	 * Produce a string with a summary of all variables
	 * @author jvanhec
	 * @return the formatted string
	 */
	public String print(){
		String output = "";
		for (UIMapping v : variables){
			output += v.print() + "\n";
		}
		return output;
	}

	/**
	 * Load mappings contained in a given file under the .py format
	 * It update the current "variables" global list
	 * @author jvanhec
	 * @param filePath
	 * @return an integer:
	 *  1 : if parsing succeed
	 *  0 : if an error append during the file reading
	 *  -2: if the file pointed by "filePath" is not a file
	 */
	public int parseUIMapping(String filePath){
		String fileContent;
		Pattern pattern;
		Matcher matcher;
		Boolean inComment = false;

		//check file
		if (! new File(filePath).isFile())
			return -2;

		//read file
		try {
			fileContent= new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}

		//parse file
		String[] lines = fileContent.split("\\s*\n\\s*");
		for (String line : lines) {

			if (inComment == true && (line.startsWith("\"\"\"") || line.endsWith("\"\"\""))){
				inComment = false;
				continue;
			}

			if (inComment == true){
				continue;
			}

			if (line.startsWith("\"\"\"")){
				inComment = true;
				continue;
			}

			if (line.startsWith("#")){
				continue;
			}

			if (line.startsWith("from ")){
				continue;
			}
			if (line.startsWith("import ")){
				continue;
			}


			//match only guimapping python lines
			pattern = Pattern.compile("([a-zA-Z_0-9]*)[ \t\n\f\r]*[=][ \t\n\f\r]*([a-zA-Z_0-9]*)[.]([a-zA-Z_0-9]*)[ \t\n\f\r]*[(](.*)[)][ \t\n\f\r]*");
			matcher = pattern.matcher(line);

			if (matcher.matches()){
				if (matcher.groupCount() < 4){
					System.out.println("not enough group in the regex (" + matcher.groupCount() + ")");
					continue;
				}

				//get the expected parameters of the parsed class/constructor. The metamodel used in the file must be selected as well in the Addon GUI
				List<PythonParameter> parameters = getClassConstructorParameter(matcher.group(2).replace(Utils.metamodelPrefix, ""),matcher.group(3));
				if (parameters == null ){
					System.out.println("Didn't find constructor parameter for " + matcher.group(2) + "." + matcher.group(3));
					continue;
				}

				//check the parameters amount
				if (matcher.group(4).contains(",") && matcher.group(4).split(",").length >  parameters.size()){
					System.out.println(matcher.group(2) + "." + matcher.group(3) + "has " + parameters.size() + " parameter(s) and " +  matcher.group(4).split(",").length + " parameters are defined!");
					continue;
				}

				//parse parameters if there are
				if (matcher.group(4).contains(",")){
					//set parameters values
					for (int i=0 ; i< matcher.group(4).split(",").length; i++){
						parameters.get(i).setValue(matcher.group(4).split(",")[i]);
					}
				}
				
				UIMapping m = new UIMapping(matcher.group(2).replace(Utils.metamodelPrefix, ""), matcher.group(3), matcher.group(1), parameters);
				addVariable(m);				
			}
			continue;
		}

		return 1;
	}

	/**
	 * Given a metamodel name and a class name, it returns a copy of the parameters expected
	 * for the constructor
	 * @author jvanhec
	 * @param metamodelName
	 * @param className
	 * @return a copy of the constructor parameters. Null if not found
	 */
	private List<PythonParameter> getClassConstructorParameter(String metamodelName, String className){
		for (UIModel m : UIModelsManager.getSingleton().getSelectedUIModels()){
			if (!m.getName().equals(metamodelName))
				continue;
			for (PythonClass c : m.getClasses()){
				if (!c.getName().equals(className))
					continue;
				return c.getConstructor().getParameters();
			}
		}
		return null;
	}

	/**
	 * Produce a copy of the mappings list
	 * @author jvanhec
	 * @return a copy of the global list "variables"
	 */
	public List<UIMapping> getGuiMappings(){
		List<UIMapping> copies = new LinkedList<UIMapping>();
		for (int i=0; i< variables.size() ; i++){
			copies.add(variables.get(i).copy());
		}
		return copies;
	}

	/**
	 * produce the python code for the mapping
	 * @author jvanhec
	 * @return a string that contains the python code
	 */
	public String getPythonCode(){
		String output = ""
				+ "# encoding= utf-8\n"
				+ "#Imports\n";

		//import of metamodels
		for (UIModel m : UIModelsManager.getSingleton().getSelectedUIModels()){
			output += "import " + Utils.metamodelPrefix + m.getName() + "\n";
		}

		//mapping
		output+= "\n#mapping definition\n";
		for (UIMapping m : variables){
			output += m.getPythonCode() + "\n";
		}

		return output;
	}
}
