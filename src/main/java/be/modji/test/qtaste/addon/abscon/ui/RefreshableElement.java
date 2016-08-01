package be.modji.test.qtaste.addon.abscon.ui;

import be.modji.test.qtaste.addon.abscon.manager.RefreshManager;

/**
 * Interface of element that needs to be refreshed
 * This class need a "ElementRefreshener" on which it can register to
 * @author jvanhec
 *
 */
public interface RefreshableElement {

	/**
	 * This method is supposed to be called by a ElementRefreshener when a specific event occurs
	 * @author jvanhec
	 * @param category
	 */
	public void refresh(RefreshManager.RefreshPart category);
	
	/**
	 * This method is supposed to be used to register the class to the ElementRefreshener
	 * @author jvanhec
	 */
	public void register();
}
