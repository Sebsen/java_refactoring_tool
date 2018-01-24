import java.io.FileNotFoundException;

import introduceField.custom.Logger;
import introduceField.custom.MessageLogger;

public class MethodCallClass {
	
	private static final StringBuilder logger = new StringBuilder();
	
	private static final Logger myLogger = MessageLogger.instance();

	public void print() {
		try {
			throw new FileNotFoundException();
		} catch (FileNotFoundException e) {
			myLogger.logAsInternalException(e);
		}
	}
	
}
