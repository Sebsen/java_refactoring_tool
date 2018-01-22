import java.io.FileNotFoundException;

import introduceField.custom.MessageLogger;

public class MethodCallClass {

	public void print() {
		try {
			throw new FileNotFoundException();
		} catch (FileNotFoundException e) {
			MessageLogger.instance().logAsInternalException(e);
		}
	}
	
}
