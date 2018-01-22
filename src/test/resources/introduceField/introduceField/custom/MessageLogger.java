package introduceField.custom;

public class MessageLogger {

    public static Logger instance() {
        return new Logger();
    }

}
