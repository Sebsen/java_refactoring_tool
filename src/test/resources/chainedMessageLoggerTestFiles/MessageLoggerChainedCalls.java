import complexClass.custom.MessageLogger;

public class MessageLoggerChainedCalls {

    private final String name;
    
    public MessageLoggerChainedCalls() {
        name = "MyName2";
    }
    
    public void print() {
        MessageLogger.instance().debug("Error", new Exception("Hi"));
        System.out.println(new StringBuilder(name).toString());
    }
    
}
