
public class ClassWithBuilderAsInlineInstance {

    private final String name;
    
    public ClassWithBuilderAsInlineInstance() {
        name = "MyName2";
    }
    
    public void print() {
        
        System.out.println(new StringBuilder(name).toString());
    }
    
}
