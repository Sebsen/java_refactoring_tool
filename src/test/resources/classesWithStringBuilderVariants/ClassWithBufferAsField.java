
public class ClassWithBufferAsField {

    private final StringBuffer s;
    private final String name;
    
    public ClassWithBufferAsField() {
        s = new StringBuffer();
        name = "MyName";
    }
    
    public void print() {
        s.append(name);
        System.out.println(s.toString());
    }
    
}
