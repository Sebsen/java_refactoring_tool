import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MappedMethodCallClass {
	
	private static final Logger logger = LoggerFactory.getLogger(MappedMethodCallClass.class);

	public void print() {
		try {
			throw new FileNotFoundException();
		} catch (FileNotFoundException e) {
			logger.error("Message", e);
		}
	}
	
}
