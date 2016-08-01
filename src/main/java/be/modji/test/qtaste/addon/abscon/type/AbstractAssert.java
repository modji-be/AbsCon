package be.modji.test.qtaste.addon.abscon.type;

import java.util.List;

/**
 * Represent an abstract assert contained in an abstract test
 * @author jvanhec
 *
 */
public class AbstractAssert extends AbstractTestOperation{

	/**
	 * Constructor
	 * @author jvanhec
	 * @param name
	 */
	public AbstractAssert(String name){
		super(name);
	}
	
	/**
	 * Copy this assert
	 * @author jvanhec
	 * @return a copy of this
	 */
	@Override
	public AbstractAssert copy(){
		AbstractAssert copy = new AbstractAssert(this.operationName);
		return copy;
	}
	
	/**
	 * Produce a string with the content of this
	 * @author jvanhec
	 * @return a formatted string
	 */
	@Override
	public String print(){
		return "Abstract assert : " + this.operationName;
	}

	/**
	 * Add the given element to the given list only if it doens't contain it yet
	 * @author jvanhec
	 * @param list
	 * @param element
	 * @return the updated list
	 */
	static public List<AbstractAssert> addElementIfNotPresent(List<AbstractAssert> list, AbstractAssert element){
		for(AbstractAssert e : list){
			if (e.equals(element))
				return list;
		}
		
		list.add(element);
		return list;	
	}
	
}
