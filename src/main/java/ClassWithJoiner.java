import java.util.StringJoiner;

public class ClassWithJoiner {

    private final StringJoiner j;
    private final String name;
    
    public ClassWithJoiner() {
        j = new StringJoiner(", ");
        name = "MyName";
    }
    
    public void print() {
        j.add(name);
        System.out.println(j.toString());
    }

    public void print(final String pStringToAdd) {
        j.add(pStringToAdd);
        System.out.println(j.toString());
    }

    public void print(final StringJoiner pStringToAdd) {
        j.merge(pStringToAdd);
        System.out.println(j.toString());
    }
    
}
