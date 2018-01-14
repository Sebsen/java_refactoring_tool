
public class ClassWithBuilderAsField {

    private final StringBuilder s;
    private final String name;
    
    public ClassWithBuilderAsField() {
        s = new StringBuilder();
        name = "MyName";
    }
    
    public void print() {
        s.append(name);
        System.out.println(s.toString());
    }
    
}
