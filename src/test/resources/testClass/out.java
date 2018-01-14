import static java.util.Arrays.asList;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestClass {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestClass.class);

    public void print(StringBuilder pString) {
        System.out.println(pString.append("Something").append(" ").append("Else"));
    }

    public void print(String... pStrings) {
        print(asList(pStrings));
    }

    /*
     * TODO: I'am very old and complex but might be useful again - that's why I'm commented out
    public void oldComplexCode(InnerInner pParam) {
        print(pParam);
    }*/
    public void print(InnerInner pParam) {
        print(pParam.someField);
    }

    public void print(Collection<String> pStrings) {
        System.out.println(pStrings);
    }

    private static class Inner implements Serializable {

        private static final long serialVersionUID = 1L;
    }

    private static class InnerInner extends Inner {

        private static final long serialVersionUID = 1L;

        private List<String> someField = asList("I", "am", "a", "field!");
    }
}
