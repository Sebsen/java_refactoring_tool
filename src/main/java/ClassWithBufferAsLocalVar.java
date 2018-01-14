
public class ClassWithBufferAsLocalVar {

    private final String name;
    
    public ClassWithBufferAsLocalVar() {
        name = "MyName2";
    }
    
    public void print() {
        final StringBuffer myStringCreator = new StringBuffer(name);
        System.out.println(myStringCreator.toString());
    }
    
}
