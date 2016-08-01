package be.modji.test.qtaste.addon.abscon.manager;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import be.modji.test.qtaste.addon.abscon.Utils;
import be.modji.test.qtaste.addon.abscon.type.AbstractAction;
import be.modji.test.qtaste.addon.abscon.type.AbstractAssert;
import be.modji.test.qtaste.addon.abscon.type.AbstractTest;


/**
 * This class manages abstract tests of the common context
 * @author jvanhec
 */
public class AbstractTestsManager {
	private List<AbstractTest> tests;
	static private AbstractTestsManager instance;

	/**
	 * Private constructor
	 * @author jvanhec
	 */
	private AbstractTestsManager(){
		tests = new LinkedList<AbstractTest>();
	}

	/**
	 * Return a singleton of this class
	 * @author jvanhec
	 * @return a singleton
	 */ 
	static public AbstractTestsManager getSingleton(){
		if (instance == null){
			instance = new AbstractTestsManager();
		}
		return instance;
	}

	/**
	 * Produce a formatted string that contains all abstract tests
	 * @author jvanhec
	 * @return the formatted string
	 */
	public String print(){
		String output = "";
		for (AbstractTest test : tests){
			output+= test.print() + "\n";
		}
		return output;
	}

	
	/**
	 * @author jvanhec
	 * @return a copy of all abstract tests
	 */
	public List<AbstractTest> getTests(){
		List<AbstractTest> copies = new LinkedList<AbstractTest>();

		for (AbstractTest test : tests){
			copies.add(test.copy());
		}
		return copies;
	}
	
	/**
	 * @author jvanhec
	 * @return a copy of a specific test
	 */
	public AbstractTest getTest(int index){
		if (index >= tests.size() || index <0)
			return null;
		return tests.get(index).copy();
	}

	/**
	 * @author jvanhec
	 * @return the amount of abstract tests
	 */
	public int getTestsCount(){
		return tests.size();
	}

	/**
	 * Produce a list with a copy of each unique Action. Each element 
	 * of this list will be present ONE time maximum
	 * @author jvanhec
	 * @return a list with a copy of every unique 'Action'
	 */
	private List<AbstractAction> getEveryActions(){
		List<AbstractAction> actions = new LinkedList<AbstractAction>();

		for (AbstractTest test : tests){
			for (AbstractAction action : test.getActions()){
				AbstractAction.addElementIfNotPresent(actions, action.copy());
			}
		}
		return actions;
	}

	/**
	 * Produce a list with a copy of each unique Assert. Each element 
	 * of this list will be present ONE time maximum
	 * @author jvanhec
	 * @return a list with a copy of every unique 'Assert'
	 */
	private List<AbstractAssert> getEveryAsserts(){
		List<AbstractAssert> asserts = new LinkedList<AbstractAssert>();

		for (AbstractTest test : tests){
			for (AbstractAssert a : test.getAsserts()){
				AbstractAssert.addElementIfNotPresent(asserts, a.copy());
			}
		}
		return asserts;
	}

	/**
	 * Parse the file mentioned. It must be an XML file following the structure:
	 * <tests><test><action>...</action><assert>...</assert></test></tests>
	 * It will add all parsed tests to the global tests list "tests"
	 * @author jvanhec
	 * @param path is the absolute path to the xml file
	 * @return an integer:
	 *  1 if the parsing is a success
	 *  0 if there is an unexpected error
	 *  -1 if there is an XML parsing error
	 *  -2 if the path doesn't specify a file
	 *  -3 if the XML file doesn't follow the expected structure
	 *  -4 if there is an error in operation naming
	 */
	public int parseAbstractTests(String path){
		tests = new LinkedList<AbstractTest>();
		File inputFile;
		DocumentBuilderFactory dbFactory;
		DocumentBuilder dBuilder;
		Document doc;

		//check existence of file
		if (! new File(path).isFile())
			return -2;

		try {	
			inputFile = new File(path);
			dbFactory = DocumentBuilderFactory.newInstance();
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			//check existence of "test" balise(s)
			NodeList nList = doc.getElementsByTagName("test");
			if (nList.getLength() <= 0){
				System.out.println("Error while parsing " + path + ". \n Didn't find any <test></test> balises");
				return -3;
			}

			//parse the "test" balises
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				AbstractTest atest = new AbstractTest();
				if (nNode.hasChildNodes()){
					for (int j = 0; j < nNode.getChildNodes().getLength(); j++) {
						Node nChild = nNode.getChildNodes().item(j);
						if (nChild.getNodeType() != Node.ELEMENT_NODE)
							continue;

						//if xml node is <assert>
						if (nChild.getNodeName() != null && nChild.getNodeName().toUpperCase().equals("ASSERT")){
							if( nChild.getTextContent() != null && !nChild.getTextContent().isEmpty()){
								if (!Utils.isAValidName(nChild.getTextContent().trim())){
									System.out.println("Cannot add abstract action '" + nChild.getTextContent().trim() + "' because its name must follow the java naming rules for methods");
									return -4;
								}
								atest.addOperation(new AbstractAssert(nChild.getTextContent().trim()));
							}
						}
						//if xml node is <action>
						else if (nChild.getNodeName() != null && nChild.getNodeName().toUpperCase().equals("ACTION")){
							if( nChild.getTextContent() != null && !nChild.getTextContent().isEmpty()){
								if (!Utils.isAValidName(nChild.getTextContent().trim())){
									System.out.println("Cannot add abstract assert '" + nChild.getTextContent().trim() + "' because its name must follow the java naming rules for methods");
									return -4;
								}
								atest.addOperation(new AbstractAction(nChild.getTextContent().trim()));
							}
						}
					}
				}
				tests.add(atest);
			}

		} catch (SAXParseException e) {
			e.printStackTrace();
			return -1;

		} catch (SAXException e) {
			e.printStackTrace();
			return -1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0; 
		}

		defineConcreteOperations();
		return 1;
	}

	/**
	 * Update the OperationsManager with all actions that are in the global
	 * list "tests"
	 * @author jvanhec
	 */
	private void defineConcreteOperations(){
		OperationsManager.getSingleton().defineActions(getEveryActions());
		OperationsManager.getSingleton().defineAsserts(getEveryAsserts());
	}

	/**
	 * Return the python code for the execution of the test contained in the global
	 * list "tests" at the index "testIndex"
	 * @author jvanhec
	 * @param testIndex
	 * @return a String that contains the python code
	 */
	public String getPythonCode(int testIndex){
		if (testIndex < 0 || testIndex >= tests.size())
			return "";

		//Write the begin of the python file
		String output = ""+
				"# encoding= utf-8\n"+
				"#Imports\n"+
				"from qtaste import *\n"+
				"import " + Utils.operationLibName + "\n\n"+
				"#Assert\n"+
				"def doAssert(method, message):\n"+
				"\tres = method()\n"
				+"\tif res == 0:\n"
				+ "\t\traise QTasteTestFailException(message)\n";

		//Write the python code for the test		
		output += "\n\n#Steps\n" + tests.get(testIndex).getPythonCode(); 

		return output;

	}
}

