import java.io.FileNotFoundException;

import introduceField.custom.Logger;
import introduceField.custom.MessageLogger;

public class MappedMethodCallClass {
	
	private static final Logger logger = MessageLogger.instance();

	public void print() {
		try {
			throw new FileNotFoundException();
		} catch (FileNotFoundException e) {
			logger.logAsInternalException("Message", e);
		}
	}
	
}
