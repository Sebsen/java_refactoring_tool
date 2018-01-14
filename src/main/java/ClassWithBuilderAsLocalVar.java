
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
