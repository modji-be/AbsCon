package be.modji.test.qtaste.addon.abscon.manager;

import java.util.LinkedList;
import java.util.List;

import be.modji.test.qtaste.addon.abscon.ui.RefreshableElement;

/**
 * This class manages refresh of the addon GUI
 * @author jvanhec
 */
public class RefreshManager implements ElementRefreshener{

	static private RefreshManager instance;
	private List<RefreshableElement> elementsToRefreshMetaModel;
	private List<RefreshableElement> elementsToRefreshAbstractTestFile;
	private List<RefreshableElement> elementsToRefreshGuiMapping;
	private List<RefreshableElement> elementsToRefreshTestData;
	private List<RefreshableElement> elementsToRefreshTestImport;

	/**
	 * Private constructor
	 * @author jvanhec
	 */
	private RefreshManager(){
		elementsToRefreshMetaModel = new LinkedList<RefreshableElement>();	
		elementsToRefreshAbstractTestFile = new LinkedList<RefreshableElement>();	
		elementsToRefreshGuiMapping = new LinkedList<RefreshableElement>();	
		elementsToRefreshTestData = new LinkedList<RefreshableElement>();	
		elementsToRefreshTestImport = new LinkedList<RefreshableElement>();
	}

	/**
	 * Return a singleton of this class
	 * @author jvanhec
	 * @return a singleton
	 */
	static public RefreshManager getSingleton(){
		if (instance == null){
			instance = new RefreshManager();
		}
		return instance;
	}


	/**
	 * Refresh elements that are in the given category
	 * @author jvanhec
	 * @param category
	 */
	@Override
	public void refreshRegisteredElements(RefreshPart category) {
		List<RefreshableElement> elements = new LinkedList<RefreshableElement>();	
		
		switch (category){
		case  UIMODEL:
			System.out.println("Refresh registered class for \"metamodel\"");
			elements = elementsToRefreshMetaModel;
			break;
		case  ABSTRACTTESTFILE:
			System.out.println("Refresh registered class for \"abstract test files\"");
			elements = elementsToRefreshAbstractTestFile;
			break;
		case  UIMAPPING:
			System.out.println("Refresh registered class for \"GUI mapping\"");
			elements = elementsToRefreshGuiMapping;
			break;
		case  TESTDATA:
			System.out.println("Refresh registered class for \"Test data\"");
			elements = elementsToRefreshTestData;
			break;
		case  IMPORT:
			System.out.println("Refresh registered class for \"Import\"");
			elements = elementsToRefreshTestImport;
			break;
		}
		
		for (RefreshableElement e : elements){
			e.refresh(category);
		}		
	}


	/**
	 * Register an new element to a given category
	 * @author jvanhec
	 * @param element is the element to refresh
	 * @param category
	 */

	@Override
	public void register(RefreshableElement element,RefreshPart category) {
		switch (category){
		case  UIMODEL:
			elementsToRefreshMetaModel.add(element);
			break;
		case  ABSTRACTTESTFILE:
			elementsToRefreshAbstractTestFile.add(element);
			break;
		case  UIMAPPING:
			elementsToRefreshGuiMapping.add(element);
			break;
		case  TESTDATA:
			elementsToRefreshTestData.add(element);
			break;
		case  IMPORT:
			elementsToRefreshTestImport.add(element);
			break;
		}
	}
}
