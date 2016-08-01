package be.modji.test.qtaste.addon.abscon.manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import be.modji.test.qtaste.addon.abscon.Utils;
import be.modji.test.qtaste.addon.abscon.type.AbstractAction;
import be.modji.test.qtaste.addon.abscon.type.AbstractAssert;
import be.modji.test.qtaste.addon.abscon.type.PythonMethod;

/**
 * This class manages operations (actions, asserts) of the common context
 * @author jvanhec
 */
public class OperationsManager {
	private List<PythonMethod> actions;
	private List<PythonMethod> asserts;
	static private OperationsManager instance;

	private enum TypeParsing{
		PARSE_ACTION,
		PARSE_ASSERT;
	}

	/**
	 * Private constructor
	 * @author jvanhec
	 */
	private OperationsManager(){
		actions = new LinkedList<PythonMethod>();
		asserts = new LinkedList<PythonMethod>();
	}

	/**
	 * Return a singleton of this class
	 * @author jvanhec
	 * @return a singleton
	 */
	static public OperationsManager getSingleton(){
		if (instance == null){
			instance = new OperationsManager();
		}
		return instance;
	}

	/**
	 * reset the actions list
	 * @author jvanhec
	 * @param actions
	 */
	public void defineActions(List<AbstractAction> actions){
		List<PythonMethod> newlist = new LinkedList<PythonMethod>();
		for (AbstractAction action : actions){
			PythonMethod method = new PythonMethod(action.getName());
			for (PythonMethod m : this.actions){
				if (m.getName().equals(action.getName()))
					method.setCode(m.getCode());
			}
			newlist.add(method);
		}
		this.actions = newlist;
	}

	/**
	 * reset the asserts list
	 * @author jvanhec
	 * @param actions
	 */
	public void defineAsserts(List<AbstractAssert> asserts){
		List<PythonMethod> newlist = new LinkedList<PythonMethod>();
		for (AbstractAssert assertion : asserts){
			PythonMethod method = new PythonMethod(assertion.getName());
			for (PythonMethod m : this.asserts){
				if (m.getName().equals(assertion.getName()))
					method.setCode(m.getCode());
			}
			newlist.add(method);
		}

		this.asserts = newlist;
	}

	/**
	 * Load code from file and update action/assert  existing in the global context
	 * @author jvanhec
	 * @param filePath
	 * @param parsing is the type of parsing (action or assert)
	 * @return an integer:
	 * 	1 : if parsing succeed
	 *  0 : if an error append during the file reading
	 *  -2: if the file pointed by 'filePath' is not a file
	 */
	private int loadOperationCodeFromFile(String filePath, TypeParsing parsing){
		String fileContent;
		PythonMethod currentMethod = null;
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
		String[] lines = fileContent.split("\n");
		for (String line : lines) {

			//replace 4 spaces by tabs
			line = line.replace("    " , "\t");

			//remove tabs that begin the line
			if (line.indexOf('\t') ==0){
				line = line.replaceFirst("\\t", "");
			}

			if (inComment == true && (line.startsWith("\"\"\"") || line.endsWith("\"\"\""))){
				inComment = false;

				if (currentMethod != null)
					currentMethod.appendCode(line);

				continue;
			}

			if (inComment == true){

				if (currentMethod != null)
					currentMethod.appendCode(line);

				continue;
			}

			if (line.startsWith("\"\"\"")){
				inComment = true;

				if (currentMethod != null)
					currentMethod.appendCode(line);

				continue;
			}

			if (line.startsWith("#")){

				if (currentMethod != null)
					currentMethod.appendCode(line);

				continue;
			}

			if (line.startsWith("class ")){
				currentMethod = null;
				continue;
			}

			if (line.startsWith("def ")){
				//reset curent method
				currentMethod = null;


				String[] methodName = line.replace("def ","").split("\\(", 2);
				if (methodName.length <= 1){
					System.out.println("error while parsing method : " + line);
					continue;
				}

				if (methodName[0].trim().equals("__init__")){
					continue;
				}

				if (parsing == TypeParsing.PARSE_ACTION){
					for (PythonMethod method : actions){
						if (method.getName().equals(methodName[0].trim())){
							currentMethod = method;
							currentMethod.setCode("");
							break;
						}
					}
				}else if (parsing == TypeParsing.PARSE_ASSERT){
					for (PythonMethod method : asserts){
						if (method.getName().equals(methodName[0].trim())){
							currentMethod = method;
							currentMethod.setCode("");
							break;
						}
					}
				}
				continue;
			}

			//code line
			if (currentMethod != null){
				currentMethod.appendCode(line);
			}
		}

		return 1;
	}

	/**
	 * Load code from file and update actions existing in the global context
	 * @author jvanhec
	 * @param filePath
	 * @return an integer:
	 * 	1 : if parsing succeed
	 *  0 : if an error append during the file reading
	 *  -2: if the file pointed by 'filePath' is not a file
	 */
	public int parseAbstractActions(String filePath){
		return loadOperationCodeFromFile(filePath, TypeParsing.PARSE_ACTION);
	}

	/**
	 * Load code from file and update asserts existing in the global context
	 * @author jvanhec
	 * @param filePath
	 * @return an integer:
	 * 	1 : if parsing succeed
	 *  0 : if an error append during the file reading
	 *  -2: if the file pointed by 'filePath' is not a file
	 */
	public int parseAbstractAsserts(String filePath){
		return loadOperationCodeFromFile(filePath, TypeParsing.PARSE_ASSERT);
	}

	/**
	 * procude copies of actions that are in the global context
	 * @author jvanhec
	 * @return copies of actions
	 */
	public List<PythonMethod> getActionMethods(){
		List<PythonMethod> copies = new LinkedList<PythonMethod>();
		for (PythonMethod method : actions){
			copies.add(method.copy());
		}
		
		Collections.sort(copies,new PythonMethodComparator());
		return copies;
	}

	/**
	 * procude copies of asserts that are in the global context
	 * @author jvanhec
	 * @return copies of asserts
	 */
	public List<PythonMethod> getAssertMethods(){
		List<PythonMethod> copies = new LinkedList<PythonMethod>();
		for (PythonMethod method : asserts){
			copies.add(method.copy());
		}

		Collections.sort(copies,new PythonMethodComparator());
		return copies;
	}

	/**
	 * Update a method in the global context
	 * @author jvanhec
	 * @param method
	 */
	public void updateMethodCode(PythonMethod method){
		if (method == null)
			return;

		for (PythonMethod p : actions){
			if (p.getName().equals(method.getName())){
				p.setCode(method.getCode());
				return;
			}
		}

		for (PythonMethod p : asserts){
			if (p.getName().equals(method.getName())){
				p.setCode(method.getCode());
				return;
			}
		}
	}

	/**
	 * Return the python code for all the operations 
	 * @author jvanhec
	 * @return the python code
	 */
	public String getPythonCode(){
		String output = ""
				+ "# encoding= utf-8\n"
				+ "#Imports\n"
				+ "from qtaste import *\n"
				+ "import " + Utils.guiMappingLibName + "\n\n"
				+ "#Actions definition\n";

		//actions code
		for (PythonMethod method : actions){
			output+= method.getPythonCode() + "\n";
		}

		//asserts code
		output += "\n\n#Asserts definition\n";
		for (PythonMethod method : asserts){
			output+= method.getPythonCode() + "\n";
		}

		return output;
	}

	class PythonMethodComparator implements Comparator<PythonMethod>{

		/**
		 * @author jvanhec
		 */
		@Override
		public int compare(PythonMethod arg0, PythonMethod arg1) {
			if(arg0.getName().compareTo(arg1.getName()) > 0){
				return 1;
			} else {
				return -1;
			}
		}
	}
}



