package be.modji.test.qtaste.addon.abscon.manager;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.qspin.qtaste.util.Environment;

import be.modji.test.qtaste.addon.abscon.Utils;
import be.modji.test.qtaste.addon.abscon.type.UIModel;

/**
 * This class manages UIModels of the common context
 * @author jvanhec
 */
public class UIModelsManager {

	static private UIModelsManager instance;
	private File modelsDir;
	private List<UIModel> availableModels;

	/** 
	 * Private constructor
	 * @author jvanhec
	 */
	private UIModelsManager(){

		//init var
		availableModels = new LinkedList<UIModel>();

		//create python libraries directory if not exists
		modelsDir = new File(Environment.getEnvironment().getMainFrame().getTestCasePanel().getTestSuiteDirectory() + "\\UIModels");
		if (!modelsDir.exists()) {
			boolean result = false;

			try{
				modelsDir.mkdir();
				result = true;
			} 
			catch(SecurityException se){
				System.out.println("Error while trying to create UIModels directory!"); 
				return;
			}        
			if(result) 
				System.out.println("UI Models directory created");    
		}
		refreshAvailableUIModels();

	}

	/**
	 * Return a singleton of this class
	 * @author jvanhec
	 * @return a singleton
	 */
	static public UIModelsManager getSingleton(){
		if (instance == null){
			instance = new UIModelsManager();
		}
		return instance;
	}

	/**
	 * Parse the UIModels directory and refresh the global list of metamodels
	 * UIModels must follow the name structure "uimodel_" + name + ".py"
	 * It reset the global lists before filling it
	 * @author jvanhec
	 */
	public void refreshAvailableUIModels(){
		String filePath, fileName,modelName;
		UIModel model;

		//reset list
		availableModels = new LinkedList<UIModel>();

		//loop in the directory that's supposed to contain the models
		File[] directoryListing = modelsDir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				filePath = child.getAbsolutePath();
				fileName = child.getName();  

				//Look only for following template: metamodel_"modelname".py
				if (fileName==null || !fileName.contains(Utils.metamodelPrefix)  
						|| !filePath.contains(".") 
						|| !filePath.substring(filePath.lastIndexOf('.'),filePath.length()).equals(".py"))
					continue;

				//add to the model list
				modelName = child.getName().replace(Utils.metamodelPrefix, "").replace(".py", "");
				model = new UIModel(filePath,modelName);
				availableModels.add(model);
			}
		} 
	}

	/**
	 * Produce copies of each available UIModels for the addon
	 * @author jvanhec
	 * @return a copiy of all UIModels found
	 */
	public List<UIModel> getAvailableUIModels(){
		List<UIModel> modelsCopy = new LinkedList<UIModel>();
		for (UIModel m : availableModels){
			modelsCopy.add(m.copy());
		}
		return modelsCopy;
	}

	/**
	 * Produce copies of selected UIModels in the GUI of the addon
	 * @author jvanhec
	 * @return a copiy of all UIModels selected
	 */
	public List<UIModel> getSelectedUIModels(){
		List<UIModel> selectedMMCopies = new LinkedList<UIModel>();
		for (UIModel m : availableModels){
			if (m.isSelected())
				selectedMMCopies.add(m.copy());
		}
		return selectedMMCopies;
	}

	/**
	 * Mark a UIModel as selected or not in the global context
	 * @author jvanhec
	 * @param modelName
	 * @param isSelected
	 */
	public void selectUIModel(String modelName,boolean isSelected){
		for (UIModel m : availableModels){
			if (m.getName().equals(modelName)){
				m.select(isSelected);
				break;
			}
		}
	}

	/**
	 * Create a new file for a new UIModel
	 * If an error append, it just exit the function
	 * @author jvanhec
	 * @param modelName
	 */
	public void createUIModel(String modelName){
		modelName = modelName.replace(".py", "").replace(Utils.metamodelPrefix, "");
		String path = modelsDir.getAbsolutePath() + "\\" + Utils.metamodelPrefix + modelName + ".py";
		File f = new File(path);

		//check if this name is free
		if (f.exists()){
			System.out.println("model already exists!");   
			return;
		}

		//create the file
		try {
			f.createNewFile();
		} catch (IOException e1) {
			System.out.println("error while creating file: " + modelName);    
			return;
		}
	}

	/**
	 * Update the local code for a given UIModel
	 * @author jvanhec
	 * @param model
	 * @param code
	 */
	public void updateUIModelCode(UIModel model, String code){
		for (UIModel m : availableModels){
			if (m.equals(model)){
				m.setCode(code);
			}		
		}
	}

	/**
	 * Reset the local code for a given UIModel
	 * @author jvanhec
	 * @param model
	 */
	public void resetUIModelCode(UIModel model){
		for (UIModel m : availableModels){
			if (m.equals(model)){
				m.resetCode();
				break;
			}		
		}
	}

	/**
	 * Save the local code in the UIModel file
	 * @author jvanhec
	 * @param model
	 */
	public void saveUIModelCode(UIModel model){
		for (UIModel m : availableModels){
			if (m.equals(model)){
				m.save();
				break;
			}		
		}
	}
}
