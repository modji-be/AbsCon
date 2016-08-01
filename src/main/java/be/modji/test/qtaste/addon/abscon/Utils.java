package be.modji.test.qtaste.addon.abscon;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.lang.model.SourceVersion;
import javax.swing.ImageIcon;

public class Utils {

	static public String metamodelPrefix = "uimodel_";
	static public String operationLibName = "concretizer_Operations";
	static public String guiMappingLibName = "concretizer_UiMappings";
	static public String configFileName = "configuration.ctar";
	static public String emptyMethodCode  =  "\"\"\"\n"
		    + "@step      'Describe what does the operation do'\n"
		    + "@expected  'Describe what is the expected result'\n"
		    + "\"\"\"\n"
		    + "# Put your code here\n" 
		    + "# An assert must return 1 if the result of the assertion is OK. Else it must return 0\n"
		    + "# You can't access DIRECTLY the UI model or the API from here\n"
		    + "# You can access the UI mapping variables by just typing their names\n"
		    + "# You can import library inside a function\n"
		    + "# You can access defined data with 'testData.getValue(\"yourDataNameInUpperCase\")'"
		    + "";
	
	/**
	 * Return a picture. The picture must be in resources/pictures
	 * @author jvanhec
	 * @param pictureName
	 * @return the picture named 'pictureName'
	 */
	static public ImageIcon getPicture(String pictureName){
		BufferedImage myPicture = null;
		try {
			myPicture = ImageIO.read(Utils.class.getResource("/pictures/" + pictureName));
			return new ImageIcon(myPicture);

		} catch (IOException e) {
			e.printStackTrace();
			return new ImageIcon();
		}
	}
	
	public static boolean isAValidName(String name){
		return SourceVersion.isName(name);
		
	}
	
}
