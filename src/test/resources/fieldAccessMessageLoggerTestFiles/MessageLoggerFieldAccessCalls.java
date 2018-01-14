import complexClass.custom.Logger;
import complexClass.custom.MessageLogger;

public class MessageLoggerFieldAccessCalls {

    private final String name;
	private final Logger logger = MessageLogger.instance();
    
    public MessageLoggerFieldAccessCalls() {
        name = "MyName2";
    }
    
    public void print() {
        logger.debug("Error", new Exception("Hi"));
        System.out.println(new StringBuilder(name).toString());
    }
    
}
