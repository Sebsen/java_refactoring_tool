
public class ClassWithBufferAsParameter {

    private final String name;
    
    public ClassWithBufferAsParameter() {
        name = "MyName2";
    }
    
    public void print(final StringBuffer pMyStringCreator) {
        System.out.println(pMyStringCreator.append(name).toString());
    }
    
}
