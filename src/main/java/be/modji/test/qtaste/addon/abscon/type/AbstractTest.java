package be.modji.test.qtaste.addon.abscon.type;

import java.util.LinkedList;
import java.util.List;

import be.modji.test.qtaste.addon.abscon.Utils;

/**
 * This class represent abstract tests found in the abstract tests file
 * @author jvanhec
 *
 */
public class AbstractTest {
	private List<AbstractTestOperation> operations;

	/**
	 * Constructor
	 * @author jvanhec
	 */
	public AbstractTest(){
		operations = new LinkedList<AbstractTestOperation>();
	}

	/**
	 * Add a new operation
	 * @author jvanhec
	 * @param operation
	 */
	public void addOperation(AbstractTestOperation operation){
		operations.add(operation);
	}	

	/**
	 * Produce a copy of all actions contained in the test
	 * @author jvanhec
	 * @return a copy of all actions
	 */
	public List<AbstractAction> getActions(){
		List<AbstractAction> actions = new LinkedList<AbstractAction>();
		for (AbstractTestOperation o : operations){
			if (o.isAction()){
				actions.add((AbstractAction) o.copy());
			}
		}
		return actions;
	}

	/**
	 * Produce a copy of all asserts contained in the test
	 * @author jvanhec
	 * @return a copy of all asserts
	 */
	public List<AbstractAssert> getAsserts(){
		List<AbstractAssert> asserts = new LinkedList<AbstractAssert>();
		for (AbstractTestOperation o : operations){
			if (o.isAssert()){
				asserts.add((AbstractAssert) o.copy());
			}
		}
		return asserts;
	}

	/**
	 * Produce a copy of all operations contained in the test
	 * @author jvanhec
	 * @return a copy of all operations
	 */
	public List<AbstractTestOperation> getOperations(){
		List<AbstractTestOperation> copies = new LinkedList<AbstractTestOperation>();
		for (AbstractTestOperation o : operations){
			copies.add(o.copy());
		}
		return copies;
	}

	/**
	 * Produce a formatted string of the content of this test
	 * @author jvanhec
	 * @return a formatted string
	 */
	public String print(){
		String output = "-------------TEST------------\n";
		for (AbstractTestOperation o : operations){
			output += o.print() + "\n";
		}
		return output;
	}

	/**
	 * Produce a copy of this
	 * @author jvanhec
	 * @return a copy of this
	 */
	public AbstractTest copy(){
		AbstractTest copy = new AbstractTest();
		for (AbstractTestOperation o : operations){
			copy.addOperation(o.copy());
		}
		return copy;
	}

	/**
	 * Produce the python code that execute operations
	 * @return the pyton code
	 */
	public String getPythonCode(){
		String output = "";
		for (AbstractTestOperation operation : operations){
			if (operation.isAction()){
				output += "doStep(" +  Utils.operationLibName + "." + operation.getName() + ")\n";
			}
			else if (operation.isAssert()){
				output += "doAssert(" +  Utils.operationLibName + "." + operation.getName() + ", \"assertion "+ operation.getName() + " has failed\")\n";
			}
		}
		return output;
	}
}
