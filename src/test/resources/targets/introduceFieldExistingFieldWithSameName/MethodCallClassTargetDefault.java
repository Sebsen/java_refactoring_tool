import java.io.FileNotFoundException;

import introduceField.custom.Logger;
import introduceField.custom.MessageLogger;

public class MethodCallClass {
	
	private static final StringBuilder logger = new StringBuilder();
	
	private static final Logger JavaRefactoringToolCreatedField = MessageLogger.instance();

	public void print() {
		try {
			throw new FileNotFoundException();
		} catch (FileNotFoundException e) {
			JavaRefactoringToolCreatedField.logAsInternalException(e);
		}
	}
	
}
