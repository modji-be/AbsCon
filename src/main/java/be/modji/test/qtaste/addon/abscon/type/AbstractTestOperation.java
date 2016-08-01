package be.modji.test.qtaste.addon.abscon.type;

import java.util.List;

/**
 * Represent an abstract test operation (assert || action ) contained in an abstract test
 * @author jvanhec
 *
 */
public class AbstractTestOperation {

	protected String operationName;
	
	/**
	 * Constructor
	 * @author jvanhec
	 * @param name
	 */
	public AbstractTestOperation(String name){
		this.operationName = name;
	}
	
	/**
	 * @author jvanhec
	 * @return the name of the operation
	 */
	public String getName(){
		return this.operationName;
	}

	/**
	 * @author jvanhec
	 * @return true if this is an action
	 */
	public boolean isAction(){
		return (this instanceof AbstractAction);
	}
	
	/**
	 * @author jvanhec
	 * @return true if this is an assert
	 */
	public boolean isAssert(){
		return (this instanceof AbstractAssert);
	}
	
	/**
	 * Copy this action
	 * @author jvanhec
	 * @return a copy of this
	 */
	public AbstractTestOperation copy(){
		AbstractTestOperation copy = new AbstractTestOperation(this.operationName);
		return copy;
	}
	
	/**
	 * Produce a string with the content of this
	 * @author jvanhec
	 * @return a formatted string
	 */
	public String print(){
		return "Abstract Operation : " + this.operationName;
	}
	
	/**
	 * @author jvanhec
	 * @param operation
	 * @return true if this is equals to the given operation
	 */
	public boolean equals(AbstractTestOperation operation){
		if (this.isAction() && !operation.isAction())
			return false;
		if (this.isAssert() && !operation.isAssert())
			return false;
		if (!this.operationName.equals(operation.operationName))
			return false;
		return true;
	}
	
	/**
	 * Add the given element to the given list only if it doens't contain it yet
	 * @author jvanhec
	 * @param list
	 * @param element
	 * @return the updated list
	 */
	static public List<AbstractTestOperation> addElementIfNotPresent(List<AbstractTestOperation> list, AbstractTestOperation element){
		
		for(AbstractTestOperation e : list){
			if (e.equals(element))
				return list;
		}
		list.add(element);
		return list;	
	}

}
