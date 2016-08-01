package be.modji.test.qtaste.addon.abscon.type;

/**
 * this class represent a python constructor
 * @author jvanhec
 *
 */
public class PythonConstructor extends PythonMethod{
	
	/**
	 * Constructor
	 * @author jvanhec
	 * @param methodName
	 */
	public PythonConstructor(String methodName){
		super(methodName);
	}
	
	/**
	 * Produce a copy of this
	 * @author jvanhec
	 * @return a copy of this
	 */
	public PythonConstructor copy(){
		PythonConstructor copy = new PythonConstructor(this.methodName);
		
		for(PythonParameter param : parameters){
			copy.addParameter(param.copy());
		}
		copy.code = this.code;
		return copy;
	}

}
