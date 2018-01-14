import java.nio.Buffer;
import java.sql.Date;

public class ClassWithBufferAsInlineInstance {

    private final String name;
    
    public ClassWithBufferAsInlineInstance() {
        name = "MyName2";
    }
    
    public void print() {
        System.out.println(new StringBuffer(name).toString());
    }
    
}
