package es.uniovi.tests.pageobjects;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.ResourceBundle;

public class PO_Properties {
	
	final static int SPANISH = 0;
	final static int ENGLISH = 1;	

	private final static String PATH = "messages";
	
	static Locale[] idioms = new Locale[] {new Locale("ES"), new Locale("EN")};

    public String getString(String prop, int locale) {
		
		ResourceBundle bundle = ResourceBundle.getBundle(PATH, idioms[locale]);
		String value = bundle.getString(prop);
		String result="";
		try {
			result = new String(value.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}
    
    public static int getSPANISH() {
		return SPANISH;
	}

	public static int getENGLISH() {
		return ENGLISH;
	}

}
