import java.io.FileNotFoundException;

import complexClass.custom.MessageLogger;

public class Test {

	public void print() {
		try {
			throw new FileNotFoundException();
		} catch (FileNotFoundException e) {
			MessageLogger.instance().logAsInternalException(e);
		}
	}
	
}
