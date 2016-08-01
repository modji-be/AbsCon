package be.modji.test.qtaste.addon.abscon.type;

import java.util.LinkedList;
import java.util.List;
/**
 * This class represent a python class
 * @author jvanhec
 *
 */
public class PythonClass {
	private String className;
	private List<PythonMethod> methods;
	private PythonConstructor constructor;
	
	/**
	 * Constructor
	 * @author jvanhec
	 * @param className
	 */
	public PythonClass(String className){
		this.className = className;
		this.methods = new LinkedList<PythonMethod>();
		constructor = null;
	}
	
	/**
	 * @author jvanhec
	 * @return the name of this class
	 */
	public String getName(){
		return className;
	}
	
	/**
	 * add a method to the class
	 * @author jvanhec
	 * @param method
	 */
	public void addMethod(PythonMethod method){
		methods.add(method);
	}
	
	/**
	 * Define the constructor
	 * @author jvanhec
	 * @param constructor
	 */
	public void setConstructor(PythonConstructor constructor){
		this.constructor = constructor;
	}
	
	/**
	 * @author jvanhec
	 * @return a copy of this constructor
	 */
	public PythonConstructor getConstructor(){
		return (PythonConstructor)this.constructor.copy();
	}
	
	/**
	 * @author jvanhec
	 * @return a copy of the methods of this class
	 */
	public List<PythonMethod> getMethods(){
		List<PythonMethod> copies = new LinkedList<PythonMethod>();
		for (PythonMethod p : methods){
			copies.add(p.copy());
		}
		return copies;
	}
	
	/**
	 * Produce a copy of this
	 * @author jvanhec
	 * @return a copy of this
	 */
	public PythonClass copy(){
		PythonClass copy = new PythonClass(this.className);
		copy.constructor = this.constructor.copy();
		for(PythonMethod p : methods){
			copy.addMethod(p.copy());
		}
		return copy;
	}
	
	/**
	 * Produce a summary of this
	 * @author jvanhec
	 * @return a formatted string
	 */
	public String print(){
		String output = "";
		output += ("=>" + className + "\n");
		output += ("    =o=>" + constructor.getName()+ "\n");
		for(PythonParameter p : constructor.getParameters()){
			output += ("            ==o==>" + p.getName()+ "\n");	
		}
		for(PythonMethod m : methods){
			output += ("    ===>" + m.getName()+ "\n");	
			for(PythonParameter p : m.getParameters()){
				output += ("            =====>" + p.getName()+ "\n");	
			}
		}
		return output;
	}

}
