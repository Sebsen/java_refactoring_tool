import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestTarget {
	
	private static Logger logger = LoggerFactory.getLogger(TestTarget.class);

	public void print() {
		try {
			throw new FileNotFoundException();
		} catch (FileNotFoundException e) {
			logger.error("", e);
		}
	}
	
}
