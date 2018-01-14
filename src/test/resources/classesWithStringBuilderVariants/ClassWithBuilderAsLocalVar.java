import java.nio.Buffer;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;

public class ClassWithBuilderAsLocalVar {

    private final String name;
    
    public ClassWithBuilderAsLocalVar() {
        name = "MyName2";
    }
    
    public void print() {
        final StringBuilder myStringCreator = new StringBuilder(name);
        System.out.println(myStringCreator.toString());
    }
    
}
