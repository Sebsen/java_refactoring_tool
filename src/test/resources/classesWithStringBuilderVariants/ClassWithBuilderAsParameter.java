import java.nio.Buffer;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.io.DoubleBuffer;

public class ClassWithBuilderAsParameter {

    private final String name;

    public ClassWithBuilderAsParameter() {
        name = "MyName2";
    }

    public void print(final StringBuilder pMyStringCreator) {
        System.out.println(pMyStringCreator.append(name).toString());
    }

}
