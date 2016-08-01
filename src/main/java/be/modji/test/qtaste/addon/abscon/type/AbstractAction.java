package be.modji.test.qtaste.addon.abscon.type;

import java.util.List;

/**
 * Represent an abstract action contained in an abstract test
 * @author jvanhec
 *
 */
public class AbstractAction extends AbstractTestOperation{
	
	/**
	 * Constructor
	 * @author jvanhec
	 */
	public AbstractAction(String name){
		super(name);
	}
	
	/**
	 * Copy this action
	 * @author jvanhec
	 * @return a copy of this
	 */
	@Override
	public AbstractAction copy(){
		AbstractAction copy = new AbstractAction(this.operationName);
		return copy;
	}

	/**
	 * Produce a string with the content of this
	 * @author jvanhec
	 * @return a formatted string
	 */
	@Override
	public String print(){
		return "Abstract action : " + this.operationName;
	}
	
	/**
	 * Add the given element to the given list only if it doens't contain it yet
	 * @author jvanhec
	 * @param list
	 * @param element
	 * @return the updated list
	 */
	static public List<AbstractAction> addElementIfNotPresent(List<AbstractAction> list, AbstractAction element){
		for(AbstractAction e : list){
			if (e.equals(element))
				return list;
		}
		
		list.add(element);
		return list;	
	}
}
