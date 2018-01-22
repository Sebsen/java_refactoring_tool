import java.io.FileNotFoundException;

import introduceField.custom.Logger;
import introduceField.custom.MessageLogger;

public class MethodCallClassTarget {
	
	private static Logger logger = MessageLogger.instance();

	public void print() {
		try {
			throw new FileNotFoundException();
		} catch (FileNotFoundException e) {
			logger.logAsInternalException(e);
		}
	}
	
}
