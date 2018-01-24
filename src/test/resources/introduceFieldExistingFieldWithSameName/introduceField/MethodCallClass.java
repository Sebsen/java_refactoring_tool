import java.io.FileNotFoundException;

import introduceField.custom.MessageLogger;

public class MethodCallClass {
	
	private static final StringBuilder logger = new StringBuilder();

	public void print() {
		try {
			throw new FileNotFoundException();
		} catch (FileNotFoundException e) {
			MessageLogger.instance().logAsInternalException(e);
		}
	}
	
}
