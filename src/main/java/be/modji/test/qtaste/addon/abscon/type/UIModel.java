package be.modji.test.qtaste.addon.abscon.type;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import be.modji.test.qtaste.addon.abscon.Utils;

/**
 * This class represent a UIModel of the GUI to test
 * @author jvanhec
 *
 */
public class UIModel {

	private String filePath, fileContent, modelName, newcode;
	private List<PythonClass> uimodelClasses;
	private boolean isSelected;

	/**
	 * Constructor
	 * @author jvanhec
	 * @param filePath of the UIModel file
	 * @param modelName
	 */
	public UIModel(String filePath, String modelName){
		this.isSelected = false;
		this.modelName = modelName;
		this.filePath = filePath;
		this.uimodelClasses = new LinkedList<PythonClass>();
		this.newcode = null;
	}

	/**
	 * Parse the UIModel file
	 * It fills the global "classes" list
	 * @author jvanhec
	 */
	private void parseDocument(){
		
		//read file
		try {
			fileContent= new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			fileContent = "";
			e.printStackTrace();
			return;
		}

		//reset list
		uimodelClasses = new LinkedList<PythonClass>();
		PythonClass currentClass = null;
		PythonMethod currentMethod = null;
		Boolean inComment = false;
		
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

			if (line.startsWith("class ")){
				String classname = line.replace("class ", "").replace(":", "").trim();
				if (!Utils.isAValidName(classname)){
					System.out.println(classname + " is not a valid UIModel class name");
					continue;
				}
				currentClass = new PythonClass(classname);
				uimodelClasses.add(currentClass);
				continue;
			}

			if (line.startsWith("def ")){
				if (currentClass == null){
					System.out.println("error while parsing method. Class not defined before. method: " + line);
					continue;
				}

				String[] methodName = line.replace("def ","").split("\\(", 2);
				if (methodName.length <= 1){
					System.out.println("error while parsing method : " + line);
					continue;
				}

				if (methodName[0].trim().equals("__init__")){
					currentMethod = new PythonConstructor(methodName[0].trim());
					currentClass.setConstructor((PythonConstructor)currentMethod);
				}else{

					if (!Utils.isAValidName((methodName[0].trim()))){
						System.out.println((methodName[0].trim() + " is not a valid UIModel method name"));
						continue;
					}
					currentMethod = new PythonMethod(methodName[0].trim());
					currentClass.addMethod(currentMethod);
				}
				String tmp = methodName[1];
				tmp = tmp.substring(0,tmp.lastIndexOf(')'));
				String[] parameters = tmp.split(",");
				for(String param : parameters){
					if (!Utils.isAValidName(param.trim())){
						System.out.println(param.trim() + " is not a valid UIModel parameter name");
						continue;
					}
					
					currentMethod.addParameter(new PythonParameter(param.trim()));
				}
			}
		}
		
		fileContent = null;
	}

	/**
	 * Ask to parse the UIModel
	 * Produce copies of classes of this UIModel
	 * @author jvanhec
	 * @return copy of classes
	 */
	public List<PythonClass> getClasses(){
		parseDocument();
		List<PythonClass> copies = new LinkedList<PythonClass>();
		for (PythonClass p : uimodelClasses){
			copies.add(p.copy());
		}

		Collections.sort(uimodelClasses,new PythonClassComparator());
		return this.uimodelClasses;
	}

	/**
	 * Mark as selected or not this
	 * @author jvanhec
	 * @param isSelected
	 */
	public void select(boolean isSelected){
		this.isSelected = isSelected;
	}

	/**
	 * @author jvanhec
	 * @return true if this is selected
	 */
	public boolean isSelected(){
		return this.isSelected;
	}

	/**
	 * @author jvanhec
	 * @return the name of the model
	 */
	public String getName(){
		return this.modelName;
	}

	/**
	 * @author jvanhec
	 * @return the path to the UIModel
	 */
	public String getPath(){
		return this.filePath;
	}

	/**
	 * Produce a copy of this
	 * @return a copy of this
	 */
	public UIModel copy(){
		UIModel copy = new UIModel(this.filePath,this.modelName);
		copy.isSelected = this.isSelected;
		copy.newcode = this.newcode;
		return copy;
	}

	/**
	 * @author jvanhec
	 * @return the code contained in the file or the modified code from the GUI if 
	 *         it has been modified
	 */
	public String getCode(){
		String code;
		
		//if code updated in GUI, return this code
		if (newcode != null && !newcode.isEmpty()){
			return newcode;
		}
		
		//return the code from file
		try {
			code= new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			code = "";
			e.printStackTrace();
		}
		return code;
	}

	/**
	 * Update the code of this
	 * @author jvanhec
	 * @param code
	 * @return false if an error append during file reading (for comparison 
	 * 		   with original code)
	 */
	public boolean setCode(String code){
		String originalCode;
		
		//if code already updated, we just update
		if (newcode != null && !newcode.isEmpty()){
			newcode = code;
			return true;
		}
		
		//if code never updated, check if there is change (avoid useless update)
		try {
			originalCode= new String(Files.readAllBytes(Paths.get(filePath)));
			if (originalCode!=null && !originalCode.equals(code)){
				newcode = code;
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return true;
		}
		
		return false;
	}

	/**
	 * @author jvanhec
	 * @param model
	 * @return true if model equals this. It doens't check the code
	 */
	public boolean equals(UIModel model){
		if (!this.filePath.equals(model.filePath))
			return false;
		if (!this.modelName.equals(model.modelName))
			return false;
		
		return true;
	}

	/**
	 * Reset the modified code
	 * @author jvanhec
	 */
	public void resetCode(){
		this.newcode = null;
	}

	/**
	 * Save the modified code to the UIModel file
	 * @author jvanhec
	 */
	public void save(){
		if (newcode == null || newcode.isEmpty())
			return;
		
		File fnew=new File(filePath);
		try {
		    FileWriter f = new FileWriter(fnew,false);
		    f.write(newcode);
		    f.close();
		} catch (IOException e) {
		    e.printStackTrace();
		} 
	}
	
	class PythonClassComparator implements Comparator<PythonClass>{

		/**
		 * @author jvanhec
		 */
		@Override
		public int compare(PythonClass arg0, PythonClass arg1) {
			if(arg0.getName().compareTo(arg1.getName()) > 0){
				return 1;
			} else {
				return -1;
			}
		}
	}
}
