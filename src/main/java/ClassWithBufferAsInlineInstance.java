import java.nio.Buffer;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;

public class ClassWithBufferAsInlineInstance {

    private final String name;
    ByteBuffer b;
    Buffer c;
    BufferOverflowException box;
    BufferUnderflowException bux;
    ByteOrder bo;
    CharBuffer cb;
    DoubleBuffer db;
    

    public ClassWithBufferAsInlineInstance() {
        name = "MyName2";
    }

    public void print() {
        System.out.println(new StringBuffer(name).toString());
    }

}
