
public class ClassWithBuilderAsParameter {

    private final String name;

    public ClassWithBuilderAsParameter() {
        name = "MyName2";
    }

    public void print(final StringBuilder pMyStringCreator) {
        System.out.println(pMyStringCreator.append(name).toString());
    }

}
