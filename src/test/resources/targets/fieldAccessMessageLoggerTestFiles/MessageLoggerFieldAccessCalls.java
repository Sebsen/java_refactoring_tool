import complexClass.custom.MessageLogger;

public class MessageLoggerFieldAccessCalls {

    private final String name;
    
    public MessageLoggerFieldAccessCalls() {
        name = "MyName2";
    }
    
    public void print() {
        System.out.println(new StringBuilder(name).toString());
    }
    
}
