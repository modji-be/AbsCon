package be.modji.test.qtaste.addon.abscon.manager;

import be.modji.test.qtaste.addon.abscon.ui.RefreshableElement;

/**
 * Interface of for a class that manage the refresh of "RefreshableElements"
 * @author jvanhec
 *
 */
public interface ElementRefreshener {
	
	static public enum RefreshPart {
		  UIMODEL,
		  UIMAPPING,
		  ABSTRACTTESTFILE,
		  TESTDATA,
		  IMPORT;	
	}
	
	/**
	 * This method is supposed to be called when we want to refresh 
	 * a specific category of RefreshableElements
	 * @author jvanhec
	 * @param category
	 */
	public void refreshRegisteredElements( RefreshPart category);
	
	/**
	 * This method is supposed to be called for registering a new element in a given category
	 * @author jvanhec
	 */
	public void register(RefreshableElement element, RefreshPart category);
}
