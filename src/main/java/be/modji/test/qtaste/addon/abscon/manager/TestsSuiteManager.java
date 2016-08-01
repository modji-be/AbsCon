package be.modji.test.qtaste.addon.abscon.manager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.qspin.qtaste.util.Environment;

import be.modji.test.qtaste.addon.abscon.Utils;
import be.modji.test.qtaste.addon.abscon.exception.ConcretizerCreateFileException;
import be.modji.test.qtaste.addon.abscon.exception.ConcretizerCreateFolderException;
import be.modji.test.qtaste.addon.abscon.exception.ConcretizerRenameFolderException;
import be.modji.test.qtaste.addon.abscon.type.AbstractTest;
import be.modji.test.qtaste.addon.abscon.type.AbstractTestOperation;
import be.modji.test.qtaste.addon.abscon.type.UIModel;

/**
 * This class manages the concrete tests suite to generate
 * @author jvanhec
 */
public class TestsSuiteManager {
	private String serieName, dataPath, operationsPath,mappingPath;
	static private TestsSuiteManager instance;

	/**
	 * Private constructor
	 * @author jvanhec
	 */
	private TestsSuiteManager(){
		//init var
	}

	/**
	 * Return a singleton of this class
	 * @author jvanhec
	 * @return a singleton
	 */
	static public TestsSuiteManager getSingleton(){
		if (instance == null){
			instance = new TestsSuiteManager();
		}
		return instance;
	}

	/**
	 * Set the name for this test serie
	 * @author jvanhec
	 * @param name
	 */
	public void setName(String name){
		this.serieName = name;
	}

	/**
	 * @author jvanhec
	 * @return the name of this tests serie
	 */
	public String getName(){
		return this.serieName;
	}

	/**
	 * Import concretes tests into the addon IHM
	 * @author jvanhec
	 * @param path
	 * @return 1 if OK, else 0
	 */
	public int open(String path){
		File inputFile;
		DocumentBuilderFactory dbFactory;
		DocumentBuilder dBuilder;
		Document doc;
		NodeList nList;

		try{
			inputFile = new File(path);
			dbFactory = DocumentBuilderFactory.newInstance();
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			//parse CTAs
			int res = AbstractTestsManager.getSingleton().parseAbstractTests(path);
			if (res != 1){
				System.out.println("Parsing of abstract tests has exit with error code " + res);
				return 0;
			}
			
			//parse name
			nList = doc.getElementsByTagName("realisation");
			if (nList.getLength() <= 0 ){
				System.out.println("Error while parsing " + path + ". \n Didn't find any <realisation></realisation> balises");
			}else{		
				if (nList.item(0).getAttributes().getNamedItem("id") != null)
					this.serieName = nList.item(0).getAttributes().getNamedItem("id").getNodeValue();
			}
			
			//parse metamodels
			nList = doc.getElementsByTagName("uimodel");
			UIModelsManager.getSingleton().refreshAvailableUIModels();
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				if (nNode.getNodeType() != Node.ELEMENT_NODE)
					continue;

				if( nNode.getTextContent() != null && !nNode.getTextContent().isEmpty()){
					UIModelsManager.getSingleton().selectUIModel(nNode.getTextContent().trim(), true);
				}

			}
			
			//parse guimapping
		    nList = doc.getElementsByTagName("uimapping");
			if (nList.getLength() <= 0 || nList.item(0).getTextContent() == null){
				System.out.println("Error while parsing " + path + ". \n Didn't find any <uimapping></uimapping> balises or its content is empty");
			}else{		
				res = UIMappingsManager.getSingleton().parseUIMapping(nList.item(0).getTextContent().trim());
				if (res != 1){
					System.out.println("Parsing of UI mappings has exit with error code " + res);
					return 0;
				}
			}

			//parse operations
			nList = doc.getElementsByTagName("operations");
			if (nList.getLength() <= 0 || nList.item(0).getTextContent() == null){
				System.out.println("Error while parsing " + path + ". \n Didn't find any <operations></operations> balises or its content is empty");
			}else{
				res = OperationsManager.getSingleton().parseAbstractActions(nList.item(0).getTextContent().trim());
				if (res != 1){
					System.out.println("Parsing of actions has exit with error code " + res);
					return 0;
				}
				
				res = OperationsManager.getSingleton().parseAbstractAsserts(nList.item(0).getTextContent().trim());
				if (res != 1){
					System.out.println("Parsing of asserts has exit with error code " + res);
					return 0;
				}
			}

			//parse data
			nList = doc.getElementsByTagName("datas");
			if (nList.getLength() <= 0 || nList.item(0).getTextContent() == null){
				System.out.println("Error while parsing " + path + ". \n Didn't find any <datas></datas> balises or its content is empty");
			}else{
				res = DatasManager.getSingleton().parseTestDatas(nList.item(0).getTextContent().trim());
				if (res != 1){
					System.out.println("Parsing of datas has exit with error code " + res);
					return 0;
				}
			}

			RefreshManager.getSingleton().refreshRegisteredElements(RefreshManager.RefreshPart.IMPORT);

		}catch (ParserConfigurationException e) {
			System.out.println("Error while parsing CTAR");
			//e.printStackTrace();
			return 0;
		} catch (SAXException e) {
			System.out.println("Error while parsing CTAR");
			//e.printStackTrace();
			return 0;
		} catch (IOException e) {
			System.out.println("Error while parsing CTAR");
			//e.printStackTrace();
			return 0;
		} catch (Exception e) {
			System.out.println("Error while parsing CTAR");
			//e.printStackTrace();
			return 0;
		}
		
		return 1;
	}


	/**
	 * Build the tests serie
	 * @author jvanhec
	 * @throws ConcretizerCreateFileException
	 * @throws ConcretizerCreateFolderException
	 * @throws ConcretizerRenameFolderException
	 */
	public void build() throws ConcretizerCreateFileException, ConcretizerCreateFolderException, ConcretizerRenameFolderException{

		//refresh all for saving current text in edition
		RefreshManager.getSingleton().refreshRegisteredElements(RefreshManager.RefreshPart.UIMODEL);
		RefreshManager.getSingleton().refreshRegisteredElements(RefreshManager.RefreshPart.UIMAPPING);
		RefreshManager.getSingleton().refreshRegisteredElements(RefreshManager.RefreshPart.ABSTRACTTESTFILE);
		RefreshManager.getSingleton().refreshRegisteredElements(RefreshManager.RefreshPart.TESTDATA);


		//create destination directory and rename the existing one with the same name
		File newdir = new File(Environment.getEnvironment().getMainFrame().getTestCasePanel().getTestSuiteDirectory() + "\\" + serieName);
		if (newdir.exists()) {
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
			File oldName = newdir;
			File newName = new File(newdir.getAbsolutePath() + timeStamp);
			boolean isFileRenamed = oldName.renameTo(newName);
			if(!isFileRenamed)
				throw new ConcretizerRenameFolderException(oldName.getAbsolutePath());
		}
		try{
			newdir.mkdir();
		}
		catch(Exception e){
			throw new ConcretizerCreateFolderException(newdir.getAbsolutePath());
		}

		//pythonlib directory
		newdir = new File(Environment.getEnvironment().getMainFrame().getTestCasePanel().getTestSuiteDirectory() + "\\" + serieName + "\\pythonlib");
		try{
			newdir.mkdir();
		}
		catch(Exception e){
			throw new ConcretizerCreateFolderException(newdir.getAbsolutePath());
		}

		//metamodels copy
		for (UIModel m : UIModelsManager.getSingleton().getSelectedUIModels()){
			String dest = Environment.getEnvironment().getMainFrame().getTestCasePanel().getTestSuiteDirectory() + "\\" + serieName + "\\pythonlib\\";
			dest+= Utils.metamodelPrefix + m.getName() + ".py";

			try{
				Files.copy(new File(m.getPath()).toPath(), new File(dest).toPath(), StandardCopyOption.REPLACE_EXISTING);
			}catch (Exception e){
				throw new ConcretizerCreateFileException(dest);
			}
		}

		//GUI mapping file
		File mapping = new File(Environment.getEnvironment().getMainFrame().getTestCasePanel().getTestSuiteDirectory() + "\\" + serieName + "\\pythonlib\\" + Utils.guiMappingLibName + ".py");
		mappingPath = mapping.getAbsolutePath();
		try {
			FileWriter f = new FileWriter(mapping,false);
			f.write(UIMappingsManager.getSingleton().getPythonCode());
			f.close();
		} catch (Exception e) {
			throw new ConcretizerCreateFileException(mapping.getAbsolutePath());
		} 


		//operations file
		File operations = new File(Environment.getEnvironment().getMainFrame().getTestCasePanel().getTestSuiteDirectory() + "\\" + serieName + "\\pythonlib\\" + Utils.operationLibName + ".py");
		operationsPath = operations.getAbsolutePath();
		try {
			FileWriter f = new FileWriter(operations,false);
			f.write(OperationsManager.getSingleton().getPythonCode());
			f.close();
		} catch (Exception e) {
			throw new ConcretizerCreateFileException(operations.getAbsolutePath());
		} 


		//tests directory / files
		for (int i=0; i< AbstractTestsManager.getSingleton().getTestsCount(); i++){
			String folderName = "test_" + String.format("%06d", i);
			newdir = new File(Environment.getEnvironment().getMainFrame().getTestCasePanel().getTestSuiteDirectory() + "\\" + serieName + "\\" + folderName);
			try{
				newdir.mkdir();
			}
			catch(Exception e){
				throw new ConcretizerCreateFolderException(newdir.getAbsolutePath());
			}			

			//TestScript.py
			File testScript = new File(Environment.getEnvironment().getMainFrame().getTestCasePanel().getTestSuiteDirectory() + "\\" + serieName + "\\" + folderName + "\\TestScript.py");
			try {
				FileWriter f = new FileWriter(testScript,false);
				f.write(AbstractTestsManager.getSingleton().getPythonCode(i));
				f.close();
			} catch (Exception e) {
				throw new ConcretizerCreateFileException(testScript.getAbsolutePath());
			} 

			//TestData.csv
			File testdata = new File(Environment.getEnvironment().getMainFrame().getTestCasePanel().getTestSuiteDirectory() + "\\" + serieName + "\\" + folderName + "\\TestData.csv");
			if (dataPath == null) dataPath = testdata.getAbsolutePath();
			try {
				FileWriter f = new FileWriter(testdata,false);
				f.write(DatasManager.getSingleton().getCsvCode());
				f.close();
			} catch (Exception e){ 
				throw new ConcretizerCreateFileException(testdata.getAbsolutePath());
			} 
		}

		//configuration File
		File configCta = new File(Environment.getEnvironment().getMainFrame().getTestCasePanel().getTestSuiteDirectory() + "\\" + serieName + "\\" + Utils.configFileName);
		try {
			FileWriter f = new FileWriter(configCta,false);
			f.write(getXmlCode(mappingPath,operationsPath,dataPath));
			f.close();
		} catch (Exception e) {
			throw new ConcretizerCreateFileException(configCta.getAbsolutePath());
		} 
	}


	/**
	 * Generate the xml code that summarize the session buil
	 * @author jvanhec
	 * @param guiMappingPath
	 * @param operationPath
	 * @param dataPath
	 * @return xml string
	 */
	private String getXmlCode(String guiMappingPath, String operationPath, String dataPath){
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			//root
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("realisation");
			rootElement.setAttribute("id", TestsSuiteManager.getSingleton().getName());
			doc.appendChild(rootElement);

			Element mapping = doc.createElement("uimapping");
			mapping.appendChild(doc.createTextNode(guiMappingPath));
			rootElement.appendChild(mapping);

			Element operations = doc.createElement("operations");
			operations.appendChild(doc.createTextNode(operationPath));
			rootElement.appendChild(operations);

			Element datas = doc.createElement("datas");
			datas.appendChild(doc.createTextNode(dataPath));
			rootElement.appendChild(datas);

			for (UIModel m : UIModelsManager.getSingleton().getSelectedUIModels()){
				Element metamodel = doc.createElement("uimodel");
				metamodel.appendChild(doc.createTextNode(m.getName()));
				rootElement.appendChild(metamodel);
			}

			Element tests = doc.createElement("tests");
			rootElement.appendChild(tests);

			// operations elements
			for (AbstractTest t : AbstractTestsManager.getSingleton().getTests()){
				Element test = doc.createElement("test");
				tests.appendChild(test);

				for (AbstractTestOperation o : t.getOperations()){
					if (o.isAction()){
						Element op = doc.createElement("action");
						op.appendChild(doc.createTextNode(o.getName()));
						test.appendChild(op);	
					}
					else if (o.isAssert()){
						Element op = doc.createElement("assert");
						op.appendChild(doc.createTextNode(o.getName()));
						test.appendChild(op);	
					}
				}
			}

			// write the content into xml file
			StringWriter sw = new StringWriter();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.transform(new DOMSource(doc), new StreamResult(sw));
			return sw.toString();

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;	
	}
}

